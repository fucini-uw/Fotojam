package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage

class RatingActivity : AppCompatActivity() {

    private lateinit var intent: Intent
    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView

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

        intent = getIntent();

        val id = intent.getIntExtra("jamId", -1)
        val name = intent.getStringExtra("username")
        val jamName = intent.getStringExtra("jamName")
        val description = intent.getStringExtra("jamDescription")

        titleView = findViewById(R.id.textView12)
        descriptionView = findViewById(R.id.textView13)

        titleView.text = jamName
        descriptionView.text = "Submissions have ended. It's time to vote on which picture is best! The description for this jam was:\n\n" + description
        this.runOnUiThread(Runnable {
            Toast.makeText(this, "$id, $name", Toast.LENGTH_SHORT).show()
        })

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

