package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        // Find the buttons by their IDs
        val btnViewResults = findViewById<Button>(R.id.btnViewResults)

        // You can add functionality for the "View Gallery" button as well
        btnViewResults.setOnClickListener {
            // Navigate to GalleryActivity (replace with the correct activity if needed)
            val intent = Intent(applicationContext, ResultsActivity::class.java)
            startActivity(intent)
        }
    }
}