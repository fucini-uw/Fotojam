package com.cs407.fotojam

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PictureRatingAdapter(
    private val pictures: List<String>,
    private val onRatingChanged: (position: Int, rating: Float) -> Unit
) : RecyclerView.Adapter<PictureRatingAdapter.PictureViewHolder>() {

    private val ratingsMap = mutableMapOf<Int, Float>()

    inner class PictureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImageView)
        val ratingBar: RatingBar = view.findViewById(R.id.itemRatingBar)
        val fullscreenButton: Button = view.findViewById(R.id.itemFullscreenButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_rate_individual, parent, false)
        return PictureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val picture = pictures[position]

        // Load image
        Glide.with(holder.itemView.context)
            .load(picture)
            .into(holder.imageView)

        holder.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            ratingsMap[position] = rating
            onRatingChanged(position, rating)
        }

        holder.fullscreenButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, FullscreenImageActivity::class.java)
            intent.putExtra("imageUrl", picture)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = pictures.size

    fun getRatings(): Map<Int, Float> = ratingsMap
}
