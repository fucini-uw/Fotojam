package com.cs407.fotojam

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
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JoinJamActivity : AppCompatActivity() {

    private lateinit var joinCodeEditText: EditText
    private lateinit var joinCode: String
    private lateinit var database: DatabaseReference
    //private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_join_jam)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        joinCodeEditText = findViewById(R.id.jamCodeEditTextNumber)

        val joinJamErrorText: TextView = findViewById(R.id.joinJamErrorText)
        joinJamErrorText.visibility = View.GONE
        joinCodeEditText.doAfterTextChanged {
            joinJamErrorText.visibility = View.GONE
        }

        database = Firebase.database.reference
        //userViewModel = injectedUserViewModel ?: ViewModelProvider(requireActivity())[UserViewModel::class.java]
        val username = intent.getStringExtra("username")
        val joinButton: Button = findViewById(R.id.joinJamSubmitButton)
        joinButton.setOnClickListener {
            joinCode = joinCodeEditText.text.toString()
            if (joinCode.isEmpty()) {
                //Toast.makeText(this, "Join code is blank", Toast.LENGTH_SHORT).show()
                joinJamErrorText.text = "Join code is blank!"
                joinJamErrorText.visibility = View.VISIBLE
            }
            else {
                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    database.child("jams").child(joinCode).get()
                        .addOnSuccessListener { dataSnapshot ->
                            if (dataSnapshot.exists()) {
                                if (username != null) {
                                    database.child("users").child(username).child("jams").child(joinCode).setValue("00")
                                }
                                Toast.makeText(this@JoinJamActivity, "Jam joined!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            else {
                                //database.child("jams").child(joinCode).child("title").setValue(jamTitle)
                                //Toast.makeText(this@JoinJamActivity, "invalid join code", Toast.LENGTH_SHORT).show()
                                //Toast.makeText(this@CreateJamActivity, "Jam \"Created\"", Toast.LENGTH_SHORT).show()
                                joinJamErrorText.text = "Invalid join code!"
                                joinJamErrorText.visibility = View.VISIBLE

                            }
                        }
                }
            }
        }
    }
}