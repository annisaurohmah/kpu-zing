package com.example.proyek_uas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.proyek_uas.databinding.FragmentRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private lateinit var binding : FragmentRegisterBinding
    private lateinit var prefManager : PrefManager
    private var TAG = "FragmentRegisterNapa"
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    //firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userCollectionRef = firestore.collection("users")
    //nampung id
    private var updateId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        prefManager = PrefManager.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ensure that the fragment is attached to the activity before using requireContext()
        if (isAdded) {
        binding = FragmentRegisterBinding.bind(view)
        with(binding) {

            btnRegister.setOnClickListener {
                val username = edtUsername.text.toString()
                val email = edtEmail.text.toString().trim()
                val password = edtPassword.text.toString().trim()
                val confirmPassword = edtPasswordConfirm.text.toString()
                if (username.isEmpty() || password.isEmpty() || email.isEmpty() ||
                    confirmPassword.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please fill in all the fields",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(requireContext(), "Password and Confirm Password do not match",
                        Toast.LENGTH_SHORT).show()
                } else {
                    checkUsernameAvailability(username){ isAvailable ->
                        if(isAvailable){
                        prefManager.signUpWithEmailPassword(email, password, username) { task, message ->
                            if (task) {
                                // Authentication was successful
                                val user = FirebaseAuth.getInstance().currentUser
                                Log.d("MainAAA", "Successfully created user with uid: ${user?.uid}")

                                // Proceed with registration
                                val newUser = User(
                                    id = user?.uid ?: "", // Use safe call to handle potential null
                                    email = email,
                                    username = username,
                                    password = password
                                )

                                addData(newUser)
                                prefManager.setLoggedIn(true)
                                checkLoginStatus()
                            } else {
                                // Authentication failed
                                Log.d("MainAAA", "Failed to create user")
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to create user",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }


                }
                        else {
                            Toast.makeText(
                                requireContext(),
                                "Username is already taken",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
            }

        }
    }
    }
        }
    }
    private fun checkUsernameAvailability(username: String, callback: (Boolean) -> Unit) {
        val usersRef = FirebaseFirestore.getInstance().collection("users")
        usersRef.whereEqualTo("username", username)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Use safe call ?. to handle null result or empty result
                    val isUsernameAvailable = task.result?.isEmpty ?: true
                    Log.d("ResultUsername", "$isUsernameAvailable")
                    callback(isUsernameAvailable)
                } else {
                    // Handle error
                    // If task.exception is not null, log the error
                    task.exception?.let {
                        Log.e("RegisterFragment", "Error checking username availability", it)
                    }
                    callback(false)
                }
            }
    }

    private fun checkLoginStatus() {
        val isLoggedIn = prefManager.isLoggedIn()
        if (isLoggedIn) {
            Toast.makeText(requireContext(), "Registrasi berhasil",
                Toast.LENGTH_SHORT).show()
            (requireActivity() as? MainActivity)?.navigateToHomeActivity()
        } else {
            Toast.makeText(requireContext(), "Registrasi gagal",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun addData(user: User) {
        userCollectionRef.add(user)
            .addOnSuccessListener { docRef ->
                val createUserId = docRef.id
                //id nya di update sesuai id yang berhasil
                user.id = createUserId
                docRef.set(user)
                    .addOnFailureListener{
                        Log.d("MainActivity2", "Error update user id", it)
                    }
                resetForm()
            }
            .addOnFailureListener{
                Log.d("MainActivity", "Error add user", it)
            }
    }

    private fun resetForm() {
        with(binding){
            edtEmail.setText("")
            edtUsername.setText("")
            edtPassword.setText("")
            edtPasswordConfirm.setText("")
        }
    }
}