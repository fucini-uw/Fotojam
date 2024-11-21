package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

class SignupActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var verifyEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var backButton: Button
    private lateinit var errorText: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = Firebase.database.reference
        usernameEditText = findViewById(R.id.usernameEditText2)
        passwordEditText = findViewById(R.id.passwordEditText2)
        verifyEditText = findViewById(R.id.verifyPasswordText)
        loginButton = findViewById(R.id.continueButton)
        backButton = findViewById(R.id.backButton)
        errorText = findViewById(R.id.errorText2)
        errorText.visibility = View.GONE
        backButton.setOnClickListener {
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
        loginButton.setOnClickListener {
            createAccount()
        }
    }

    fun createAccount() {
        val user = usernameEditText.getText().toString()
        val pass = passwordEditText.getText().toString()
        val verify = verifyEditText.getText().toString()
        if (pass.compareTo(verify) != 0) {
            Toast.makeText(this, "passwords do not match", Toast.LENGTH_LONG).show()
            return
        }
        database.child("users").child(user).setValue(hash(pass))
            .addOnSuccessListener {
                val scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
                    val intent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "failed to add user", Toast.LENGTH_LONG).show()
            }
    }

    private fun hash(input: String): String {
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}