package com.example.chama

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class GoalsFragment : Fragment() {

    private lateinit var chamaCardView: CardView
    private lateinit var chama: Chama
    private var isEditMode = false
    private var chamaId: String? = null // Add chamaId variable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        chamaCardView = view.findViewById(R.id.goalsCardView)
        chamaCardView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.normalModeBackground))


        chamaId = arguments?.getString("chamaId") // Assign the chamaId value

        val chamaName = arguments?.getString("chamaName")
        val description = arguments?.getString("description")
        val goals = arguments?.getString("goals")
        val targetPerMonth = arguments?.getString("targetPerMonth")

        displayChamaDetails(chamaName, description, goals, targetPerMonth)

        val editButton = view.findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            toggleEditMode()
        }

        return view
    }

    private fun displayChamaDetails(
        chamaName: String?,
        description: String?,
        goals: String?,
        targetPerMonth: String?
    ) {
        chamaCardView.apply {
            findViewById<EditText>(R.id.chamaNameET)?.apply {
                isEnabled = isEditMode
                setText(getString(R.string.chama_name_edit_text, chamaName))
                backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            }
            findViewById<EditText>(R.id.descriptionET)?.apply {
                isEnabled = isEditMode
                setText(getString(R.string.description_edit_text, description))
                backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            }
            findViewById<EditText>(R.id.goalsET)?.apply {
                isEnabled = isEditMode
                setText(getString(R.string.goals_edit_text, goals))
                backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            }
            findViewById<EditText>(R.id.targetPerMonthET)?.apply {
                isEnabled = isEditMode
                setText(getString(R.string.target_per_month_edit_text, targetPerMonth))
                backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            }
        }


        // Initialize the chama object with the retrieved details
        chama = Chama(
            chamaId = chamaId ?: "",
            chamaName = chamaName ?: "",
            description = description ?: "",
            goals = goals ?: "",
            targetPerMonth = targetPerMonth ?: ""
        )
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            enableEditFields()
            chamaCardView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editModeBackground))
        } else {
            disableEditFields()
            chamaCardView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.normalModeBackground))
            updateChamaDetails()
        }
    }


    private fun enableEditFields() {
        chamaCardView.apply {
            findViewById<EditText>(R.id.chamaNameET)?.isEnabled = true
            findViewById<EditText>(R.id.descriptionET)?.isEnabled = true
            findViewById<EditText>(R.id.goalsET)?.isEnabled = true
            findViewById<EditText>(R.id.targetPerMonthET)?.isEnabled = true

            // Change the background color when in edit mode
            setBackgroundColor(Color.parseColor("#F2F2F2"))
        }
    }

    private fun disableEditFields() {
        chamaCardView.apply {
            findViewById<EditText>(R.id.chamaNameET)?.isEnabled = false
            findViewById<EditText>(R.id.descriptionET)?.isEnabled = false
            findViewById<EditText>(R.id.goalsET)?.isEnabled = false
            findViewById<EditText>(R.id.targetPerMonthET)?.isEnabled = false

            // Restore the original background color when not in edit mode
            setBackgroundColor(Color.TRANSPARENT)
        }
    }



    private fun updateChamaDetails() {
        if (chamaId != null) {
            val chamaNameET = chamaCardView.findViewById<EditText>(R.id.chamaNameET)
            val descriptionET = chamaCardView.findViewById<EditText>(R.id.descriptionET)
            val goalsET = chamaCardView.findViewById<EditText>(R.id.goalsET)
            val targetPerMonthET = chamaCardView.findViewById<EditText>(R.id.targetPerMonthET)

            chama.chamaName = chamaNameET.text.toString().trim()
            chama.description = descriptionET.text.toString().trim()
            chama.goals = goalsET.text.toString().trim()
            chama.targetPerMonth = targetPerMonthET.text.toString().trim()

            val firestore = FirebaseFirestore.getInstance()
            val chamaCollection = firestore.collection("chamas")
            val chamaDocument = chamaCollection.document(chamaId!!) // Use chamaId as document path

            val updatedData = mapOf(
                "chamaName" to chama.chamaName,
                "description" to chama.description,
                "goals" to chama.goals,
                "targetPerMonth" to chama.targetPerMonth
            )

            chamaDocument.update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Chama details updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    saveChamaDetails() // Call saveChamaDetails() function here
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to update Chama details: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(requireContext(), "Invalid Chama ID", Toast.LENGTH_SHORT).show()
        }
    }



    private fun saveChamaDetails() {
        // Implement your code to save the updated Chama details
        // For example, you can use Firebase Firestore
        val firestore = FirebaseFirestore.getInstance()
        val chamaCollection = firestore.collection("chamas")
        val chamaDocument = chamaCollection.document(chama.chamaId)

        val updatedChama = hashMapOf(
            "chamaName" to chama.chamaName,
            "description" to chama.description,
            "goals" to chama.goals,
            "targetPerMonth" to chama.targetPerMonth
        ) as Map<String, Any> // Explicit cast to the expected type

        chamaDocument.update(updatedChama)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Chama details updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to update Chama details: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
