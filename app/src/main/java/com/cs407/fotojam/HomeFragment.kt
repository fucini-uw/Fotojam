package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.util.LinkedList
import kotlin.reflect.typeOf

class HomeFragment(
    private val injectedUserViewModel: UserViewModel? = null
) : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var database: DatabaseReference
    private lateinit var adapter: FotojamListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var username: String
    private lateinit var list: MutableList<List<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = injectedUserViewModel ?: ViewModelProvider(requireActivity())[UserViewModel::class.java]
        list = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private suspend fun fetchFromDB() {
        var placeholder = true
        database.child("users").child(username).child("jams")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val code = child.key
                        val isCreator = child.value
                        if (isCreator != null) {
                            Log.i("CREATORTYPE", "" + isCreator.javaClass.kotlin.simpleName)
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

                                    val jamEntryList = listOf("" + jamtitle, "subtext", "" + code, "" + phase, "" + description, "" + isCreator, "" + phaseComplete)
                                    list.add(jamEntryList)

                                    // Let adapter know that content of list has changed
                                    // It will redraw every element in the list (slow!)
                                    // Put this call here because the successListener for firebase
                                    // is asynchronous and can return at any time
                                    adapter.notifyDataSetChanged()
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

        val jamEntryList = listOf("test", "subtext", "111111", "1", "none", "true", "false")
        list.add(jamEntryList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set on click handlers for the buttons
        val createJamButton: Button = view.findViewById(R.id.createJamButton)
        createJamButton.setOnClickListener {
            val intent = Intent(requireContext(), CreateJamActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        val joinJamButton: Button = view.findViewById(R.id.joinJamButton)
        joinJamButton.setOnClickListener {
            val intent = Intent(requireContext(), JoinJamActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        // Add the menu bar
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_activity_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_logout -> {
                        userViewModel.setUser(UserState())
                        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        // Update the toolbar titles
        val toolbar: Toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        //setSupportActionToolbar(toolbar)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        username = userViewModel.userState.value.name
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Hello, " + username + "!"
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = "Your current FotoJams:"

        // Set up the recyclerview
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        database = Firebase.database.reference

        //list = mutableListOf()
        adapter = FotojamListAdapter(list, username)
        val layoutManager = LinearLayoutManager(
            activity
        )
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.adapter = adapter
        //adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()

        CoroutineScope(Dispatchers.Main).launch {
            list = mutableListOf() // Remove all local DB entries on each refresh (slow!)
            fetchFromDB() // Asynchronous
            adapter = FotojamListAdapter(list, username)
            val layoutManager = LinearLayoutManager(
                activity
            )
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}