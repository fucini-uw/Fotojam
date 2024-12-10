package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage

class RatingActivity : AppCompatActivity() {

    private lateinit var intent: Intent
    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var database: DatabaseReference
    private lateinit var username: String
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        database = Firebase.database.reference

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
        username = intent.getStringExtra("username").toString()
        val jamName = intent.getStringExtra("jamName")
        val description = intent.getStringExtra("jamDescription")

        isAdmin = intent.getBooleanExtra("userIsAdmin", false)

        val adminText: TextView = findViewById(R.id.textView9)
        val adminButton: Button = findViewById(R.id.button2)

        if (!isAdmin) {
            adminText.visibility = View.GONE
            adminButton.visibility = View.GONE
        }

        titleView = findViewById(R.id.textView12)
        descriptionView = findViewById(R.id.textView13)

        titleView.text = jamName
        descriptionView.text = "Submissions have ended. It's time to vote on which picture is best! The description for this jam was:\n\n" + description
        this.runOnUiThread(Runnable {
            Toast.makeText(this, "$id, $username", Toast.LENGTH_SHORT).show()
        })

        val submitRatingsButton: Button = findViewById(R.id.submitRatingsButton)
        val endRatingsButton: Button = findViewById(R.id.button2)

        val stageComplete = intent.getBooleanExtra("stageComplete", false)
        if (stageComplete) {
            submitRatingsButton.visibility = View.GONE
            //val share: Button = findViewById(R.id.button2)
            //share.visibility = View.GONE
            recyclerView.visibility = View.GONE
            descriptionView.text = "The description for this jam was:\n\n" + description
            adminText.text = "You've already sumbitted your ratings, but because you are the creator of this jam, you can still decide when to end voting and finalize the jam results."
        }

        submitRatingsButton.setOnClickListener {
            //TODO: Gather all ratings and submit them to the database
            var state: String = "01"
            if (isAdmin) { state = "11" }
            database.child("users").child(username).child("jams").child(id.toString()).setValue(state)
            Toast.makeText(applicationContext, "Ratings submitted!", Toast.LENGTH_SHORT).show()
            finish()
        }

        endRatingsButton.setOnClickListener {
            database.child("users").child(username).child("jams").child(id.toString()).setValue("10")
            database.child("jams").child(id.toString()).child("phase").setValue(2)
            Toast.makeText(applicationContext, "Votes finalized!", Toast.LENGTH_SHORT).show()
            finish()
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

