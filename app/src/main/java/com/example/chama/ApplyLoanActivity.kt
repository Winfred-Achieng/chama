package com.example.chama

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chama.databinding.ActivityApplyLoanBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ApplyLoanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApplyLoanBinding
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var isApplicationSubmitted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityApplyLoanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitLoanBtn.setOnClickListener {
            if (!isApplicationSubmitted) {
                submitLoanApplication()
                isApplicationSubmitted = true
                binding.submitLoanBtn.isEnabled = false
            }
        }
    }

    private fun submitLoanApplication() {
        // Retrieve input values from EditText fields
        val loanAmount = binding.loanAmountEditText.text.toString()
        val loanPurpose = binding.loanPurposeEditText.text.toString()

        // Perform validation and processing of the loan application data
        // ...

        // Check if a loan application with the same user details already exists
        val existingLoanQuery = firestore.collection("loans")
            .whereEqualTo("loanAmount", loanAmount)
            .whereEqualTo("loanPurpose", loanPurpose)
            .limit(1)

        existingLoanQuery.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // No existing loan application found, proceed with submitting the loan application
                    val loanData = hashMapOf(
                        "loanAmount" to loanAmount,
                        "loanPurpose" to loanPurpose,
                        "requestDate" to FieldValue.serverTimestamp()
                    )

                    firestore.collection("loans")
                        .add(loanData)
                        .addOnSuccessListener { documentReference ->
                            // Loan application successfully submitted
                            val loanId = documentReference.id

                            // Store the loan ID in shared preferences
                            val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            sharedPrefs.edit().putString("loanId", loanId).apply()

                            Toast.makeText(this, "Loan application submitted", Toast.LENGTH_SHORT).show()

                            navigateToLoanDetails(loanId)
                        }
                        .addOnFailureListener { e ->
                            // Failed to submit loan application
                            Toast.makeText(this, "Failed to submit loan application: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // An existing loan application with the same user details already exists
                    Toast.makeText(this, "You have already submitted a loan application with the same details", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Failed to check for existing loan application
                Toast.makeText(this, "Failed to check for existing loan application: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToLoanDetails(loanId: String) {
        val intent = Intent(this, ChamaCreatedActivity::class.java)
        intent.putExtra("loanId", loanId)
        startActivity(intent)
        finish()
    }
}
