package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class JamActivity : AppCompatActivity() {

    private lateinit var intent: Intent
    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView

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

        intent = getIntent();

        val id = intent.getIntExtra("jamId", -1)
        val name = intent.getStringExtra("username")
        val jamName = intent.getStringExtra("jamName")
        val description = intent.getStringExtra("jamDescription")

        titleView = findViewById(R.id.textView2)
        descriptionView = findViewById(R.id.textView3)

        titleView.text = jamName
        descriptionView.text = "The theme for this jam is:\n\n" + description
        this.runOnUiThread(Runnable {
            Toast.makeText(this, "$id, $name", Toast.LENGTH_SHORT).show()
        })

    }
}