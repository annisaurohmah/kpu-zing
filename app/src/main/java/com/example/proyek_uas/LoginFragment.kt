package com.example.proyek_uas

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.proyek_uas.databinding.FragmentLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private lateinit var prefManager: PrefManager
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager.getInstance(requireContext())

        binding = FragmentLoginBinding.bind(view)

        with(binding) {
            if (prefManager.checkLoginStatus() && prefManager.getUsername() != "admin"){
                (requireActivity() as? MainActivity)?.navigateToHomeActivity()
            } else if (prefManager.checkLoginStatus() && prefManager.getUsername() == "admin"){
                (requireActivity() as? MainActivity)?.navigateToListActivity()
            }
            btnLogin.setOnClickListener {
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please fill in all the fields",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    isValidUsernamePassword { isValid ->
                        if (isValid) {
                            // Username and password are valid, proceed with your logic

                            prefManager.setLoggedIn(true)
                            checkLoginStatus()

                        } else {
                            // Username and password are not valid
                            Toast.makeText(requireContext(), "Invalid username", Toast.LENGTH_SHORT).show()

                        }
                    }

                }
            }

        }
    }
    private fun isValidUsernamePassword(callback: (Boolean) -> Unit) {
        val inputUsername = binding.edtUsername.text.toString()
        val inputPassword = binding.edtPassword.text.toString()

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
                        prefManager.signInWithEmailPassword(email!!, inputPassword, inputUsername) { authTask, message ->
                                if (authTask) {
                                    // Successfully logged in
                                    prefManager.saveIdUser(idUser!!)
                                    callback(true)
                                    Log.d("Login", "${prefManager.getIdUser()}")
                                } else {
                                    // Failed to log in
                                    callback(false)
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to log in",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        // User not found
                        callback(false)
                        Toast.makeText(
                            requireContext(), "User not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Handle the case where the document is not found or other errors
                    callback(false)
                    Toast.makeText(
                        requireContext(), "Error retrieving user data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun checkLoginStatus() {
        val isLoggedIn = prefManager.isLoggedIn()
        if (isLoggedIn) {
            Toast.makeText(requireContext(), "Login success",
                Toast.LENGTH_SHORT).show()
            (requireActivity() as? MainActivity)?.navigateToHomeActivity()
        } else {
            Toast.makeText(requireContext(), "Login fail",
                Toast.LENGTH_SHORT).show()
        }
    }

}


