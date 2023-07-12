package com.example.chama

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ContributeActivity : AppCompatActivity() {

    private lateinit var phoneNumberEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var paymentsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contribute)

        phoneNumberEditText = findViewById(R.id.editTextPhoneNumber)
        amountEditText = findViewById(R.id.editTextContributionAmount)

        val payButton: Button = findViewById(R.id.buttonPay)
        payButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString().trim()
            val amount = amountEditText.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please input phone number", Toast.LENGTH_SHORT).show()
            } else if (phoneNumber.length != 13) {
                Toast.makeText(this, "Invalid phone number.", Toast.LENGTH_SHORT).show()
            } else if (amount.isEmpty()) {
                Toast.makeText(this, "Please input amount", Toast.LENGTH_SHORT).show()
            } else {
                // Launch M-Pesa application for payment processing
                val mpesaUri = Uri.parse("mpesa://pay?phone=$phoneNumber&amount=$amount")
                val intent = Intent(Intent.ACTION_VIEW, mpesaUri)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                    savePaymentDetails(phoneNumber, amount.toDouble())
                } else {
                    // M-Pesa application not found, display an error message or alternative payment option
                    Toast.makeText(this, "M-Pesa application not installed.", Toast.LENGTH_SHORT).show()
                }
            }
        }


        // Get a reference to the "payments" node in the Firebase Realtime Database
        paymentsRef = FirebaseDatabase.getInstance().getReference("payments")

        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            finish() // Finish the current activity and go back to the previous activity (TransactionActivity)
        }
    }

    private fun savePaymentDetails(phoneNumber: String, amount: Double) {
        // Create a new payment object
        val payment = Payment(phoneNumber, amount, System.currentTimeMillis())

        // Generate a unique key for the payment
        val paymentKey = paymentsRef.push().key

        // Save the payment details under the generated key
        paymentKey?.let {
            paymentsRef.child(it).setValue(payment)
                .addOnSuccessListener {
                    // Payment details saved successfully
                    Toast.makeText(this, "Payment details saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    // Error occurred while saving payment details
                    Toast.makeText(this, "Failed to save payment details", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

data class Payment(
    val phoneNumber: String,
    val amount: Double,
    val timestamp: Long
)
