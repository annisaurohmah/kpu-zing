package com.example.proyek_uas.ui_admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_uas.MainActivity
import com.example.proyek_uas.Movie
import com.example.proyek_uas.PrefManager
import com.example.proyek_uas.R
import com.example.proyek_uas.databinding.ActivityListBinding
import com.google.firebase.firestore.FirebaseFirestore

class ListActivity : AppCompatActivity() {
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityListBinding
    private lateinit var itemAdapter: ListAdapter
    private lateinit var prefManager :PrefManager
    private val listViewData = ArrayList<Movie>()
    private val firestore = FirebaseFirestore.getInstance()
    private val movieCollectionRef = firestore.collection("movies")
    //nampung id
    private var updateId = ""
    //nampung list dari data suara
    private val movieListLiveData: MutableLiveData<List<Movie>> by lazy {
        MutableLiveData<List<Movie>>()
    }

    companion object{
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
                startActivity(Intent(this@ListActivity, MainActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        manager = LinearLayoutManager(this)



        itemAdapter = ListAdapter(this, listViewData) { item ->
            // Handle item click event
            // Misalnya, buka detail catatan atau lakukan tindakan lain
            updateId = item.id
            val IntentToForm = Intent(this, EditActivity::class.java)
                .apply {
                    putExtra(EXTRA_ID, item.id)
                }
            Log.d("Inii apaaa yaa", "${updateId}")
            startActivity(IntentToForm)
        }

        with(binding){
            btnAdd.setOnClickListener {
                val IntentToForm = Intent(this@ListActivity, AddActivity::class.java)
                startActivity(IntentToForm)
            }
            observeMovies()
            getAllMovies()
            recyclerView.layoutManager = LinearLayoutManager(this@ListActivity)
            recyclerView.adapter = itemAdapter

            }

//            listView.onItemLongClickListener =
//                AdapterView.OnItemLongClickListener { adapterView, view, i, id ->
//                    val item = adapterView.adapter.getItem(i) as Note
//                    delete(item)
//                    true
//                }
            }

    private fun getAllMovies() {
        observeMovieChanges()
    }

    private fun observeMovieChanges(){
        movieCollectionRef.addSnapshotListener{ snapshot, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for suara change: ", error)
                return@addSnapshotListener
            }
            val suaras = snapshot?.toObjects(Movie::class.java)
            if (suaras!= null) {
                movieListLiveData.postValue(suaras)
            }
        }
    }
    //update list data dari data budget yg diperbarui
    private fun observeMovies() {
        movieListLiveData.observe(this) { movies ->
            listViewData.clear()
            listViewData.addAll(movies)
            itemAdapter.notifyDataSetChanged()

            Log.d("ListActivity", "Number of moviess: ${movies.size}")
        }
    }

    override fun onResume() {
        super.onResume()
        getAllMovies()
    }
}


