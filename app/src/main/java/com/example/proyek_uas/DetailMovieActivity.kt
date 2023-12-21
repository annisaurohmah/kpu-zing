package com.example.proyek_uas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.proyek_uas.databinding.ActivityDetailMovieBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class DetailMovieActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailMovieBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var prefManager: PrefManager
    private val channelId = "TEST_NOTIF"
    private val notifId = 90
    private val usersCollection: CollectionReference = firestore.collection("users")


    private val movieCollectionRef = firestore.collection("movies")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getStringExtra("EXTRA_ID")
        val isHome = intent.getBooleanExtra("FROM_HOME_FRAGMENT", false)
        prefManager = PrefManager.getInstance(this@DetailMovieActivity)

        with(binding) {
            getFavoriteMovie(prefManager.getIdUser()) { favoriteMovies ->
                // Use the favoriteMovies list here
                if (id !in favoriteMovies) {
                    btnLove.setImageDrawable(ContextCompat.getDrawable(this@DetailMovieActivity, com.example.proyek_uas.R.drawable.baseline_favorite_border_white_24))
                } else {
                    btnLove.setImageDrawable(ContextCompat.getDrawable(this@DetailMovieActivity, com.example.proyek_uas.R.drawable.baseline_favorite_24_red))
                }
                Log.d("FavoriteMovies", "Favorite Movies: $favoriteMovies")
            }

            btnBack.setOnClickListener{
                onBackPressed()
            }
            movieCollectionRef.whereEqualTo("id", id).get()
                .addOnCompleteListener{
                        task ->
                    if (task.isSuccessful) {
                        val movieDocuments = task.result?.documents
                        Log.d("Complete42", "$movieDocuments")
                        if (movieDocuments != null && movieDocuments.isNotEmpty()) {
                            val movieDocument = movieDocuments[0]
                            val poster = movieDocument.getString("poster")
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
                            Glide.with(this@DetailMovieActivity).load(poster)
                                .centerCrop().into(imagePoster)
                            val rinci = durationLong.toString() + " minutes  |  ⭐ " + rating.toString() + " "
                            textDes.setText(rinci)
                            txtDes.setText(description)
                            nameDirector.setText(director)
                            nameWriter.setText(writer)
                            nameStar.setText(star)}}

                }

            btnLove.setOnClickListener {
                getFavoriteMovie(prefManager.getIdUser()) { favoriteMovies ->
                    // Use the favoriteMovies list here
                    if (id !in favoriteMovies.toSet()) {
                        btnLove.setImageDrawable(ContextCompat.getDrawable(this@DetailMovieActivity, com.example.proyek_uas.R.drawable.baseline_favorite_24_red))
                        prefManager.saveState(false)
                        // Menambahkan film ke daftar favorit
                        val favoriteMovie = favoriteMovies.toMutableSet()
                        favoriteMovie.add(id!!)
                        saveFavoriteMovie(prefManager.getIdUser(), favoriteMovie.toList())
                        notificationButton()
                    } else {
                        btnLove.setImageDrawable(ContextCompat.getDrawable(this@DetailMovieActivity, com.example.proyek_uas.R.drawable.baseline_favorite_border_white_24))
                        prefManager.saveState(true)
                        // Menghapus film dari daftar favorit
                        val favoriteMovie = favoriteMovies.toMutableSet()
                        favoriteMovie.remove(id!!)
                        saveFavoriteMovie(prefManager.getIdUser(), favoriteMovie.toList())
                    }
                    Log.d("FavoriteMovies", "Favorite Movies: $favoriteMovies")
                }
            }

        }
    }

    private fun notificationButton() {
        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        }
        else {
            0
        }
        val intent = Intent(this@DetailMovieActivity,
            NotifReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this@DetailMovieActivity,
            0,
            intent,
            flag
        )
        val builder = NotificationCompat.Builder(this@DetailMovieActivity, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Favorite")
            .setContentText("You add the movie in favorite list")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notifChannel = NotificationChannel(
                channelId, // Id channel
                "Ticket Flix", // Nama channel notifikasi
                NotificationManager.IMPORTANCE_DEFAULT
            )
            with(notifManager) {
                createNotificationChannel(notifChannel)
                notify(notifId, builder.build())
            }
        }
        else {
            notifManager.notify(notifId, builder.build())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                handleBackNavigation()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun handleBackNavigation() {
        val fromHomeFragment = intent.getBooleanExtra("FROM_HOME_FRAGMENT", false)

        if (fromHomeFragment) {
            // Navigate back to Home fragment
            supportFragmentManager.popBackStack()
        } else {
            // Navigate back to Favorite fragment
            val intentBack = Intent(this@DetailMovieActivity, FavoriteFragment::class.java)
            startActivity(intentBack)
        }
    }
    fun saveFavoriteMovie(userId: String, favoriteMovies: List<String>) {
        val userReference: DocumentReference = usersCollection.document(userId)
        userReference.update("favoriteMovies", favoriteMovies)
    }

    // Mengambil set ID film favorit dari Firestore
    fun getFavoriteMovie(userId: String, onComplete: (List<String>) -> Unit) {
        val userReference: DocumentReference = usersCollection.document(userId)

        userReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userSnapshot = task.result
                    val favoriteMovies = if (userSnapshot != null && userSnapshot.exists()) {
                        val user = userSnapshot.toObject(User::class.java)
                        user?.favoriteMovies ?: mutableListOf()
                    } else {
                        mutableListOf()
                    }
                    onComplete(favoriteMovies)
                } else {
                    // Handle the error
                    onComplete(emptyList())
                }
            }
    }

}