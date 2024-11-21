package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProvider

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
                        // TODO: return to login activity
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        //val toolbar: Toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        //setSupportActionBar(toolbar)

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

        val jamDemoButton: Button = view.findViewById(R.id.jamDemoButton)
        jamDemoButton.setOnClickListener {
            val intent = Intent(requireContext(), JamActivity::class.java)
            startActivity(intent)
        }
    }
}