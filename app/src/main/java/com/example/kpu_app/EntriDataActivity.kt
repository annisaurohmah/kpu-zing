package com.example.kpu_app

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import android.Manifest
import android.location.Location
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.kpu_app.databinding.ActivityEntriDataBinding
import com.example.room1.database.Pemilih
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EntriDataActivity : AppCompatActivity() {
    lateinit var binding: ActivityEntriDataBinding
    private var ImageUri: Uri? = null

    //firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val pemilihCollectionRef = firestore.collection("pemilih")
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    companion object {
        private const val REQUEST_CODE_MAP = 1
        private const val REQUEST_CODE_IMAGE = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntriDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Cek dan minta izin lokasi
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            getLastLocation()
        }

        with(binding){
            textLocation.setOnClickListener {
                val intent = Intent(this@EntriDataActivity, MapsActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_MAP)
            }



            btnUpload.setOnClickListener{
                selectImage()
            }

            btnSave.setOnClickListener {
                val isGenderSelected = radioGroupGender.checkedRadioButtonId != -1

                if(nik.text!!.isEmpty() || fullname.text!!.isEmpty() ||  nohp.text!!.isEmpty() ||
                    !isGenderSelected || registerdate.text!!.isEmpty() || textLocation.text!!.isEmpty() ){
                    Toast.makeText(this@EntriDataActivity, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                } else if(ImageUri == null){
                    Toast.makeText(this@EntriDataActivity, "Please upload an image", Toast.LENGTH_SHORT).show()
                } else {
                    uploadImage()
                    startActivity(Intent(this@EntriDataActivity, HomeActivity::class.java))
                }

            }

            btnCancel.setOnClickListener {
                startActivity(Intent(this@EntriDataActivity, HomeActivity::class.java))
            }
            btnBack.setOnClickListener {
                startActivity(Intent(this@EntriDataActivity, HomeActivity::class.java))
            }


        }
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLastLocation()
        } else {
            Toast.makeText(this, "Izin lokasi diperlukan untuk mengakses fitur ini", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    // Dapatkan alamat dari koordinat
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Alamat tidak ditemukan"

                    // Tampilkan alamat di TextView `text_location`
                    with(binding) { textLocation.setText(address) }
                }
            }
        }
    }

    private fun uploadImage() {
        // Ambil nilai NIK dari input
        val nikInput = binding.nik.text.toString()

        pemilihCollectionRef.whereEqualTo("nik", nikInput).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Jika NIK ditemukan, tampilkan pesan Toast dan batalkan penyimpanan
                    Toast.makeText(this, "NIK sudah terdaftar, data tidak disimpan.", Toast.LENGTH_SHORT).show()
                } else {


                    val progressDialog = ProgressDialog(this)
                    progressDialog.setMessage("Uploading file...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()

                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val now = Date()
                    val filename = formatter.format(now) + System.currentTimeMillis()

                    val storageReference =
                        FirebaseStorage.getInstance().getReference("images/$filename")

                    // Upload the image to Firebase Storage
                    storageReference.putFile(ImageUri!!)
                        .continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            // Return the download URL of the uploaded image
                            storageReference.downloadUrl
                        }
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Get the download URL
                                val downloadUri = task.result
                                val selectedGenderId = binding.radioGroupGender.checkedRadioButtonId
                                val genderValue = if (selectedGenderId != -1) {
                                    findViewById<RadioButton>(selectedGenderId).text.toString()
                                } else {
                                    ""  // Default jika tidak ada yang dipilih
                                }


                                // Save the movie details (including image URL) to Firebase Firestore
                                val newData = Pemilih(
                                    nik = binding.nik.text.toString(),
                                    fullname = binding.fullname.text.toString(),
                                    nohp = binding.nohp.text.toString(),
                                    gender = genderValue,
                                    registerdate = binding.registerdate.text.toString(),
                                    location = binding.textLocation.text.toString(),
                                    picture = downloadUri.toString(),
                                )

                                // Save the movie to Firestore
                                addData(newData)
                                // Display a success message
                                Toast.makeText(
                                    this@EntriDataActivity,
                                    "Successfully uploaded",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Handle failures
                                Toast.makeText(
                                    this@EntriDataActivity,
                                    "Failed to upload image",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            // Dismiss the progress dialog
                            if (progressDialog.isShowing) progressDialog.dismiss()

                        }
                }
            }

    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_IMAGE -> {
                if (resultCode == RESULT_OK && data != null) {
                    ImageUri = data.data
                    binding.imageView.setImageURI(ImageUri)
                }
            }
            REQUEST_CODE_MAP -> {
                if (resultCode == RESULT_OK && data != null) {
                    val latitude = data.getDoubleExtra("selected_latitude", 0.0)
                    val longitude = data.getDoubleExtra("selected_longitude", 0.0)

                    // Gunakan Geocoder untuk mendapatkan alamat dari koordinat
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Alamat tidak ditemukan"

                    // Tampilkan alamat di TextView
                    binding.textLocation.setText(address)
                }
            }
        }
    }


    // dihandle untuk kondisi sukses, akan mereturn docRef
    private fun addData(pemilih: Pemilih) {
        pemilihCollectionRef.add(pemilih)
            .addOnSuccessListener { docRef ->
                docRef.set(pemilih)
                    .addOnFailureListener{
                        Log.d("MainActivity", "Error update movie id", it)
                    }
            }
            .addOnFailureListener{
                Log.d("MainActivity", "Error add movie", it)
            }
    }
    @Suppress("DEPRECATION")
    fun Geocoder.getAddress(
        latitude: Double,
        longitude: Double,
        addressCallback: (android.location.Address?) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Menggunakan callback untuk API 33 ke atas
            getFromLocation(latitude, longitude, 1) { addresses ->
                addressCallback(addresses.firstOrNull())
            }
        } else {
            // Menggunakan synchronous untuk API di bawah 33
            try {
                val addresses = getFromLocation(latitude, longitude, 1)
                if (addresses != null) {
                    addressCallback(addresses.firstOrNull())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                addressCallback(null) // Callback dengan nilai null jika ada error
            }
        }
    }


}