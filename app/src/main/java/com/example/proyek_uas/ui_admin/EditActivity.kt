package com.example.proyek_uas.ui_admin


import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.proyek_uas.databinding.ActivityEditBinding
import com.example.room1.database.Movie
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private var ImageUri: Uri? = null
    private var originalImageUri: Uri? = null

    //firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val movieCollectionRef = firestore.collection("movies")
    //nampung id
    private var updateId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getStringExtra(ListActivity.EXTRA_ID)

        with(binding) {
            Log.d("EditDataMovie", "ID yang masuk : ${id}")
            movieCollectionRef.whereEqualTo("id", id).get()
                .addOnCompleteListener{
                        task ->
                    if (task.isSuccessful) {
                        val movieDocuments = task.result?.documents
                        Log.d("Data Movie :", "$movieDocuments")
                        if (movieDocuments != null && movieDocuments.isNotEmpty()) {
                            val movieDocument = movieDocuments[0]
                            val poster = movieDocument.getString("poster")
                            originalImageUri = poster!!.toUri()
                            val title = movieDocument.getString("title")
                            val durationLong = movieDocument.getLong("duration")
                            val duration = durationLong?.toInt() ?: 0
                            val ratingDouble = movieDocument.getDouble("rating")
                            val rating = ratingDouble?.toFloat() ?: 0.0f
                            val description = movieDocument.getString("description")
                            val director = movieDocument.getString("director")
                            val writer = movieDocument.getString("writer")
                            val star = movieDocument.getString("star")

                            Log.d("MovieDetails", "Title: $title, Poster: $poster, Rating: $rating")

                            txtTitle.setText(title)
                            Glide.with(this@EditActivity).load(poster)
                                .centerCrop().into(imageView)
                            txtDesc.setText(description)
                            txtRating.setText(rating.toString())
                            txtDuration.setText(duration.toString())
                            txtDirector.setText(director)
                            txtWriter.setText(writer)
                            txtStar.setText(star)}}

                }



            btnUpload.setOnClickListener{
                selectImage()
            }

            btnUpdate.setOnClickListener {
                if (txtTitle.text!!.isEmpty() || txtDesc.text!!.isEmpty() ||  txtDirector.text!!.isEmpty() ||
                    txtStar.text!!.isEmpty() || txtDuration.text!!.isEmpty() || txtRating.text!!.isEmpty() || txtWriter.text!!.isEmpty()){
                    Toast.makeText(this@EditActivity, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                } else if ((txtRating.text.toString().toFloat() !in 0.0..5.0)){
                    Toast.makeText(this@EditActivity, "Please fill with correct range in Rating", Toast.LENGTH_SHORT).show()
                } else {
                    uploadImage(id!!)
                    startActivity(Intent(this@EditActivity, ListActivity::class.java))
                }
            }

            btnDelete.setOnClickListener {
                val movieToDelete = id?.let { it1 -> Movie(id = it1) }
                if (movieToDelete != null) {
                    deleteData(movieToDelete)
                    FirebaseStorage.getInstance().getReferenceFromUrl(originalImageUri.toString()).delete()
                }
                    startActivity(Intent(this@EditActivity, ListActivity::class.java))
            }
            btnBack.setOnClickListener {
                startActivity(Intent(this@EditActivity, ListActivity::class.java))
            }
        }
    }


    private fun uploadImage(id: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading file...")
        progressDialog.setCancelable(false)

        progressDialog.show()

        if (ImageUri == null) {
            // No image selected, proceed with updating data without uploading an image
            val newData = Movie(
                id = id,
                poster = originalImageUri.toString(),
                title = binding.txtTitle.text.toString(),
                description = binding.txtDesc.text.toString(),
                rating = binding.txtRating.text.toString().toFloat(),
                duration = binding.txtDuration.text.toString().toInt(),
                director = binding.txtDirector.text.toString(),
                writer = binding.txtWriter.text.toString(),
                star = binding.txtStar.text.toString()
            )
            val id = intent.getStringExtra(ListActivity.EXTRA_ID)
            updateId = id.toString()
            updateData(newData, updateId)
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this@EditActivity, "Data updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            // Image is selected, proceed with uploading image and updating data
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val now = Date()
            val filename = formatter.format(now) + System.currentTimeMillis()
            val storageReference = FirebaseStorage.getInstance().getReference("images/$filename")

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
                        FirebaseStorage.getInstance().getReferenceFromUrl(originalImageUri.toString()).delete()

                        // Save the movie details (including image URL) to Firebase Firestore
                        val newData = Movie(
                            id = id,
                            poster = downloadUri.toString(),
                            title = binding.txtTitle.text.toString(),
                            description = binding.txtDesc.text.toString(),
                            rating = binding.txtRating.text.toString().toFloat(),
                            duration = binding.txtDuration.text.toString().toInt(),
                            director = binding.txtDirector.text.toString(),
                            writer = binding.txtWriter.text.toString(),
                            star = binding.txtStar.text.toString()
                        )
                        val id = intent.getStringExtra(ListActivity.EXTRA_ID)
                        updateId = id.toString()
                        // Save the movie to Firestore
                        updateData(newData, id!!)
                        // Your code for completion
                        Toast.makeText(this@EditActivity, "Successfully uploaded", Toast.LENGTH_SHORT).show()
                    }
                    if (progressDialog.isShowing) progressDialog.dismiss()
                }
                .addOnFailureListener { exception ->
                    // Your code for failure
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    Toast.makeText(this@EditActivity, "Failed uploaded", Toast.LENGTH_SHORT).show()
                    Log.e("Firebase", "Image Upload fail", exception)
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

        if (requestCode == 100 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // Check if it's a content URI
                if (uri.scheme == "content") {
                    ImageUri = uri
                    binding.imageView.setImageURI(ImageUri)
                } else {
                    return
                }
            }
        }
    }

    private fun updateData(movie: Movie, id: String) {
        Log.d("UpdateData", "Update ID: ${id}")
                        movieCollectionRef.document(id).set(movie)
                            .addOnFailureListener {
                                Log.d("MainActivity", "Error update data suara", it)
                            }
                    }

    private fun deleteData(movie: Movie) {
        if (movie.id.isEmpty()) {
            Log.d("EditActivity", "Error delete data empty ID", return)
        }

        movieCollectionRef.document(movie.id).delete()
            .addOnFailureListener {
                Log.d("MainActivity", "Error delete data budget")
            }
    }

}