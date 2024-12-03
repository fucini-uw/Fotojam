package com.cs407.fotojam

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FullscreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        val fullscreenImageView: ImageView = findViewById(R.id.fullscreenImageView)
        val exitButton: Button = findViewById(R.id.exitButton)

        // get image resource passed from the adapter
        val imageResource = intent.getIntExtra("imageResource", -1)
        if (imageResource != -1) {
            fullscreenImageView.setImageResource(imageResource)
        }

        exitButton.setOnClickListener {
            finish()
        }
    }
}