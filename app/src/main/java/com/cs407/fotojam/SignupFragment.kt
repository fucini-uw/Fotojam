package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.launch
import java.security.MessageDigest

class SignupFragment(
    private val injectedUserViewModel: UserViewModel? = null
) : Fragment() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var verifyEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var backButton: Button
    private lateinit var errorText: TextView
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        userViewModel = injectedUserViewModel ?: ViewModelProvider(requireActivity())[UserViewModel::class.java]

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        database = Firebase.database.reference
        usernameEditText = view.findViewById(R.id.usernameEditText2)
        passwordEditText = view.findViewById(R.id.passwordEditText2)
        verifyEditText = view.findViewById(R.id.verifyPasswordText)
        loginButton = view.findViewById(R.id.continueButton)
        backButton = view.findViewById(R.id.backButton)
        errorText = view.findViewById(R.id.errorText2)
        errorText.visibility = View.GONE
        backButton.setOnClickListener {
            //val scope = CoroutineScope(Dispatchers.Main)
            //scope.launch {
            //    val intent = Intent(context, MainActivity::class.java)
            //    startActivity(intent)
            //}
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
        loginButton.setOnClickListener {
            createAccount()
        }

        usernameEditText.doAfterTextChanged {
            errorText.visibility = View.GONE
        }

        passwordEditText.doAfterTextChanged {
            errorText.visibility = View.GONE
        }

        verifyEditText.doAfterTextChanged {
            errorText.visibility = View.GONE
        }


    }

    fun createAccount() {
        val user = usernameEditText.getText().toString()
        val pass = passwordEditText.getText().toString()
        val verify = verifyEditText.getText().toString()
        if (user == "") {
            errorText.text = "Username is empty!"
            errorText.visibility = View.VISIBLE
            return
        }
        else if (pass == "") {
            errorText.text = "Password is empty!"
            errorText.visibility = View.VISIBLE
            return
        }
        else if (pass.compareTo(verify) != 0) {
            //Toast.makeText(context, "passwords do not match", Toast.LENGTH_LONG).show()
            errorText.text = "Passwords do not match!"
            errorText.visibility = View.VISIBLE
            return
        }
        database.child("users").child(user).setValue(hash(pass))
            .addOnSuccessListener {
                userViewModel.setUser(UserState(0, user, hash(pass)))
                findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
                //val scope = CoroutineScope(Dispatchers.Main)
                //scope.launch {
                    //val intent = Intent(context, HomeActivity::class.java)
                    //startActivity(intent)
                //}
            }
            .addOnFailureListener {
                errorText.text = "Could not create account!"
                errorText.visibility = View.VISIBLE
                //Toast.makeText(context, "failed to add user", Toast.LENGTH_LONG).show()
            }
    }

    private fun hash(input: String): String {
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

}