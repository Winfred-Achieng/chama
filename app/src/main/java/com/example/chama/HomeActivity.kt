package com.example.chama

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Create an instance of HeaderFragment and pass the profilePictureUri
        val profilePictureUri = Uri.parse("your_profile_picture_uri_here")
        val headerFragment = HeaderFragment(profilePictureUri)
        supportFragmentManager.beginTransaction()
            .replace(R.id.headerContainer, headerFragment)
            .commit()

        // Set click listeners for the buttons
        val buttonCreateChama = findViewById<Button>(R.id.buttonCreateChama)
        buttonCreateChama.setOnClickListener {
            // Handle create new chama button click
            // Start the create new chama activity
            startActivity(Intent(this, CreateChamaActivity::class.java))
        }

        val buttonMyChamas = findViewById<Button>(R.id.buttonMyChamas)
        buttonMyChamas.setOnClickListener {
            // Handle my chamas button click
            // Start the my chamas activity
            startActivity(Intent(this, MyChamasActivity::class.java))
        }
    }
}
