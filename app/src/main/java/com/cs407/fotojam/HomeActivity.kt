package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_view_profile -> {
                // TODO: Launch profile activity
                true
            }
            R.id.action_logout -> {
                //userViewModel.setUser(UserState())
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
    }
}