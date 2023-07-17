package com.example.chama

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var bio: String
    private lateinit var userProfilePicture: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Retrieve data from intent
        firstName = intent.getStringExtra("firstName") ?: ""
        lastName = intent.getStringExtra("lastName") ?: ""

        val userProfilePictureString = intent.getStringExtra("userProfilePicture")
        userProfilePicture = if (userProfilePictureString != null) Uri.parse(userProfilePictureString) else Uri.EMPTY


        // Create an instance of HeaderFragment and pass the profilePictureUri
        val homeFragment = HomeFragment.newInstance(firstName, lastName, userProfilePicture)
        supportFragmentManager.beginTransaction()
            .replace(R.id.headerContainer, homeFragment)
            .commit()



        // Set click listeners for the buttons
        val buttonCreateChama = findViewById<Button>(R.id.buttonCreateChama)
        buttonCreateChama.setOnClickListener {
            // Handle create new chama button click
            // Start the create new chama activity
            intent.putExtra("firstName", firstName)
            intent.putExtra("lastName", lastName)
            intent.putExtra("userProfilePicture", userProfilePicture?.toString())
            startActivity(Intent(this, CreateNewChamaActivity::class.java))
        }

        val buttonMyChamas = findViewById<Button>(R.id.buttonMyChamas)
        buttonMyChamas.setOnClickListener {
            // Handle my chamas button click
            // Start the my chamas activity
            startActivity(Intent(this, MyChamasActivity::class.java))
        }
    }
}
