package com.example.kpu_app


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.kpu_app.databinding.ActivityHomeBinding
import com.example.kpu_app.ui_admin.LihatDataActivity


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            menuInformasi.setOnClickListener {
                val intentToInformasiActivity = Intent(this@HomeActivity, InformasiActivity::class.java)
                startActivity(intentToInformasiActivity)
            }
            menuFormEntri.setOnClickListener {
                val intentToFormEntriActivity = Intent(this@HomeActivity, EntriDataActivity::class.java)
                startActivity(intentToFormEntriActivity)
            }
            menuLihatData.setOnClickListener {
                val intentToLihatDataActivity = Intent(this@HomeActivity, LihatDataActivity::class.java)
                startActivity(intentToLihatDataActivity)
            }
            menuKeluar.setOnClickListener {
                val intentToLoginActivity = Intent(this@HomeActivity, LoginActivity::class.java)
                startActivity(intentToLoginActivity)
            }


        }
    }

    override fun finish(){
        finish()
    }

}