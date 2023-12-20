package com.example.proyek_uas

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.transition.Transition
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Database
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.example.proyek_uas.databinding.FragmentHomeBinding
import com.example.proyek_uas.ui_admin.ListActivity
import com.example.room1.database.MovieDao
import com.example.room1.database.MovieR
import com.example.room1.database.MovieRoomDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var prefManager: PrefManager
    private lateinit var manager: RecyclerView.LayoutManager
    private val listViewData = ArrayList<Movie>()
    private val listViewDataOff = ArrayList<MovieR>()
    private lateinit var itemAdapter: HomeAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val movieCollectionRef = firestore.collection("movies")

    //nampung id
    private var updateId = ""

    //nampung list dari data suara
    private val movieListLiveData: MutableLiveData<List<Movie>> by lazy {
        MutableLiveData<List<Movie>>()
    }
    private lateinit var executorService: ExecutorService
    private lateinit var mMovieDao: MovieDao
    private lateinit var database: CollectionReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        executorService = Executors.newSingleThreadExecutor()
        val db = MovieRoomDatabase.getDatabase(requireContext())
        mMovieDao = db!!.nodeDao()!!


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        prefManager = PrefManager.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        manager = LinearLayoutManager(requireContext())


        itemAdapter = HomeAdapter(requireContext(), listViewData) { item ->
            // Handle item click event
            // Misalnya, buka detail catatan atau lakukan tindakan lain
            val intent = Intent(requireContext(), DetailMovieActivity::class.java).apply {
                putExtra("EXTRA_ID", item.id)
                putExtra("FROM_HOME_FRAGMENT", true)
            }
            // Start the activity with the intent
            startActivity(intent)
        }
        // Initialize Room database
        mMovieDao = MovieRoomDatabase.getDatabase(requireContext())?.nodeDao()!!

        // Initialize Firebase reference
        database = FirebaseFirestore.getInstance().collection("movies")

        // Fetch data from Firebase and update itemList
        fetchFilmFromFirebase()

        with(binding) {
            username.setText(prefManager.getUsername())
            getAllMovies()
            rvMovie.layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false);
            rvMovie.adapter = itemAdapter

        }
    }

    private fun fetchData() {
        if (isNetworkAvailable()) {
            // Jika online, ambil data dari Firestore
            fetchFilmFromFirebase()
        } else {
            // Jika offline, ambil data dari Room
            getAllMovies()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun getAllMovies() {
        lifecycleScope.launch {
            observeMovies()
        }
    }

    private fun observeMovies() {
        movieListLiveData.observe(requireActivity()) { movies ->
            listViewData.clear()
            listViewData.addAll(movies)
            itemAdapter.notifyDataSetChanged()
            Log.d("ListActivity", "Number of movies: ${movies.size}")
        }
    }

    private fun fetchFilmFromFirebase() {
        database.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle the error if needed
                return@addSnapshotListener
            }

            snapshot?.let { documents ->
                val filmList = mutableListOf<MovieR>()

                for (document in documents) {
                    val filmEntity = document.toObject(MovieR::class.java)
                    filmEntity?.let { filmList.add(it) }
                }

                // Update Room database with the new data from Firebase
                lifecycleScope.launch(Dispatchers.IO) {
                    mMovieDao.deleteAllFilm()
                    mMovieDao.insertFilm(filmList)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

}