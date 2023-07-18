package com.example.chama

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class HistoryTransactionActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var paymentDetailsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_transaction)

        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            onBackPressed()
        }
        paymentDetailsTextView = findViewById(R.id.textViewPaymentDetails)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Read the payment details from Firestore
        firestore.collection("payments")
            .get()
            .addOnSuccessListener { documents ->
                val paymentDetails = StringBuilder()

                for (document in documents) {
                    val phoneNumber = document.getString("phoneNumber")
                    val amount = document.getString("amount")
                    val timestamp = document.getLong("timestamp")

                    val paymentInfo = """
                        | Phone Number: $phoneNumber
                        | Amount: $amount
                        | Timestamp: $timestamp
                        |-------------------------
                        |
                    """.trimMargin()

                    paymentDetails.append(paymentInfo)
                }

                // Set the payment details text in the TextView
                paymentDetailsTextView.text = paymentDetails.toString()
            }
            .addOnFailureListener { exception ->
                // Failed to read payment details
                showToast("Failed to read payment details: ${exception.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
