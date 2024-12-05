package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class JamActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_jam)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val jamDemoButton: Button = findViewById(R.id.capturePhoto)
        jamDemoButton.setOnClickListener {
            val intent = Intent(applicationContext, CameraActivity::class.java)
            startActivity(intent)
        }
        val ratingDemoButton: Button = findViewById(R.id.ratingButton)
        ratingDemoButton.setOnClickListener {
            val intent = Intent(applicationContext, RatingActivity::class.java)
            startActivity(intent)
        }

        val resultsDemoButton: Button = findViewById(R.id.resultsButton)
        resultsDemoButton.setOnClickListener {
            val intent = Intent(applicationContext, ResultsActivity::class.java)
            startActivity(intent)
        }
    }
}