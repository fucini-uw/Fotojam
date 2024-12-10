package com.cs407.fotojam

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FotojamListAdapter(
    private val jamInfoList: List<List<String>>,
    private val username: String
) : RecyclerView.Adapter<FotojamListAdapter.ListItemHolder>() {

    inner class ListItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val subTitleText: TextView = view.findViewById(R.id.subtitleText)
        val viewButton: Button = view.findViewById(R.id.viewJamButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_main_menu_item, parent, false)
        return ListItemHolder(view)
    }

    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
        val jamID: Int = jamInfoList[position][2].toInt()
        val jamStage: Int = jamInfoList[position][3].toInt()
        val jamName: String = jamInfoList[position][0]
        val jamDescription: String = jamInfoList[position][4]
        var admin: Boolean = false

        if (jamInfoList[position][5] == "true") admin = true

        holder.titleText.text = jamInfoList[position][0]
        holder.subTitleText.text = jamInfoList[position][1]
        if (jamStage == 0) holder.viewButton.text = "Capture"
        if (jamStage == 1) holder.viewButton.text = "Vote"
        if (jamStage >= 2) holder.viewButton.text = "View"

        holder.viewButton.setOnClickListener {
            val context = holder.itemView.context
            var intent: Intent? = null
            if (jamStage == 0) intent = Intent(context, JamActivity::class.java)
            if (jamStage == 1) intent = Intent(context, RatingActivity::class.java)
            if (jamStage >= 2) intent = Intent(context, ResultsActivity::class.java)
            intent?.putExtra("username", username)
            intent?.putExtra("jamId", jamID)
            intent?.putExtra("jamName", jamName)
            intent?.putExtra("jamDescription", jamDescription)
            intent?.putExtra("userIsAdmin", admin)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = jamInfoList.size
}
