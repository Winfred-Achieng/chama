package com.example.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class HistoryFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var paymentDetailsTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_history, container, false)

        val backButton: Button = rootView.findViewById(R.id.buttonBack)
        paymentDetailsTextView = rootView.findViewById(R.id.textViewPaymentDetails)

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

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

        return rootView
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
