package com.cs407.fotojam

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JamActivity : AppCompatActivity() {

    private lateinit var intent: Intent
    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private var jamId: Int = -1

    private var photoFile: File? = null
    private lateinit var photoImageView: ImageView
    private lateinit var brightnessSeekBar: SeekBar
    private lateinit var saveButton: Button
    private lateinit var database: DatabaseReference
    private lateinit var username: String
    private var isAdmin: Boolean = false

    // For adding brightness
    private var multColor = -0x1
    private var addColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_jam)

        database = Firebase.database.reference

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val jamDemoButton: Button = findViewById(R.id.capturePhoto)
        jamDemoButton.setOnClickListener {
            val intent = Intent(applicationContext, CameraActivity::class.java)
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

        intent = getIntent();

        jamId = intent.getIntExtra("jamId", -1)
        username = intent.getStringExtra("username").toString()
        val jamName = intent.getStringExtra("jamName")
        val description = intent.getStringExtra("jamDescription")
        isAdmin = intent.getBooleanExtra("userIsAdmin", false)
        val stageComplete = intent.getBooleanExtra("stageComplete", false)

        val adminText: TextView = findViewById(R.id.textView)
        val adminButton: Button = findViewById(R.id.button)

        if (!isAdmin) {
            adminText.visibility = View.GONE
            adminButton.visibility = View.GONE
        }

        titleView = findViewById(R.id.textView2)
        descriptionView = findViewById(R.id.textView3)

        titleView.text = jamName
        descriptionView.text = description

        photoImageView = findViewById(R.id.photo2)
        saveButton = findViewById(R.id.save_button2)
        saveButton.setOnClickListener { savePhotoClick() }
        saveButton.isEnabled = false
        saveButton.visibility = View.GONE

        findViewById<Button>(R.id.capturePhoto).setOnClickListener { takePhotoClick() }

        brightnessSeekBar = findViewById(R.id.brightness_seek_bar2)
        brightnessSeekBar.visibility = View.INVISIBLE

        if (stageComplete) {
            photoImageView.visibility = View.GONE
            val share: Button = findViewById(R.id.shareJamButton)
            share.visibility = View.GONE
            findViewById<Button>(R.id.capturePhoto).visibility = View.GONE
            adminText.text = "You've already sumbitted your photo, but because you are the creator of this jam, you can still decide when to end submissions and start the rating stage."
        }

        brightnessSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                changeBrightness(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        adminButton.setOnClickListener {
            // Set phaseComplete to false
            database.child("users").child(username).child("jams").child(jamId.toString()).setValue("110")
            database.child("jams").child(jamId.toString()).child("phase").setValue(1)
            Toast.makeText(applicationContext, "Submissions closed!", Toast.LENGTH_SHORT).show()
            finish()
        }

        //this.runOnUiThread(Runnable {
        //    Toast.makeText(this, "$jamId, $username", Toast.LENGTH_SHORT).show()
        //})

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
            //brightnessSeekBar.visibility = View.VISIBLE
            changeBrightness(brightnessSeekBar.progress)
            saveButton.isEnabled = true
            saveButton.visibility = View.VISIBLE
            findViewById<Button>(R.id.capturePhoto).visibility = View.GONE
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
                val success = savePhotoToFirebase(photoFile!!, multColor, addColor)

                if (success) {
                    // Show success message
                    //Toast.makeText(applicationContext, R.string.photo_saved, Toast.LENGTH_LONG).show()
                    var state: String = "010"
                    if (isAdmin) { state = "110" }
                    database.child("users").child(username).child("jams").child(jamId.toString()).setValue(state)
                    Toast.makeText(applicationContext, "Photo submitted!", Toast.LENGTH_SHORT).show()
                } else {
                    // Show error message
                    Toast.makeText(applicationContext, R.string.photo_not_saved, Toast.LENGTH_LONG).show()
                }

                // Allow Save button to be clicked again
                saveButton.isEnabled = true

                // Navigate back to Home
                finish()
            }
        }
    }

    private suspend fun savePhotoToFirebase(
        photoFile: File,
        filterMultColor: Int,
        filterAddColor: Int
    ): Boolean = withContext(Dispatchers.IO) {

        // Read and alter the original image
        val origBitmap = BitmapFactory.decodeFile(photoFile.absolutePath, null)

        val alteredBitmap = Bitmap.createBitmap(
            origBitmap.width,
            origBitmap.height,
            origBitmap.config
        )

        val canvas = Canvas(alteredBitmap)
        val paint = Paint()
        val colorFilter = LightingColorFilter(filterMultColor, filterAddColor)
        paint.colorFilter = colorFilter
        canvas.drawBitmap(origBitmap, 0f, 0f, paint)

        // Convert bitmap to a byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageData = byteArrayOutputStream.toByteArray()

        // Get Firebase Storage reference
        intent = getIntent();
        val jamId = intent.getIntExtra("jamId", -1).toString()

        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("${jamId}/${photoFile.name}")


        val username = intent.getStringExtra("username")
        // Create custom metadata
        val metadata = StorageMetadata.Builder()
            .setCustomMetadata("numRatings", "0")
            .setCustomMetadata("totalStars", "0")
            .setCustomMetadata("username", username)
            .build()

        // Upload the image with metadata
        return@withContext try {
            imageRef.putBytes(imageData, metadata).await() // Use Kotlin Coroutines
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}