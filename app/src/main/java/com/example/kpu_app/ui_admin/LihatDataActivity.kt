package com.example.kpu_app.ui_admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kpu_app.DetailDataActivity
import com.example.kpu_app.LoginActivity
import com.example.kpu_app.PrefManager
import com.example.kpu_app.R
import com.example.kpu_app.databinding.ActivityLihatDataBinding
import com.example.room1.database.Pemilih
import com.google.firebase.firestore.FirebaseFirestore

class LihatDataActivity : AppCompatActivity() {
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityLihatDataBinding
    private lateinit var itemAdapter: ListAdapter
    private lateinit var prefManager: PrefManager
    private val listViewData = ArrayList<Pemilih>()
    private val firestore = FirebaseFirestore.getInstance()
    private val pemilihCollectionRef = firestore.collection("pemilih")
    private val pemilihListLiveData: MutableLiveData<List<Pemilih>> by lazy {
        MutableLiveData<List<Pemilih>>()
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        prefManager = PrefManager.getInstance(this)
        when (item.itemId) {
            R.id.nav_logout -> {
                prefManager.signOut()
                startActivity(Intent(this@LihatDataActivity, LoginActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLihatDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        manager = LinearLayoutManager(this)

        itemAdapter = ListAdapter(this, listViewData) { item ->
            val intentToForm = Intent(this, DetailDataActivity::class.java).apply {
                putExtra(EXTRA_ID, item.nik)
            }
            Log.d("ListActivity", "ID yang diklik ${item.nik}" )
            startActivity(intentToForm)
        }

        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(this@LihatDataActivity)
            recyclerView.adapter = itemAdapter

            btnBack.setOnClickListener{
             onBackPressed()
            }


            // Mengambil data dari Firestore
            getAllPemilih()
        }
    }

    // Memuat data dari Firestore ke dalam LiveData
    private fun getAllPemilih() {
        observePemilihChanges() // Panggil metode ini untuk memuat data dari Firestore pertama kali
        observePemilih()        // Pantau LiveData untuk memperbarui tampilan saat data berubah
    }

    // Mendengarkan perubahan data pada koleksi Firestore
    private fun observePemilihChanges() {
        pemilihCollectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for pemilih changes: ", error)
                return@addSnapshotListener
            }

            val pemilihList = snapshot?.toObjects(Pemilih::class.java)
            if (pemilihList != null) {
                pemilihListLiveData.postValue(pemilihList)
            }
        }
    }

    // Memperbarui tampilan saat data pada LiveData berubah
    private fun observePemilih() {
        pemilihListLiveData.observe(this) { pemilih ->
            listViewData.clear()
            listViewData.addAll(pemilih)
            itemAdapter.notifyDataSetChanged()
            checkIfEmpty() // Panggil fungsi ini untuk mengecek jika data kosong
        }
    }

    // Mengecek apakah listViewData kosong
    private fun checkIfEmpty() {
        if (listViewData.isEmpty()) {
            binding.tvEmptyMessage.visibility = View.VISIBLE
        } else {
            binding.tvEmptyMessage.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        getAllPemilih()
    }
}
