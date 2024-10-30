package com.example.kpu_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kpu_app.databinding.ActivityLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivityTicketApp"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefManager : PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefManager = PrefManager.getInstance(this)

        with(binding) {
            btnLogin.setOnClickListener {
                val inputEmail = emailField.text.toString()
                val inputPassword = passwordField.text.toString()

                val adminRef = FirebaseFirestore.getInstance().collection("admin")
                adminRef.whereEqualTo("email", inputEmail)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val adminDocuments = task.result?.documents
                            if (adminDocuments != null && adminDocuments.isNotEmpty()) {
                                // Retrieve the first document (assuming email are unique)
                                val adminDocument = adminDocuments[0]
                                val password = adminDocument.getString("password")
                                // Check if input matches the default username and password
                                if (inputPassword == password) {

                                    val intentToMainActivity = Intent(this@LoginActivity, HomeActivity::class.java)
                                    startActivity(intentToMainActivity)
                                } else {
                                    // Show an error message or perform appropriate action
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Invalid password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

        }
    }
}
