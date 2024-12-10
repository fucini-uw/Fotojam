package com.cs407.fotojam

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.galleryRecyclerView)

        // placeholder data using drawable
        val pictureList = listOf(
            R.drawable.placeholder1,
            R.drawable.placeholder2,
            R.drawable.placeholder3
        )

        // set up RecyclerView with adapter
//        val adapter = PictureRatingAdapter(pictureList) { position, rating ->
//            // handle rating change for now
//            println("Image at position $position rated $rating stars")
//        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerView.adapter = adapter
    }
}