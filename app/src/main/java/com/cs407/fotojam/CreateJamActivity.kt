package com.cs407.fotojam

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateJamActivity : AppCompatActivity() {

    private lateinit var jamStartDateTime: LocalDateTime
    private lateinit var jamDescriptionEditText: EditText
    private lateinit var jamNameEditText: EditText
    private lateinit var joinCodeEditText: EditText
    private lateinit var jamTitle: String
    private lateinit var jamDescription: String
    private lateinit var joinCode: String
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_jam)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = Firebase.database.reference
        jamNameEditText = findViewById(R.id.jamNameEditText)
        joinCodeEditText = findViewById(R.id.JoinCodeEditText)
        jamDescriptionEditText = findViewById(R.id.jamDescriptionEditText)

        val createButton: Button = findViewById<Button>(R.id.submitJamToDBButton)
        createButton.setOnClickListener {
            jamTitle = jamNameEditText.text.toString()
            joinCode = joinCodeEditText.text.toString()
            jamDescription = jamDescriptionEditText.text.toString()

            if (joinCode.isBlank() or jamTitle.isBlank()) {
                Toast.makeText(this, "Title or code is blank", Toast.LENGTH_SHORT).show()
            }
            else if (joinCode.toInt().toString() != joinCode) {
                Toast.makeText(this, "invalid join code", Toast.LENGTH_LONG).show()
            }
            else {
                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    database.child("jams").child(joinCode).get()
                        .addOnSuccessListener { dataSnapshot ->
                            if (dataSnapshot.exists()) {
                                Toast.makeText(this@CreateJamActivity, "Jam code already being used!", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                database.child("jams").child(joinCode).child("title").setValue(jamTitle)
                                database.child("jams").child(joinCode).child("description").setValue(jamDescription)
                                database.child("jams").child(joinCode).child("phase").setValue(0)
                                intent.getStringExtra("username")
                                    ?.let { it1 -> database.child("users").child(it1).child("jams").child(joinCode).setValue("100")}
                                finish()
                                Toast.makeText(this@CreateJamActivity, "Jam created!", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            // submit request to DB with provided params
            //Toast.makeText(this, "Jam \"Created\"", Toast.LENGTH_SHORT).show()
            //finish()
        }

    }

//    private fun getStartDateTime() {
//        val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
//            .Builder
//            .datePicker()
//            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
//            .setTitleText("Select a start date")
//            .build()
//        datePicker.show(supportFragmentManager, "DATE_PICKER")
//
//        datePicker.addOnPositiveButtonClickListener {
//            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//            val date = sdf.format(it)
//
//            val timePicker: MaterialTimePicker = MaterialTimePicker
//                .Builder()
//                .setTitleText("Select a start time")
//                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
//                .build()
//            timePicker.show(supportFragmentManager, "TIME_PICKER")
//
//            timePicker.addOnPositiveButtonClickListener {
//                timePicker.hour     // returns the selected hour
//                timePicker.minute   // returns the selected minute
//            }
//        }
//    }
}