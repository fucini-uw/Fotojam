package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
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


class HomeFragment(
    private val injectedUserViewModel: UserViewModel? = null
) : Fragment() {

    private lateinit var userViewModel: UserViewModel

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
                    R.id.action_view_profile -> {
                        // TODO: Launch profile activity
                        true
                    }
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
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Welcome, " + username + "!"

        //val welcomeText: TextView = view.findViewById<TextView>(R.id.WelcomeText)
        //welcomeText.text = "Welcome, " + username + "!"


        val createJamButton: Button = view.findViewById(R.id.createJamButton)
        createJamButton.setOnClickListener {
            val intent = Intent(requireContext(), CreateJamActivity::class.java)
            startActivity(intent)
        }

        val joinJamButton: Button = view.findViewById(R.id.joinJamButton)
        joinJamButton.setOnClickListener {
            val intent = Intent(requireContext(), JoinJamActivity::class.java)
            startActivity(intent)
        }

        // Set up the recyclerview
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val jamInfoList = listOf(
            "Title 1",
            "Title 2",
            "Title 3"
        )

        // set up RecyclerView with adapter
        val adapter = FotojamListAdapter(jamInfoList)
        //recyclerView.layoutManager = LinearLayoutManager(context)
        val layoutManager = LinearLayoutManager(
            activity
        )
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.adapter = adapter
    }
}