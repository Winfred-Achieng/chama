package com.example.chama


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
//
class UserProfileActivity : AppCompatActivity() {

    private lateinit var userProfile: UserProfile
    private lateinit var imageViewProfilePicture: ImageView
    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextBio: EditText

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private lateinit var storageRef: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private var isProfilePictureUploaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture)
        editTextFirstName = findViewById(R.id.editTextFirstName)
        editTextLastName = findViewById(R.id.editTextLastName)
        editTextBio = findViewById(R.id.editTextBio)

        userProfile = UserProfile(this)
        userProfile.setSelectProfilePictureClickListener(findViewById(R.id.buttonSelectProfilePicture))

        storageRef = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonSave.setOnClickListener {
            saveUserProfile()
            checkIfAllDataUploaded()
        }

        val buttonSelectProfilePicture = findViewById<Button>(R.id.buttonSelectProfilePicture)
        buttonSelectProfilePicture.setOnClickListener {
            openGallery()
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            imageViewProfilePicture.setImageURI(selectedImageUri)

            uploadImage(selectedImageUri)
        }
    }


    private fun getFilePathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    private fun uploadImage(uri: Uri?) {
        if (uri != null) {
            // Perform the upload with the non-null URI

            // Upload the image to Firebase Storage
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid

            if (userId != null) {
                val imageRef = storageRef.child("profilePictures/$userId")

                imageRef.putFile(uri)
                    .addOnSuccessListener { taskSnapshot ->
                        // Image upload success
                        // Retrieve the download URL
                        imageRef.downloadUrl
                            .addOnSuccessListener { downloadUri ->
                                // Update the user document in Firestore with the profile picture URL
                                val userProfile = hashMapOf<String, Any>(
                                    "firstName" to editTextFirstName.text.toString().trim(),
                                    "lastName" to editTextLastName.text.toString().trim(),
                                    "profilePictureUri" to downloadUri.toString()
                                )

                                firestore.collection("userProfiles").document(userId)
                                    .set(userProfile)
                                    .addOnSuccessListener {
                                        // Profile picture URL saved to Firestore
                                        isProfilePictureUploaded = true
                                        checkIfAllDataUploaded()
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
        } else {
            // Handle the case when the URI is null
        }
    }



    private fun saveUserProfile() {
        val firstName = editTextFirstName.text.toString().trim()
        val lastName = editTextLastName.text.toString().trim()
        val profilePictureUri = selectedImageUri?.toString()

        // Save user data to Firestore
        val user = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "profilePictureUri" to profilePictureUri
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

                    isProfilePictureUploaded = true
                    checkIfAllDataUploaded()
                }
                .addOnFailureListener {
                    // Error occurred while saving data
                    // Handle the error
                }
        }
    }


    private fun checkIfAllDataUploaded() {
        if (isProfilePictureUploaded && editTextFirstName.text.isNotBlank() && editTextLastName.text.isNotBlank() && editTextBio.text.isNotBlank()) {
            val firstName = editTextFirstName.text.toString().trim()
            val lastName = editTextLastName.text.toString().trim()
            val bio = editTextBio.text.toString().trim()
            startHomeActivity(firstName, lastName, selectedImageUri)
        }
    }



    private fun startHomeActivity(firstName: String, lastName: String, userProfilePicture: Uri?) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("firstName", firstName)
        intent.putExtra("lastName", lastName)
        intent.putExtra("userProfilePicture", userProfilePicture?.toString())
        startActivity(intent)
        finish()
    }



}
