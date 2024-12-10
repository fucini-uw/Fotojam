package com.cs407.fotojam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible

class JamActivity : AppCompatActivity() {

    private lateinit var intent: Intent
    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private var jamId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_jam)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        intent = getIntent();
        jamId = intent.getIntExtra("jamId", -1)
        val name = intent.getStringExtra("username")
        val jamDemoButton: Button = findViewById(R.id.capturePhoto)
        jamDemoButton.setOnClickListener {
            val intent = Intent(applicationContext, CameraActivity::class.java)
            intent.putExtra("username", name)
            intent.putExtra("jamId", jamId)
            startActivity(intent)
        }

        val shareButton: Button = findViewById(R.id.shareJamButton)
        shareButton.setOnClickListener{
            if (jamId > -1) {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Join my FotoJam! The join code is: " + jamId.toString())
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }

        val jamName = intent.getStringExtra("jamName")
        val description = intent.getStringExtra("jamDescription")
        val isAdmin = intent.getBooleanExtra("userIsAdmin", false)

        if (!isAdmin) {
            val adminText: TextView = findViewById(R.id.textView)
            val adminButton: Button = findViewById(R.id.button)
            adminText.visibility = View.GONE
            adminButton.visibility = View.GONE
        }

        titleView = findViewById(R.id.textView2)
        descriptionView = findViewById(R.id.textView3)

        titleView.text = jamName
        descriptionView.text = description
        this.runOnUiThread(Runnable {
            Toast.makeText(this, "$jamId, $name", Toast.LENGTH_SHORT).show()
        })
    }
}