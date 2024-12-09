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
        // TODO: Change title and subtitle based on information in database query

        holder.titleText.text = jamInfoList[position][0]
        holder.subTitleText.text = jamInfoList[position][1]

        val jamID: Int = jamInfoList[position][2].toInt()
        val jamName: String = jamInfoList[position][0]
        val jamDescription: String = jamInfoList[position][3]

        holder.viewButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, JamActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("jamId", jamID)
            intent.putExtra("jamName", jamName)
            intent.putExtra("jamDescription", jamDescription)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = jamInfoList.size
}
