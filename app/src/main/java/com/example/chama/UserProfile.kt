package com.example.chama

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserProfile(private val activity: AppCompatActivity) {

    private lateinit var imageViewProfilePicture: ImageView
    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextBio: EditText

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore
//
    init {
        // Initialize Firebase Storage
        val storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()
    }

    fun initializeViews() {
        imageViewProfilePicture = activity.findViewById(R.id.imageViewProfilePicture)
        editTextFirstName = activity.findViewById(R.id.editTextFirstName)
        editTextLastName = activity.findViewById(R.id.editTextLastName)
        editTextBio = activity.findViewById(R.id.editTextBio)
    }

    fun setSelectProfilePictureClickListener(buttonSelectProfilePicture: Button?) {
        buttonSelectProfilePicture?.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            imageViewProfilePicture.setImageURI(selectedImageUri)
        }
    }

    fun saveUserProfile() {
        val firstName = editTextFirstName.text.toString().trim()
        val lastName = editTextLastName.text.toString().trim()
        val bio = editTextBio.text.toString().trim()

        // Save user data to Firestore
        val user = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "bio" to bio
        )

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("userProfiles").document(userId)
                .set(user)
                .addOnSuccessListener {
                    // Successful data save
                    // Show a success message or navigate to another screen
                    Toast.makeText(activity, "User profile saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    // Error occurred while saving data
                    // Handle the error
                    Toast.makeText(activity, "Failed to save user profile", Toast.LENGTH_SHORT).show()
                }
        }
    }




}
