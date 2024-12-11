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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata

class RatingActivity : AppCompatActivity() {

    private lateinit var intent: Intent
    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var imageUrls: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        val recyclerView = findViewById<RecyclerView>(R.id.pictureRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch images and pass them to the adapter
        fetchImagesFromFirebase { fetchedImageUrls ->
            imageUrls = fetchedImageUrls
            val adapter = PictureRatingAdapter(imageUrls) { position, rating ->
                // handle rating change
                println("Image at position $position rated $rating stars")
            }
            recyclerView.adapter = adapter
        }

        intent = getIntent()

        val id = intent.getIntExtra("jamId", -1)
        val name = intent.getStringExtra("username")
        val jamName = intent.getStringExtra("jamName")
        val description = intent.getStringExtra("jamDescription")

        val isAdmin = intent.getBooleanExtra("userIsAdmin", false)

        if (!isAdmin) {
            val adminText: TextView = findViewById(R.id.textView9)
            val adminButton: Button = findViewById(R.id.button2)
            adminText.visibility = View.GONE
            adminButton.visibility = View.GONE
        }

        titleView = findViewById(R.id.textView12)
        descriptionView = findViewById(R.id.textView13)

        titleView.text = jamName
        descriptionView.text = "Submissions have ended. It's time to vote on which picture is best! The description for this jam was:\n\n" + description
        this.runOnUiThread(Runnable {
            Toast.makeText(this, "$id, $name", Toast.LENGTH_SHORT).show()
        })

        // Handle submit button click
        val submitButton = findViewById<Button>(R.id.submitRatingsButton)
        submitButton.setOnClickListener {
            val ratings = (recyclerView.adapter as PictureRatingAdapter).getRatings()

            ratings.forEach { (position, rating) ->
                val pictureUrl = imageUrls[position]
                val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(pictureUrl)

                imageRef.metadata.addOnSuccessListener { metadata ->
                    val numRatings = metadata.getCustomMetadata("numRatings")?.toInt() ?: 0
                    val totalStars = metadata.getCustomMetadata("totalStars")?.toInt() ?: 0

                    val newNumRatings = numRatings + 1
                    val newTotalStars = totalStars + rating.toInt()

                    val updatedMetadata = StorageMetadata.Builder()
                        .setCustomMetadata("numRatings", newNumRatings.toString())
                        .setCustomMetadata("totalStars", newTotalStars.toString())
                        .build()

                    imageRef.updateMetadata(updatedMetadata)
                        .addOnSuccessListener {
                            Log.d("RatingActivity", "Updated metadata for image at $pictureUrl")
                        }
                        .addOnFailureListener {
                            Log.e("RatingActivity", "Failed to update metadata for image at $pictureUrl", it)
                        }
                }.addOnFailureListener {
                    Log.e("RatingActivity", "Failed to fetch metadata for image at $pictureUrl", it)
                }
            }

            Toast.makeText(this, "Ratings submitted successfully!", Toast.LENGTH_SHORT).show()
        }

        val endJamButton = findViewById<Button>(R.id.button2)
        endJamButton.setOnClickListener {
            // Prepare intent to navigate to ResultsActivity
            val resultsIntent = Intent(this, ResultsActivity::class.java)

            // Pass relevant data to ResultsActivity
            resultsIntent.putExtra("jamId", intent.getIntExtra("jamId", -1))
            resultsIntent.putExtra("username", intent.getStringExtra("username"))
            resultsIntent.putExtra("jamName", intent.getStringExtra("jamName"))
            resultsIntent.putExtra("jamDescription", intent.getStringExtra("jamDescription"))

            // Start the ResultsActivity
            startActivity(resultsIntent)
            finish()
        }
    }

    private fun fetchImagesFromFirebase(onImagesFetched: (List<String>) -> Unit) {
        intent = getIntent()

        val jamId = intent.getIntExtra("jamId", -1).toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("${jamId}/")
        val imageUrls = mutableListOf<String>()

        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                val tasks = listResult.items.map { imageRef ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        imageUrls.add(uri.toString())
                    }
                }

                tasks.last().addOnSuccessListener {
                    onImagesFetched(imageUrls)  // Return the list of image URLs
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseStorage", "Failed to list files: ${exception.message}")
            }
    }


}

//need onclicklistener for submit rating button, have in send the rating value to custom data and increment the
//total ratings. In results activity, get the info and calculate the overall rating average