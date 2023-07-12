package com.example.chama

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.chama.databinding.ActivityCreateNewChamaBinding
import com.google.firebase.firestore.FirebaseFirestore

class CreateNewChamaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNewChamaBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewChamaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inviteBtn.setOnClickListener {
            val chamaName = binding.nameEditText.text.toString().trim()
            val description = binding.descAutoCompleteTextView.text.toString().trim()
            val goals = binding.goalsEditText.text.toString().trim()
            val targetPerMonth = binding.targetEditText.text.toString().trim()
            val noOfMembers = binding.noOfMembersEditText.text.toString().trim()

            if (noOfMembers.isEmpty()) {
                binding.noOfMembersEditText.error = "This field is required"
                return@setOnClickListener
            }
            if (targetPerMonth.isEmpty()) {
                binding.targetEditText.error = "This field is required"
                return@setOnClickListener
            }
            if (chamaName.isEmpty()) {
                binding.nameEditText.error = "This field is required"
                return@setOnClickListener
            }

            val numberOfMembers = binding.noOfMembersEditText.text.toString().toInt()

            // Creating a data map with the values from the input fields
            val data = hashMapOf(
                "chamaName" to chamaName,
                "description" to description,
                "goals" to goals,
                "targetPerMonth" to targetPerMonth,
                "noOfMembers" to noOfMembers
            )

            // Adding the data to Cloud Firestore

            db.collection("chamas")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    val chamaDocumentId = documentReference.id
                    Toast.makeText(this, "New chama created successfully", Toast.LENGTH_SHORT).show()

                    // Pass the Chama document ID to the GoalsFragment
                    val goalsFragment = GoalsFragment()
                    val bundle = Bundle()
                    bundle.putString("chamaId", chamaDocumentId)
                    goalsFragment.arguments = bundle

                    // Pass the chamaName or any other data as needed
                    val intent = Intent(this, ChooseMemberActivity::class.java)
                    intent.putExtra("chamaName", chamaName)
                    intent.putExtra("numberOfMembers", numberOfMembers)
                    startActivity(intent)
                    finish()
                }

                .addOnFailureListener { e ->
                    // Handle the error appropriately
                    Toast.makeText(this,"Failed to create new chama",Toast.LENGTH_SHORT).show()

                }
        }

        val items = listOf("Family", "School", "Friends", "Others")

        val descTextInputLayout = binding.descTextInputLayout
        val descAutoCompleteTextView = binding.descAutoCompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        descAutoCompleteTextView.setAdapter(adapter)

        descTextInputLayout.setEndIconOnClickListener {
            // Open the dropdown programmatically
            descAutoCompleteTextView.showDropDown()
        }
    }
}
