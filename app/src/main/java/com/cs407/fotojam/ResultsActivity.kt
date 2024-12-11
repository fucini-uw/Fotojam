package com.cs407.fotojam

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jam_results)

        fetchTopThreeImages()
    }

    private fun fetchTopThreeImages() {
        val intent = intent
        val jamId = intent.getIntExtra("jamId", -1).toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("${jamId}/")
        val imageRatings = mutableListOf<Triple<String, Float, String>>() // URL, average rating, username

        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                val tasks = listResult.items.map { imageRef ->
                    imageRef.metadata.addOnSuccessListener { metadata ->
                        val totalStars = metadata.getCustomMetadata("totalStars")?.toFloat() ?: 0f
                        val numRatings = metadata.getCustomMetadata("numRatings")?.toFloat() ?: 1f // Avoid divide by 0
                        val username = metadata.getCustomMetadata("username") ?: "Unknown"

                        val averageRating = if (numRatings > 0) totalStars / numRatings else 0f
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            imageRatings.add(Triple(uri.toString(), averageRating, username))

                            // Once all items are processed, sort and display top 3
                            if (imageRatings.size == listResult.items.size) {
                                displayTopThreeImages(imageRatings.sortedByDescending { it.second }.take(3))
                            }
                        }
                    }
                }

                if (tasks.isEmpty()) {
                    Toast.makeText(this, "No images available.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch images: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun displayTopThreeImages(topImages: List<Triple<String, Float, String>>) {
        if (topImages.isNotEmpty()) {
            val (goldUrl, goldRating, goldUsername) = topImages.getOrNull(0) ?: Triple("", 0f, "N/A")
            val (silverUrl, silverRating, silverUsername) = topImages.getOrNull(1) ?: Triple("", 0f, "N/A")
            val (bronzeUrl, bronzeRating, bronzeUsername) = topImages.getOrNull(2) ?: Triple("", 0f, "N/A")

            // Gold
            findViewById<ImageView>(R.id.ivPhotoGold)?.let {
                Glide.with(this).load(goldUrl).into(it)
            }
            findViewById<TextView>(R.id.tvNameGold)?.text = "Name: $goldUsername"
            findViewById<TextView>(R.id.tvRatingGold)?.text = "Rating: ${"%.1f".format(goldRating)}"

            // Silver
            findViewById<ImageView>(R.id.ivPhotoSilver)?.let {
                Glide.with(this).load(silverUrl).into(it)
            }
            findViewById<TextView>(R.id.tvNameSilver)?.text = "Name: $silverUsername"
            findViewById<TextView>(R.id.tvRatingSilver)?.text = "Rating: ${"%.1f".format(silverRating)}"

            // Bronze
            findViewById<ImageView>(R.id.ivPhotoBronze)?.let {
                Glide.with(this).load(bronzeUrl).into(it)
            }
            findViewById<TextView>(R.id.tvNameBronze)?.text = "Name: $bronzeUsername"
            findViewById<TextView>(R.id.tvRatingBronze)?.text = "Rating: ${"%.1f".format(bronzeRating)}"
        } else {
            Toast.makeText(this, "No images to display.", Toast.LENGTH_SHORT).show()
        }
    }
}

