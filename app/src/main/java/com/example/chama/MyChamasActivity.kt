package com.example.chama

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MyChamasActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_chamas)

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Fetch and display user's chamas
        fetchChamas()
    }

    private fun fetchChamas() {
        // Assuming you have the user's ID available
        val userId = "user123"

        firestore.collection("chamas")
            .whereArrayContains("members", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Chamas fetched successfully
                for (document in querySnapshot) {
                    val chamaName = document.getString("name")
                    val chamaLocation = document.getString("location")
                    // Process chama data as needed
                }
            }
            .addOnFailureListener { exception ->
                // Error occurred while fetching chamas
            }
    }
}
