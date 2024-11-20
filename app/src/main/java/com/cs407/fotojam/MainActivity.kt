package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var errorText: TextView
    private lateinit var createAcctButton: Button
    private lateinit var database: DatabaseReference

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
        createAcctButton = findViewById(R.id.CreateAcctButton)
        database = Firebase.database.reference
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
                    val e = withContext(Dispatchers.IO) {
                        attemptLogin(userName, userPassword)
                    }
                    if(!e) {
                        Toast.makeText(applicationContext, "incorrect username or password", Toast.LENGTH_LONG).show()
                    } else {
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        startActivity(intent)
                    }
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

                }
            }
        }
        createAcctButton.setOnClickListener {
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                val intent = Intent(applicationContext, SignupActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private suspend fun attemptLogin(user: String, pass: String): Boolean {
        val scope = CoroutineScope(Dispatchers.IO)
        var flag = false
        val job = scope.launch {
            database.child("users").child(user).get()
                .addOnSuccessListener {
                    val pw = it.value.toString()
                    if (pw.compareTo(hash(pass)) == 0) {
                        Log.i("firebase", "pw: " + pw)
                        flag = true
                    } else {
                        Log.e("firebase", "pw: " + pw)
                    }
                }
                .addOnFailureListener {
                    Log.e("firebase", "error retrieving from database")
                }
        }
        runBlocking {
            job.join()
        }
        delay(100)
        return flag
    }

    private fun hash(input: String): String {
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}