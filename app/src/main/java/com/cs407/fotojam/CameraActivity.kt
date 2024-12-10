package com.cs407.fotojam

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class CameraActivity : AppCompatActivity() {

    private var photoFile: File? = null
    private lateinit var photoImageView: ImageView
    private lateinit var brightnessSeekBar: SeekBar
    private lateinit var saveButton: Button

    // For adding brightness
    private var multColor = -0x1
    private var addColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        photoImageView = findViewById(R.id.photo)
        saveButton = findViewById(R.id.save_button)
        //saveButton.setOnClickListener { savePhotoClick() }
        saveButton.isEnabled = false

        //findViewById<Button>(R.id.take_photo_button).setOnClickListener { takePhotoClick() }

        brightnessSeekBar = findViewById(R.id.brightness_seek_bar)
        brightnessSeekBar.visibility = View.INVISIBLE

        brightnessSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //changeBrightness(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}