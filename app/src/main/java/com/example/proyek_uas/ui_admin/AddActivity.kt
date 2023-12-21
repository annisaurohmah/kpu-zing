package com.example.proyek_uas.ui_admin


import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.proyek_uas.Movie
import com.example.proyek_uas.databinding.ActivityAddBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    private var ImageUri: Uri? = null

    //firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val movieCollectionRef = firestore.collection("movies")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding){
            btnUpload.setOnClickListener{
                selectImage()
            }

            btnSave.setOnClickListener {
                if(txtTitle.text!!.isEmpty() || txtDesc.text!!.isEmpty() ||  txtDirector.text!!.isEmpty() ||
                    txtStar.text!!.isEmpty() || txtDuration.text!!.isEmpty() || txtRating.text!!.isEmpty() || txtWriter.text!!.isEmpty()){
                    Toast.makeText(this@AddActivity, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                } else if ((txtRating.text.toString().toFloat() !in 0.0..5.0)){
                    Toast.makeText(this@AddActivity, "Please fill with correct range in Rating", Toast.LENGTH_SHORT).show()
                } else if(ImageUri == null){
                    Toast.makeText(this@AddActivity, "Please upload an image poster", Toast.LENGTH_SHORT).show()
                } else {
                    uploadImage()
                    startActivity(Intent(this@AddActivity, ListActivity::class.java))
                }

            }

                btnCancel.setOnClickListener {
                    startActivity(Intent(this@AddActivity, ListActivity::class.java))
                }
                btnBack.setOnClickListener {
                    startActivity(Intent(this@AddActivity, ListActivity::class.java))
                }
        }
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading file...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = Date()
        val filename = formatter.format(now) + System.currentTimeMillis()

        val storageReference = FirebaseStorage.getInstance().getReference("images/$filename")

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

                    // Save the movie details (including image URL) to Firebase Firestore
                    val newData = Movie(
                        poster = downloadUri.toString(),
                        title = binding.txtTitle.text.toString(),
                        description = binding.txtDesc.text.toString(),
                        rating = binding.txtRating.text.toString().toFloat(),
                        duration = binding.txtDuration.text.toString().toInt(),
                        director = binding.txtDirector.text.toString(),
                        writer = binding.txtWriter.text.toString(),
                        star = binding.txtStar.text.toString()
                    )

                    // Save the movie to Firestore
                    addData(newData)
                    // Display a success message
                    Toast.makeText(this@AddActivity, "Successfully uploaded", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle failures
                    Toast.makeText(this@AddActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }

                // Dismiss the progress dialog
                if (progressDialog.isShowing) progressDialog.dismiss()

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

        if(requestCode == 100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            binding.imageView.setImageURI(ImageUri)
        }
    }

    // dihandle untuk kondisi sukses, akan mereturn docRef
    private fun addData(movie: Movie) {
        movieCollectionRef.add(movie)
            .addOnSuccessListener { docRef ->
                val createSuaraId = docRef.id
                //id nya di update sesuai id yang berhasil
                movie.id = createSuaraId
                docRef.set(movie)
                    .addOnFailureListener{
                        Log.d("MainActivity", "Error update movie id", it)
                    }
            }
            .addOnFailureListener{
                Log.d("MainActivity", "Error add movie", it)
            }
    }

}