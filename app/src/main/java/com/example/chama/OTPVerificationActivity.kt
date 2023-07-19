package com.example.chama

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class OTPVerificationActivity : AppCompatActivity() {
    private lateinit var verificationId: String
    private lateinit var etOtp: EditText
    private lateinit var btnVerifyOtp: Button
    private lateinit var firestore: FirebaseFirestore
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        // Retrieve the verification ID from the intent
        verificationId = intent.getStringExtra("verificationId") ?: ""

        etOtp = findViewById(R.id.etOtp)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)

        firestore = FirebaseFirestore.getInstance()

        btnVerifyOtp.setOnClickListener {
            val enteredOtp = etOtp.text.toString().trim()

            if (enteredOtp.isNotEmpty()) {
                verifyOtp(enteredOtp)
            } else {
                showToast("Please enter the OTP")
            }
        }
    }

    private fun verifyOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    showToast("Verification successful")

                    // Save phone number and OTP to Firestore
                    val phoneNumber = user?.phoneNumber
                    saveUserDataToFirestore(phoneNumber, etOtp.text.toString().trim())

                    // Proceed to HomeActivity
                    val intent = Intent(this@OTPVerificationActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // Optionally, finish the OTPVerificationActivity
                } else {
                    showToast("Verification failed: ${task.exception?.message}")
                }
            }
    }

    private fun saveUserDataToFirestore(phoneNumber: String?, otp: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val user = hashMapOf(
                "phoneNumber" to phoneNumber,
                "otp" to otp
            )

            firestore.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener {
                    // Data saved successfully
                }
                .addOnFailureListener { exception ->
                    showToast("Failed to save user data: ${exception.message}")
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@OTPVerificationActivity, message, Toast.LENGTH_SHORT).show()
    }
}
