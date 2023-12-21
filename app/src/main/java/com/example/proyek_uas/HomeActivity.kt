package com.example.proyek_uas


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.proyek_uas.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
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

    override fun finish(){
        finish()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

}