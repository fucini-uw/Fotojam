package com.cs407.fotojam

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FotojamListAdapter(
    private var jamInfoList: MutableList<List<String>>,
    private val username: String
) : RecyclerView.Adapter<FotojamListAdapter.ListItemHolder>() {

    private lateinit var database: DatabaseReference

    inner class ListItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.titleText)
        val subTitleText: TextView = view.findViewById(R.id.subtitleText)
        val viewButton: Button = view.findViewById(R.id.viewJamButton)
    }

    private suspend fun fetchFromDB() {
        var placeholder = true
        database.child("users").child(username).child("jams")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val code = child.key
                        val creatorCompleteByteArr: String = child.value.toString()
                        var isCreator: String = "false"
                        var hasSubmitted: String = "false"
                        var hasVoted: String = "false"
                        if (creatorCompleteByteArr != null) {
                            Log.i("CREATORTYPE", creatorCompleteByteArr)
                            val isCreatorChar = creatorCompleteByteArr[0]
                            if (isCreatorChar == '1') { isCreator = "true" }
                            val hasSubmittedChar = creatorCompleteByteArr[1]
                            if (hasSubmittedChar == '1') { hasSubmitted = "true" }
                            val hasVotedChar = creatorCompleteByteArr[2]
                            if (hasVotedChar == '1') { hasVoted = "true" }
                        }
                        if (code != null) {
                            database.child("jams").child(code).get().
                            addOnSuccessListener { dataSnapshot ->
                                if (dataSnapshot.exists()) {
                                    val jamtitle = dataSnapshot.child("title").value
                                    val description = dataSnapshot.child("description").value
                                    val phase = dataSnapshot.child("phase").value

                                    // phaseComplete tracks whether the user has completed the current stage of the jam
                                    // for example, if phase is 0 and phaseComplete is true, the user has submitted a photo
                                    // for the jam. As a result, they will not be able to go back into the jam unless they are the creator
                                    val phaseComplete = "false"

                                    val jamEntryList = listOf("" + jamtitle, "subtext", "" + code, "" + phase, "" + description, "" + isCreator, "" + hasSubmitted, "" + hasVoted)
                                    jamInfoList.add(jamEntryList)

                                    // Let adapter know that content of list has changed
                                    // It will redraw every element in the list (slow!)
                                    // Put this call here because the successListener for firebase
                                    // is asynchronous and can return at any time
                                    notifyDataSetChanged()
                                }
                            }
                        }
                    }
                    placeholder = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "error fetching jams")
                    placeholder = false
                }

            })
        while (placeholder) {
            delay(100)
        }
        //}

        //list.add(jamEntryList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_main_menu_item, parent, false)
        database = Firebase.database.reference
        return ListItemHolder(view)
    }

    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
        val jamID: Int = jamInfoList[position][2].toInt()
        val jamStage: Int = jamInfoList[position][3].toInt()
        val jamName: String = jamInfoList[position][0]
        val jamDescription: String = jamInfoList[position][4]
        var admin: Boolean = false
        if (jamInfoList[position][5] == "true") admin = true
        var submitted: Boolean = false
        if (jamInfoList[position][6] == "true") submitted = true
        var voted: Boolean = false
        if (jamInfoList[position][7] == "true") voted = true

        holder.titleText.text = jamInfoList[position][0]
        holder.subTitleText.text = jamInfoList[position][1]

        if (jamStage == 0 && !submitted){
            holder.viewButton.text = "Capture"
            holder.subTitleText.text = "No submission yet..."
        }
        if (jamStage == 0 && submitted){
            if (!admin) holder.viewButton.visibility = View.GONE
            holder.subTitleText.text = "You've submited a photo!"
        }
        if (jamStage == 1 && !voted){
            holder.viewButton.text = "Vote"
            holder.subTitleText.text = "No ratings yet..."
        }
        if (jamStage == 1 && voted){
            if (!admin) holder.viewButton.visibility = View.GONE
            holder.subTitleText.text = "You've voted on these photos!"
        }
        if (jamStage >= 2) {
            holder.viewButton.text = "Results"
            holder.subTitleText.text = "This jam is complete!"
        }

        var alertTitleText = "ERROR!"
        if (jamStage == 0) alertTitleText = "Do you want to end the submission period?"
        if (jamStage == 1) alertTitleText = "Do you want to end the rating period?"


        val alert: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
        alert.setTitle(alertTitleText)

        // alert.setMessage("Message");
        alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, whichButton ->
            var intent: Intent? = null
            if (jamStage == 0) intent = Intent(holder.itemView.context, JamActivity::class.java)
            if (jamStage == 1) intent = Intent(holder.itemView.context, RatingActivity::class.java)
            if (jamStage >= 2) intent = Intent(holder.itemView.context, ResultsActivity::class.java)
            intent?.putExtra("username", username)
            intent?.putExtra("jamId", jamID)
            intent?.putExtra("jamName", jamName)
            intent?.putExtra("jamDescription", jamDescription)
            intent?.putExtra("userIsAdmin", admin)
            if (jamStage == 0) {
                //intent?.putExtra("stageComplete", submitted)
                database.child("users").child(username).child("jams").child(jamID.toString()).setValue("110")
                database.child("jams").child(jamID.toString()).child("phase").setValue(1)
                Toast.makeText(holder.itemView.context, "Submissions closed!", Toast.LENGTH_SHORT).show()
                jamInfoList = mutableListOf()
                CoroutineScope(Dispatchers.Main).launch {
                    fetchFromDB() // Asynchronous
                }
            }
            if (jamStage == 1) {
                //intent?.putExtra("stageComplete", voted)
                database.child("users").child(username).child("jams").child(jamID.toString()).setValue("111")
                database.child("jams").child(jamID.toString()).child("phase").setValue(2)
                Toast.makeText(holder.itemView.context, "Votes finalized!", Toast.LENGTH_SHORT).show()
                jamInfoList = mutableListOf()
                CoroutineScope(Dispatchers.Main).launch {
                    fetchFromDB() // Asynchronous
                }
                //this.notifyDataSetChanged()
            }
            //holder.itemView.context.startActivity(intent)
        })

        alert.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog, whichButton -> })



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
            intent?.putExtra("stageComplete", false)
            intent?.putExtra("stageComplete", false)
            if (jamStage == 0 && submitted) alert.show()
            else if (jamStage == 1 && voted) alert.show()
            else context.startActivity(intent)
            //if (jamStage == 0) intent?.putExtra("stageComplete", submitted)
            //if (jamStage == 1) intent?.putExtra("stageComplete", voted)

        }
    }

    override fun getItemCount(): Int = jamInfoList.size
}