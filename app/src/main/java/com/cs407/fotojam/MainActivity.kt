package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        errorText = findViewById(R.id.errorText)
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
                errorText.visibility = View.VISIBLE
            } else {
                // Set the logged-in user in the ViewModel (store user info) (placeholder)
                val scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
                    //if (getUserPasswd(userName, userPassword)) {
                    //val user = noteDB.userDao().getByName(userName)
                    //Log.i("db", user.userId.toString() + " " + user.userName)
                    //userViewModel.setUser(UserState(user.userId, userName, userPassword)) // You will implement this in UserViewModel
                    //Log.i("vm", userViewModel.userState.value.id.toString() + " " + userViewModel.userState.value.name.toString())

                    //findNavController().navigate(R.id.action_loginFragment_to_noteListFragment) // Example navigation action
                    //} else {
                    //errorText.visibility = View.VISIBLE
                    //}
                    // Start main activity if success
                    val intent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun hash(input: String): String {
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}