package com.example.chama

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class HistoryTransactionActivity : AppCompatActivity() {

    private lateinit var paymentsRef: DatabaseReference
    private lateinit var paymentDetailsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_transaction)

        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            onBackPressed()
        }
        paymentDetailsTextView = findViewById(R.id.textViewPaymentDetails)

        // Get a reference to the "payments" node in the Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        paymentsRef = database.getReference("payments")

        // Read the payment details from the database
       // paymentsRef.addValueEventListener(object : ValueEventListener {
         //   override fun onDataChange(dataSnapshot: DataSnapshot) {
            //    val paymentDetails = StringBuilder()

                // Iterate over each child node (payment)
                //for (childSnapshot in dataSnapshot.children) {
                    // Parse the payment details
                   // val payment = childSnapshot.getValue(Payment::class.java)

                    // Append the amount and timestamp to the paymentDetails string
                    //payment?.let {
                       // val amount = it.amount
                       // val timestamp = it.timestamp

                        //val paymentInfo = "Amount: $amount, Timestamp: $timestamp\n"
                        //paymentDetails.append(paymentInfo)
                    }
                //}

                // Set the payment details text in the TextView
                //paymentDetailsTextView.text = paymentDetails.toString()
           // }

            //override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read payment details
                // Handle the error
            //}
       // })
    //}

    // ...
}
