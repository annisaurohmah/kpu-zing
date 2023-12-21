package com.example.proyek_uas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreen : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val mainIntent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}