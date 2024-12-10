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


class HomeFragment(
    private val injectedUserViewModel: UserViewModel? = null
) : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var database: DatabaseReference
    private lateinit var adapter: FotojamListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = injectedUserViewModel ?: ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
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
        val username = userViewModel.userState.value.name
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Hello, " + username + "!"
        (activity as AppCompatActivity?)?.supportActionBar?.subtitle = "Your current FotoJams:"

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

        // Set up the recyclerview
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        database = Firebase.database.reference

        var jamInfoList = mutableListOf(
            listOf("Title 1", "Subtitle 1", "0", "0", "Example Description"),
            listOf("Title 2", "Subtitle 2", "1", "Example Description"),
            listOf("Title 3", "Subtitle 3", "2", "Example Description"),
            listOf("title 4", "asfsa", "4", "asdfasdf")
        )
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
                                        listOf(jamtitle, "placeholder", code, phase, description, phase)
                                        jamInfoList.add(listOf(jamtitle, "placeholder", code, phase, description, isCreator) as List<String>)
                                        Log.i(jamtitle.toString(), description.toString())
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
            adapter = FotojamListAdapter(jamInfoList, username)
            //recyclerView.layoutManager = LinearLayoutManager(context)
            val layoutManager = LinearLayoutManager(
                activity
            )
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            recyclerView.setLayoutManager(layoutManager)
            recyclerView.adapter = adapter
        }

        // set up RecyclerView with adapter
//        val adapter = FotojamListAdapter(jamInfoList, username)
//        //recyclerView.layoutManager = LinearLayoutManager(context)
//        val layoutManager = LinearLayoutManager(
//            activity
//        )
//        layoutManager.orientation = LinearLayoutManager.VERTICAL
//        recyclerView.setLayoutManager(layoutManager)
//        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (this::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }
}