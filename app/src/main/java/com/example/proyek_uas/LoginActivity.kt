package com.example.proyek_uas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.proyek_uas.databinding.ActivityLoginBinding
import com.example.proyek_uas.ui_admin.ListActivity
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
                val inputUsername = usernameField.text.toString()
                val inputPassword = passwordField.text.toString()

                val adminRef = FirebaseFirestore.getInstance().collection("admin")
                adminRef.whereEqualTo("username", inputUsername)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val adminDocuments = task.result?.documents
                            if (adminDocuments != null && adminDocuments.isNotEmpty()) {
                                // Retrieve the first document (assuming usernames are unique)
                                val adminDocument = adminDocuments[0]
                                val password = adminDocument.getString("password")
                                // Check if input matches the default username and password
                                if (inputPassword == password) {
                                    prefManager.saveUsername(inputUsername)
                                    prefManager.savePassword(password)
                                    prefManager.setLoggedIn(true)

                                    val intentToMainActivity = Intent(this@LoginActivity, ListActivity::class.java)
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
                            "Invalid username",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            regLink.setOnClickListener{
                val intentToMainActivity = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intentToMainActivity)
            }
        }
    }
}
