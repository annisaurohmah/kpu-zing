package com.example.proyek_uas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.proyek_uas.databinding.ActivityHomeBinding
import com.example.room1.database.MovieDao
import com.example.room1.database.MovieR
import java.util.concurrent.ExecutorService


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var executorService: ExecutorService
    private lateinit var mMovieDao: MovieDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_favorite -> replaceFragment(FavoriteFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())

                else -> {}
            }
            true
        }
    }
    fun navigateToDetailMovieActivity() {
        Intent(this, DetailMovieActivity::class.java)
    }
    override fun finish(){
        finish()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
    fun insert(movie: MovieR) {
        executorService.execute { mMovieDao.insert(movie) }
    }

    fun delete(movie: MovieR) {
        executorService.execute { mMovieDao.delete(movie) }
    }

    fun update(movie: MovieR) {
        executorService.execute { mMovieDao.update(movie) }
    }

}