package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jam_results)

        // Find the buttons by their IDs
        val btnViewGallery = findViewById<Button>(R.id.btnViewGallery)
        val btnReturnHome = findViewById<Button>(R.id.btnReturnHome)

        // Set a click listener for the "Return Home" button
        btnReturnHome.setOnClickListener {
            // Navigate to HomeActivity
            val intent = Intent(applicationContext, JamActivity::class.java)
            startActivity(intent)
        }

        // You can add functionality for the "View Gallery" button as well
        btnViewGallery.setOnClickListener {
            // Navigate to GalleryActivity (replace with the correct activity if needed)
            val intent = Intent(applicationContext, GalleryActivity::class.java)
            startActivity(intent)
        }
    }
}
