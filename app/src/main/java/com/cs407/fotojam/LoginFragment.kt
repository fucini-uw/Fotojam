package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class LoginFragment(
    private val injectedUserViewModel: UserViewModel? = null
) : Fragment() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var errorText: TextView
    private lateinit var createAcctButton: Button
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        userViewModel = injectedUserViewModel ?: ViewModelProvider(requireActivity())[UserViewModel::class.java]

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        usernameEditText = view.findViewById(R.id.usernameEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)
        createAcctButton = view.findViewById(R.id.CreateAcctButton)
        database = Firebase.database.reference
        errorText = view.findViewById(R.id.errorText)
        errorText.visibility = View.GONE

        usernameEditText.doAfterTextChanged {
            errorText.visibility = View.GONE
        }

        passwordEditText.doAfterTextChanged {
            errorText.visibility = View.GONE
        }

        // Set the login button click action
        loginButton.setOnClickListener {
            // Get the entered username and password from EditText fields
            val userName = usernameEditText.getText().toString()
            val userPassword = passwordEditText.getText().toString()

            if (userName.isBlank() or userPassword.isBlank()) {
                // Show an error message if either username or password is empty
                errorText.text = "Username is empty!"
                errorText.visibility = View.VISIBLE
            } else if (userPassword.isBlank()) {
                errorText.text = "Password is empty!"
                errorText.visibility = View.VISIBLE
            } else {
                // Set the logged-in user in the ViewModel (store user info) (placeholder)
                val scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
//                    val e = withContext(Dispatchers.IO) {
//                        attemptLogin(userName, userPassword)
//                    }
                    val e = async { attemptLogin(userName, userPassword) }
                    if(!e.await()) {
                        errorText.text = "Incorrect username or password!"
                        errorText.visibility = View.VISIBLE
                    //Toast.makeText(context, "incorrect username or password", Toast.LENGTH_LONG).show()
                    } else {
                        //val intent = Intent(context, HomeActivity::class.java)
                        //startActivity(intent)
                        userViewModel.setUser(UserState(0, userName, hash(userPassword)))
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                    //if (getUserPasswd(userName, userPassword)) {
                    //val user = noteDB.userDao().getByName(userName)
                    //Log.i("db", user.userId.toString() + " " + user.userName)
                    //Log.i("vm", userViewModel.userState.value.id.toString() + " " + userViewModel.userState.value.name.toString())

                    //} else {
                    //errorText.visibility = View.VISIBLE
                    //}
                    // Start main activity if success

                }
            }
        }
        createAcctButton.setOnClickListener {
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                //val intent = Intent(context, SignupActivity::class.java)
                //startActivity(intent)
                findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
            }
        }
    }

    private suspend fun attemptLogin(user: String, pass: String): Boolean {
        val scope = CoroutineScope(Dispatchers.IO)
        var placeholder = false
        var flag = false
        val job = scope.launch {
            database.child("users").child(user).child("pass").get()
                .addOnSuccessListener {
                    val pw = it.value.toString()
                    if (pw.compareTo(hash(pass)) == 0) {
                        Log.i("firebase", "pw: " + pw)
                        flag = true
                        placeholder = true
                    } else {
                        Log.e("firebase", "pw: " + pw)
                        placeholder = true
                    }
                }
                .addOnFailureListener {
                    Log.e("firebase", "error retrieving from database")
                    placeholder = true
                }

        }
        runBlocking {
            job.join()
        }
        while (!placeholder) {
            delay(100)
        }

        return flag
    }

    private fun hash(input: String): String {
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}