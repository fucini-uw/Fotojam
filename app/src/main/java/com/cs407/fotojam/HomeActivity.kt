package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity(
    private val injectedUserViewModel: UserViewModel? = null
) : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.home_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_home)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val createJamButton: Button = findViewById(R.id.createJamButton)
        createJamButton.setOnClickListener {
            val intent = Intent(applicationContext, CreateJamActivity::class.java)
            startActivity(intent)
        }

        val joinJamButton: Button = findViewById(R.id.joinJamButton)
        joinJamButton.setOnClickListener {
            val intent = Intent(applicationContext, JoinJamActivity::class.java)
            startActivity(intent)
        }

        val jamDemoButton: Button = findViewById(R.id.jamDemoButton)
        jamDemoButton.setOnClickListener {
            val intent = Intent(applicationContext, JamActivity::class.java)
            startActivity(intent)
        }
    }
}