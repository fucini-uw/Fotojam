package com.cs407.fotojam

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage

class RatingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        val recyclerView = findViewById<RecyclerView>(R.id.pictureRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchImagesFromFirebase { imageUrls ->
            val adapter = PictureRatingAdapter(imageUrls) { position, rating ->
                // Handle rating change
                println("Image at position $position rated $rating stars")
            }
            recyclerView.adapter = adapter
        }
    }

    private fun fetchImagesFromFirebase(onImagesFetched: (List<String>) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/")
        val imageUrls = mutableListOf<String>()

        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                val tasks = listResult.items.map { imageRef ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        imageUrls.add(uri.toString())
                    }
                }

                // Wait for all URLs to be fetched
                tasks.last().addOnSuccessListener {
                    onImagesFetched(imageUrls)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseStorage", "Failed to list files: ${exception.message}")
            }
    }
}

