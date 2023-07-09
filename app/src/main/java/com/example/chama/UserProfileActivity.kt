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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        userProfile.onActivityResult(requestCode, resultCode, data)
    }
}