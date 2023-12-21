package com.example.proyek_uas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyek_uas.databinding.ActivityMainBinding
import com.example.proyek_uas.ui_admin.ListActivity

class MainActivity : AppCompatActivity() {
    //    private var TAG = "MainActivityBerjalan"
    private lateinit var binding: ActivityMainBinding

    fun navigateToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
    fun navigateToListActivity() {
        startActivity(Intent(this, ListActivity::class.java))
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {

            viewPager.adapter = TabAdapter(supportFragmentManager)

            tabLayout.setupWithViewPager(viewPager)

            loginAdmin.setOnClickListener {
                startActivity(
                    Intent(this@MainActivity, LoginActivity::class.java)
                )
            }


        }


    }

}