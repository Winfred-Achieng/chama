package com.example.chama

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
//add
    fun setSaveClickListener(buttonSave: Button) {
        buttonSave.setOnClickListener {
            saveUserProfile()
        }
    }

    fun setDeleteProfilePictureClickListener(buttonDeleteProfilePicture: Button) {
        buttonDeleteProfilePicture.setOnClickListener {
            deleteProfilePicture()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST)
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
            firestore.collection("userProfiles").document(userId)
                .set(user)
                .addOnSuccessListener {
                    // Successful data save
                    // Show a success message or navigate to another screen

                    // Start ChatActivity
                    startChatActivity(firstName, lastName)
                }
                .addOnFailureListener {
                    // Error occurred while saving data
                    // Handle the error
                }
        }
    }

    private fun deleteProfilePicture() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            // Delete the profile picture from Firebase Storage
            storageRef.child("profilePictures/$userId").delete()
                .addOnSuccessListener {
                    // Successful deletion
                    // Show a success message or update the UI accordingly
                }
                .addOnFailureListener {
                    // Error occurred while deleting the profile picture
                    // Handle the error
                }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            imageViewProfilePicture.setImageURI(selectedImageUri)

            // Upload the selected image to Firebase Storage
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid

            if (userId != null && selectedImageUri != null) {
                val imageRef = storageRef.child("profilePictures/$userId")

                imageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Image upload success
                        // Retrieve the download URL
                        imageRef.downloadUrl
                            .addOnSuccessListener { uri ->
                                // Update the user document in Firestore with the profile picture URL
                                val user = hashMapOf<String, Any>(
                                    "profilePicture" to uri.toString()
                                    // Add other fields if needed
                                )

                                firestore.collection("userProfiles").document(userId)
                                    .set(user)
                                    .addOnSuccessListener {
                                        // Profile picture URL saved to Firestore
                                    }
                                    .addOnFailureListener { exception ->
                                        // Error occurred while saving profile picture URL to Firestore
                                    }
                            }
                            .addOnFailureListener { exception ->
                                // Error occurred while retrieving the download URL
                            }
                    }
                    .addOnFailureListener { exception ->
                        // Error occurred while uploading the image
                    }
            }
        }
    }

    fun startChatActivity(firstName: String, lastName: String) {
        val intent = Intent(activity, ChatActivity::class.java)
        intent.putExtra("firstName", firstName)
        intent.putExtra("lastName", lastName)
        activity.startActivity(intent)
    }

    fun getFirstName(): String {
        return editTextFirstName.text.toString().trim()
    }

    fun getLastName(): String {
        return editTextLastName.text.toString().trim()
    }
}
