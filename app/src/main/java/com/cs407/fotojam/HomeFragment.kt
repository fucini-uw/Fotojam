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


class HomeFragment(
    private val injectedUserViewModel: UserViewModel? = null
) : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var database: DatabaseReference
    private lateinit var adapter: FotojamListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var jamInfoList: MutableList<List<String>>

    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = injectedUserViewModel ?: ViewModelProvider(requireActivity())[UserViewModel::class.java]
        jamInfoList = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    fun resetList() {
        jamInfoList = mutableListOf()
    }

    fun addToList(entry: List<String>) {
        jamInfoList.add(entry)
        Log.i("ADDED", jamInfoList.toString())
    }

    fun fetchFromDB() {
        val scope = CoroutineScope(Dispatchers.Main)
        var placeholder = true
        val job = scope.launch {
            database.child("users").child(username).child("jams")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (child in snapshot.children) {
                            val code = child.key
                            val isCreator = child.value
                            if (code != null) {
                                database.child("jams").child(code).get().
                                addOnSuccessListener { dataSnapshot ->
                                    if (dataSnapshot.exists()) {
                                        val jamtitle = dataSnapshot.child("title").value
                                        val description = dataSnapshot.child("description").value
                                        val phase = dataSnapshot.child("phase").value

                                        val jamEntryList = LinkedList<String>()
                                        jamEntryList.add("" + jamtitle)
                                        jamEntryList.add("subtext")
                                        jamEntryList.add("" + code)
                                        jamEntryList.add("" + phase)
                                        jamEntryList.add("" + description)
                                        jamEntryList.add("" + isCreator)
                                        addToList(jamEntryList)
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
        //if (adapter.isInitialized) {

            adapter = FotojamListAdapter(jamInfoList, username)
            //recyclerView.layoutManager = LinearLayoutManager(context)
            val layoutManager = LinearLayoutManager(
                activity
            )
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.adapter = adapter

            //if (this::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        //}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val toolbar: Toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        //setSupportActionToolbar(toolbar)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        username = userViewModel.userState.value.name
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Hello, " + username + "!"
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = "Your current FotoJams:"

        // Set up the recyclerview
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        database = Firebase.database.reference

        fetchFromDB()

        adapter = FotojamListAdapter(jamInfoList, username)
        //recyclerView.layoutManager = LinearLayoutManager(context)
        val layoutManager = LinearLayoutManager(
            activity
        )
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.adapter = adapter

        //if (this::adapter.isInitialized) {
        adapter.notifyDataSetChanged()
        //}

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
    }

    override fun onResume() {
        super.onResume()

        fetchFromDB()

        adapter = FotojamListAdapter(jamInfoList, username)
        //recyclerView.layoutManager = LinearLayoutManager(context)
        val layoutManager = LinearLayoutManager(
            activity
        )
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.adapter = adapter

        //if (this::adapter.isInitialized) {
        adapter.notifyDataSetChanged()

        val scope = CoroutineScope(Dispatchers.Main)
        val job = scope.launch {
            Log.i("CONTENT", jamInfoList.toString())
            Toast.makeText(context, "Fragment resumed", Toast.LENGTH_SHORT).show()
            adapter = FotojamListAdapter(jamInfoList, username)
            recyclerView.adapter = adapter
        }

        //if (this::adapter.isInitialized) {
        //    adapter.notifyDataSetChanged()
        //}
    }
}