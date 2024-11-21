package com.cs407.fotojam

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView

class PictureRatingAdapter(
    private val pictures: List<Int>, // replace with image data type late
    private val onRatingChanged: (position: Int, rating: Float) -> Unit
) : RecyclerView.Adapter<PictureRatingAdapter.PictureViewHolder>() {

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

        holder.imageView.setImageResource(picture) // adjust for data source
        holder.ratingBar.rating = 0f
        holder.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            onRatingChanged(position, rating)
        }

        holder.fullscreenButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, FullscreenImageActivity::class.java)
            intent.putExtra("imageResource", picture)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = pictures.size
}
