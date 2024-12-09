package com.cs407.fotojam

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
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

class CreateJamActivity : AppCompatActivity() {

    private lateinit var jamStartDateTime: LocalDateTime
    private lateinit var jamTitle: String
    private lateinit var jamDescription: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_jam)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editButton: Button = findViewById<Button>(R.id.editTimeButton)
        editButton.setOnClickListener {
            getStartDateTime()
        }

        val createButton: Button = findViewById<Button>(R.id.submitJamToDBButton)
        createButton.setOnClickListener {
            // TODO: submit request to DB with provided params
            Toast.makeText(this, "Jam \"Created\"", Toast.LENGTH_SHORT).show()
            finish()
        }

        val dropdown = findViewById<Spinner>(R.id.jamDurationDropdown)
        val items = arrayOf("1 Day", "3 Days", "7 Days")
        val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter
    }

    private fun getStartDateTime() {
        val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
            .Builder
            .datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .setTitleText("Select a start date")
            .build()
        datePicker.show(supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.format(it)

            val timePicker: MaterialTimePicker = MaterialTimePicker
                .Builder()
                .setTitleText("Select a start time")
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build()
            timePicker.show(supportFragmentManager, "TIME_PICKER")

            timePicker.addOnPositiveButtonClickListener {
                timePicker.hour     // returns the selected hour
                timePicker.minute   // returns the selected minute
            }
        }
    }
}