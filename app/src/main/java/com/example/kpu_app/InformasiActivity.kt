package com.example.kpu_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kpu_app.databinding.ActivityDetailPemilihBinding
import com.example.kpu_app.databinding.ActivityEntriDataBinding
import com.example.kpu_app.databinding.ActivityInformasiBinding

class InformasiActivity : AppCompatActivity() {
    lateinit var binding: ActivityInformasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            btnBack.setOnClickListener{
                onBackPressed()
            }
        }


    }
}