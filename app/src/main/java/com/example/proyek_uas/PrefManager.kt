package com.example.proyek_uas

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PrefManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val auth = FirebaseAuth.getInstance()


    companion object {
        private const val PREFS_FILENAME = "AuthAppPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_ID = "idUser"
        private const val KEY_PASSWORD = "password"// Add new key for email
        @Volatile
        private var instance: PrefManager? = null
        fun getInstance(context: Context): PrefManager {
            return instance ?: synchronized(this) {
                instance ?: PrefManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    init {
        sharedPreferences = context.getSharedPreferences(PREFS_FILENAME,
            Context.MODE_PRIVATE)
    }

    fun saveState(favoriteState: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("State", favoriteState)
        editor.apply()
    }


    fun checkLoginStatus(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    fun saveUsername(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }
    fun saveIdUser(idUser: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_ID, idUser)
        editor.apply()
    }
    fun savePassword(password: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_PASSWORD, password)
        editor.apply()
    }


    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    }
    fun getIdUser(): String {
        return sharedPreferences.getString(KEY_ID, "") ?: ""
    }
    fun getPassword(): String {
        return sharedPreferences.getString(KEY_PASSWORD, "") ?: ""
    }
    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun saveEmail(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    fun getEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "") ?: ""
    }

    // Firebase authentication methods
    fun signUpWithEmailPassword(email: String, password: String, username: String, callback: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveEmail(email)
                    savePassword(password)
                    saveUsername(username)// Save email to shared preferences
                    callback(true, "Sign up successful")
                } else {
                    callback(false, task.exception?.message ?: "Sign up failed")
                }
            }
    }

    fun signInWithEmailPassword(email: String, password: String, username: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveEmail(email)
                    savePassword(password)
                    saveUsername(username)// Save email to shared preferences
                    callback(true, "Sign in successful")
                } else {
                    callback(false, task.exception?.message ?: "Sign in failed")
                }
            }
    }

    fun isValidUsernamePassword(inputUsername: String, inputPassword:String, callback: (Boolean) -> Unit) {
        val usersRef = FirebaseFirestore.getInstance().collection("users")
        usersRef.whereEqualTo("username", inputUsername)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userDocuments = task.result?.documents
                    if (userDocuments != null && userDocuments.isNotEmpty()) {
                        // Retrieve the first document (assuming usernames are unique)
                        val userDocument = userDocuments[0]
                        val email = userDocument.getString("email")
                        val idUser = userDocument.getString("id")
                        // Use Firebase Authentication to log in
                        signInWithEmailPassword(email!!, inputPassword, inputUsername) { authTask, message ->
                            if (authTask) {
                                // Successfully logged in
                                saveIdUser(idUser!!)
                                callback(true)
                                Log.d("Login", "${getIdUser()}")
                            } else {
                                // Failed to log in
                                callback(false)
                            }
                        }
                    } else {
                        // User not found
                        callback(false)
                    }
                } else {
                    // Handle the case where the document is not found or other errors
                    callback(false)
                }
            }
    }


    fun signOut() {
        auth.signOut()
        clear()  // Clear shared preferences on sign out
    }
}

