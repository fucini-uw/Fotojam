package com.cs407.fotojam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.fotojam.R

class RatingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        val recyclerView = findViewById<RecyclerView>(R.id.pictureRecyclerView)

        // placeholder data using drawable
        val pictureList = listOf(
            R.drawable.placeholder1,
            R.drawable.placeholder2,
            R.drawable.placeholder3
        )

        // set up RecyclerView with adapter
        val adapter = PictureRatingAdapter(pictureList) { position, rating ->
            // handle rating change for now
            println("Image at position $position rated $rating stars")
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}
