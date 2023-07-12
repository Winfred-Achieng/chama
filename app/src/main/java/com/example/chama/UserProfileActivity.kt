package com.example.chama

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.widget.Button

class UserProfileActivity : AppCompatActivity() {

    private lateinit var userProfile: UserProfile
    private var profilePictureUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Initialize views and set click listeners
        userProfile = UserProfile(this)
        userProfile.initializeViews()
        userProfile.setSelectProfilePictureClickListener(findViewById(R.id.buttonSelectProfilePicture))
        userProfile.setSaveClickListener(findViewById(R.id.buttonSave))
        userProfile.setDeleteProfilePictureClickListener(findViewById(R.id.buttonDeleteProfilePicture))

        // Retrieve the profile picture URI from Firestore or any other source
        // and assign it to the profilePictureUri variable
        // For example:
        // profilePictureUri = Uri.parse("your_profile_picture_uri_here")

        // Create an instance of HeaderFragment and pass the profilePictureUri
        val headerFragment = HeaderFragment(profilePictureUri)
        supportFragmentManager.beginTransaction()
            .replace(R.id.headerFragmentContainer, headerFragment)
            .commit()

        // Set click listener for the "Save" button
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonSave.setOnClickListener {
            userProfile.saveUserProfile()
        }

        // Set click listener for the "Start Chat" button
        val buttonStartChat = findViewById<Button>(R.id.buttonSave)
        buttonStartChat.setOnClickListener {
            val firstName = userProfile.getFirstName()
            val lastName = userProfile.getLastName()
            userProfile.startChatActivity(firstName, lastName)
        }
    }
}
