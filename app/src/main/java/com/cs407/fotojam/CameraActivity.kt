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
        saveButton.setOnClickListener { savePhotoClick() }
        saveButton.isEnabled = false

        findViewById<Button>(R.id.take_photo_button).setOnClickListener { takePhotoClick() }

        brightnessSeekBar = findViewById(R.id.brightness_seek_bar)
        brightnessSeekBar.visibility = View.INVISIBLE

        brightnessSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                changeBrightness(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun takePhotoClick() {

        // Create the File for saving the photo
        photoFile = createImageFile()

        // Create a content URI to grant camera app write permission to photoFile
        val photoUri = FileProvider.getUriForFile(
            this, "com.cs407.fotojam.fileprovider", photoFile!!)

        // Start camera app
        takePicture.launch(photoUri)
    }

    private val takePicture = registerForActivityResult(
        TakePicture()
    ) { success ->
        if (success) {
            displayPhoto()
            brightnessSeekBar.progress = 100
            brightnessSeekBar.visibility = View.VISIBLE
            changeBrightness(brightnessSeekBar.progress)
            saveButton.isEnabled = true
        }
    }

    private fun createImageFile(): File {

        // Create a unique image filename
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFilename = "photo_$timeStamp.jpg"

        // Get file path where the app can save a private image
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, imageFilename)
    }

    private fun displayPhoto() {
        // Get ImageView dimensions
        val targetWidth = photoImageView.width
        val targetHeight = photoImageView.height

        // Get bitmap dimensions
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(photoFile!!.absolutePath, bmOptions)
        val photoWidth = bmOptions.outWidth
        val photoHeight = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoWidth / targetWidth, photoHeight / targetHeight)

        // Decode the image file into a smaller bitmap that fills the ImageView
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        val bitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath, bmOptions)

        // Display smaller bitmap
        photoImageView.setImageBitmap(bitmap)
    }

    private fun changeBrightness(brightness: Int) {

        // 100 is the middle value
        if (brightness > 100) {
            // Add color
            val addMult = brightness / 100f - 1
            addColor = Color.argb(
                255, (255 * addMult).toInt(), (255 * addMult).toInt(),
                (255 * addMult).toInt()
            )
            multColor = -0x1
        } else {
            // Scale color down
            val brightMult = brightness / 100f
            multColor = Color.argb(
                255, (255 * brightMult).toInt(), (255 * brightMult).toInt(),
                (255 * brightMult).toInt()
            )
            addColor = 0
        }

        val colorFilter = LightingColorFilter(multColor, addColor)
        photoImageView.colorFilter = colorFilter
    }

    private fun savePhotoClick() {

        // Don't allow Save button to be pressed while image is saving
        saveButton.isEnabled = false

        if (photoFile != null) {

            // Save image in background thread
            CoroutineScope(Dispatchers.Main).launch {
                saveAlteredPhoto(photoFile!!, multColor, addColor)

                // Show message
                Toast.makeText(applicationContext, R.string.photo_saved, Toast.LENGTH_LONG).show()

                // Allow Save button to be clicked again
                saveButton.isEnabled = true
            }
        }

        // navigate back to jam activity
        val intent = Intent(applicationContext, JamActivity::class.java)
        startActivity(intent)
    }

    private suspend fun saveAlteredPhoto(photoFile: File, filterMultColor: Int,
                                         filterAddColor: Int) = withContext(Dispatchers.IO) {
        // Read original image
        val origBitmap = BitmapFactory.decodeFile(photoFile.absolutePath, null)

        // Create a new origBitmap with the same dimensions as the original
        val alteredBitmap = Bitmap.createBitmap(origBitmap.width, origBitmap.height,
            origBitmap.config)

        // Draw original origBitmap on canvas and apply the color filter
        val canvas = Canvas(alteredBitmap)
        val paint = Paint()
        val colorFilter = LightingColorFilter(filterMultColor, filterAddColor)
        paint.colorFilter = colorFilter
        canvas.drawBitmap(origBitmap, 0f, 0f, paint)

        // Create an entry for the MediaStore
        val imageValues = ContentValues()
        imageValues.put(MediaStore.MediaColumns.DISPLAY_NAME, photoFile.name)
        imageValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        // Insert a new row into the MediaStore
        val resolver = this@CameraActivity.applicationContext.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageValues)

        // Save bitmap as JPEG
        uri?.let {
            runCatching {
                resolver.openOutputStream(it).use { outStream ->
                    if (outStream != null) {
                        alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                    }
                }
            }
        }
    }
}