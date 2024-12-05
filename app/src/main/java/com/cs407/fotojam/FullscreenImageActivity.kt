package com.cs407.fotojam

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class FullscreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        val fullscreenImageView: ImageView = findViewById(R.id.fullscreenImageView)
        val exitButton: Button = findViewById(R.id.exitButton)

        // Get image URL passed from the adapter
        val imageUrl = intent.getStringExtra("imageUrl")
        Log.d("FullscreenImageActivity", "Received URL: $imageUrl")

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(fullscreenImageView)
        } else {
            Log.e("FullscreenImageActivity", "No URL received.")
        }

        // Set the exit button functionality
        exitButton.setOnClickListener {
            finish()
        }
    }
}