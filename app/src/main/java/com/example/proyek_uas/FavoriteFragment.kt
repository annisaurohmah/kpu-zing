package com.example.proyek_uas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_uas.databinding.FragmentFavoriteBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentFavoriteBinding
    private lateinit var prefManager : PrefManager
    private lateinit var manager: RecyclerView.LayoutManager
    private val listViewData = ArrayList<Movie>()
    private lateinit var itemAdapter: HomeAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val movieCollectionRef = firestore.collection("movies")
    //nampung id
    private var updateId = ""
    //nampung list dari data suara
    private val movieListLiveData: MutableLiveData<List<Movie>> by lazy {
        MutableLiveData<List<Movie>>()
    }
    private val favoriteMovies: ArrayList<Movie>
        get() = ArrayList(listViewData.filter { it.isFavorite })



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)
        manager = LinearLayoutManager(requireContext())



        itemAdapter = HomeAdapter(requireContext(), listViewData) { item ->
            // Handle item click event
            // Misalnya, buka detail catatan atau lakukan tindakan lain

            val intent = Intent(requireContext(), DetailMovieActivity::class.java).apply {
                putExtra("EXTRA_ID", item.id)
            }
            // Start the activity with the intent
            startActivity(intent)
        }


        with(binding){
            observeMovies()
            getAllMovies()
            rvMovie.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false);
            rvMovie.adapter = itemAdapter

        }}
    private fun getAllMovies() {
        observeMovieChanges()
    }

    private fun observeMovieChanges(){
        movieCollectionRef.addSnapshotListener{ snapshot, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for suara change: ", error)
                return@addSnapshotListener
            }
            val movies = snapshot?.toObjects(Movie::class.java)
            if (movies!= null) {
                movieListLiveData.postValue(movies)
            }
        }
    }
    //update list data dari data budget yg diperbarui
    private fun observeMovies() {
        movieListLiveData.observe(requireActivity()) { movies ->
            favoriteMovies.clear()
            favoriteMovies.addAll(movies)
            itemAdapter.notifyDataSetChanged()

            Log.d("ListActivity", "Number of moviess: ${movies.size}")
        }
    }

    override fun onResume() {
        super.onResume()
        getAllMovies()
    }

}