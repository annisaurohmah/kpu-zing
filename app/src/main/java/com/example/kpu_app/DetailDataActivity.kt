package com.example.kpu_app

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kpu_app.databinding.ActivityDetailPemilihBinding
import com.example.kpu_app.ui_admin.LihatDataActivity
import com.example.kpu_app.ui_admin.LihatDataActivity.Companion.EXTRA_ID
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class DetailDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPemilihBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var prefManager: PrefManager
    private val channelId = "TEST_NOTIF"
    private val notifId = 90
    private val adminCollection: CollectionReference = firestore.collection("admin")


    private val pemilihCollectionRef = firestore.collection("pemilih")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPemilihBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getStringExtra(EXTRA_ID)
        Log.d("Complete42", "$id")
        val isHome = intent.getBooleanExtra("FROM_HOME_FRAGMENT", false)
        prefManager = PrefManager.getInstance(this@DetailDataActivity)

        with(binding) {

            btnBack.setOnClickListener{
                onBackPressed()
            }

            btnDelete.setOnClickListener {
                pemilihCollectionRef.whereEqualTo("nik", id).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val pemilihDocuments = task.result?.documents
                            Log.d("Complete42", "$pemilihDocuments")
                            if (pemilihDocuments != null && pemilihDocuments.isNotEmpty()) {
                                // Tampilkan dialog konfirmasi
                                AlertDialog.Builder(this@DetailDataActivity)
                                    .setTitle("Konfirmasi Hapus")
                                    .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                                    .setPositiveButton("Ya") { dialog, _ ->
                                        // Eksekusi kode penghapusan
                                        for (document in pemilihDocuments) {
                                            document.reference.delete()
                                                .addOnSuccessListener {
                                                    Toast.makeText(this@DetailDataActivity, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                                                    startActivity(Intent(this@DetailDataActivity, LihatDataActivity::class.java))
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(this@DetailDataActivity, "Gagal menghapus data: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                        dialog.dismiss()
                                    }
                                    .setNegativeButton("Tidak") { dialog, _ ->
                                        // Tutup dialog tanpa melakukan apapun
                                        dialog.dismiss()
                                    }
                                    .show()
                            }
                        }
                    }
            }


            pemilihCollectionRef.whereEqualTo("nik", id).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val pemilihDocuments = task.result?.documents
                        Log.d("Complete42", "$pemilihDocuments")
                        if (pemilihDocuments != null && pemilihDocuments.isNotEmpty()) {
                            val pemilihDocument = pemilihDocuments[0]
                            val nik = pemilihDocument.getString("nik")
                            val fullname = pemilihDocument.getString("fullname")
                            val nohp = pemilihDocument.getString("nohp")
                            val gender = pemilihDocument.getString("gender")
                            val registerdate = pemilihDocument.getString("registerdate")
                            val location = pemilihDocument.getString("location")
                            val picture = pemilihDocument.getString("picture")

                            Log.d("MovieDetails", "Title")

                            textFullname.setText(fullname)
                            textNik.setText(nik)
                            textGender.setText(gender)
                            textNohp.setText(nohp)
                            textRegisterdate.setText(registerdate)
                            textLocation.setText(location)
                            Log.d("IMGP", "{$picture}")
                            Glide.with(this@DetailDataActivity).load(picture)
                                .centerCrop().into(imagePoster)
                        }
                    }

                }

        }
    }


}