package com.example.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class GoalsFragment : Fragment() {

    private lateinit var chamaCardView: CardView
    private lateinit var chama: Chama
    private var isEditMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        chamaCardView = view.findViewById(R.id.goalsCardView)

        // Retrieve the Chama document ID passed from CreateNewChamaActivity
        val chamaDocumentId = arguments?.getString("chamaId")

        // Retrieve and display the Chama details
        if (chamaDocumentId != null) {
            fetchChamaDetails(chamaDocumentId)
        }

        val editButton = view.findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            toggleEditMode()
        }

        return view
    }

    private fun fetchChamaDetails(chamaDocumentId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val chamaCollection = firestore.collection("chamas")
        val chamaDocument = chamaCollection.document(chamaDocumentId)

        chamaDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    chama = Chama(
                        chamaId = chamaDocumentId,
                        chamaName = documentSnapshot.getString("chamaName") ?: "",
                        description = documentSnapshot.getString("description") ?: "",
                        goals = documentSnapshot.getString("goals") ?: "",
                        targetPerMonth = documentSnapshot.getString("targetPerMonth") ?: ""
                    )

                    // Display the Chama details in a CardView
                    displayChamaDetails()
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure to retrieve Chama details
                Toast.makeText(requireContext(), "Failed to retrieve Chama details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayChamaDetails() {
        chamaCardView.apply {
            findViewById<TextView>(R.id.chamaNameTV).text = chama.chamaName
            findViewById<TextView>(R.id.descriptionTV).text = chama.description
            findViewById<TextView>(R.id.goalsTV).text = chama.goals
            findViewById<TextView>(R.id.targetPerMonthTV).text = chama.targetPerMonth
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            enableEditFields()
        } else {
            disableEditFields()
              updateChamaDetails()
        }
    }

    private fun enableEditFields() {
        chamaCardView.apply {
            findViewById<TextView>(R.id.chamaNameTV).isEnabled = true
            findViewById<TextView>(R.id.descriptionTV).isEnabled = true
            findViewById<TextView>(R.id.goalsTV).isEnabled = true
            findViewById<TextView>(R.id.targetPerMonthTV).isEnabled = true
        }
    }

    private fun disableEditFields() {
        chamaCardView.apply {
            findViewById<TextView>(R.id.chamaNameTV).isEnabled = false
            findViewById<TextView>(R.id.descriptionTV).isEnabled = false
            findViewById<TextView>(R.id.goalsTV).isEnabled = false
            findViewById<TextView>(R.id.targetPerMonthTV).isEnabled = false
        }
    }

    private fun updateChamaDetails() {
        chamaCardView.apply {
            chama.chamaName = findViewById<TextView>(R.id.chamaNameTV).text.toString()
            chama.description = findViewById<TextView>(R.id.descriptionTV).text.toString()
            chama.goals = findViewById<TextView>(R.id.goalsTV).text.toString()
            chama.targetPerMonth = findViewById<TextView>(R.id.targetPerMonthTV).text.toString()
        }

        saveChamaDetailsToFirestore()
    }

    private fun saveChamaDetailsToFirestore() {
        // Implement your code to save the updated Chama details to Firestore
        // Example:
        val firestore = FirebaseFirestore.getInstance()
        val chamaCollection = firestore.collection("chamas")
        val chamaDocument = chamaCollection.document(chama.chamaId)

        chamaDocument.update(
            "chamaName", chama.chamaName,
            "description", chama.description,
            "goals", chama.goals,
            "targetPerMonth", chama.targetPerMonth
        )
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Chama details updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to update Chama details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
