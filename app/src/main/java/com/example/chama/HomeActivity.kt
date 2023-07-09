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
        val btnCreateChama = findViewById<Button>(R.id.buttonCreateChama)
        btnCreateChama.setOnClickListener {
            // Handle create new chama button click
            // Start the create new chama activity
            startActivity(Intent(this, CreateNewChamaActivity::class.java))
        }

        val btnListChamas = findViewById<Button>(R.id.buttonMyChamas)
        btnListChamas.setOnClickListener {
            // Handle my chamas button click
            // Start the my chamas activity-list of chamas
            startActivity(Intent(this, MyChamasActivity::class.java))
        }
    }
}
