package com.example.chama
import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import android.content.Intent
import com.google.firebase.firestore.FirebaseFirestore

class OTPActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var etPhoneNumber: EditText
    private lateinit var etOtp: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var btnVerifyOtp: Button
    private var verificationId: String? = null
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etOtp = findViewById(R.id.etOtp)
        btnSendOtp = findViewById(R.id.btnSendOtp)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)

        etPhoneNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15)) // Set max length to 15 characters

        btnSendOtp.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString().trim()

            if (phoneNumber.isNotEmpty()) {
                val phoneNumberPattern = Regex("^\\+[1-9]\\d{10,14}\$")
                if (phoneNumber.matches(phoneNumberPattern)) {
                    sendOtpToPhoneNumber(phoneNumber)
                } else {
                    showToast("Invalid phone number format")
                }
            } else {
                showToast("Please enter a phone number")
            }
        }

        btnVerifyOtp.setOnClickListener {
            val otp = etOtp.text.toString().trim()

            if (otp.isNotEmpty()) {
                verifyOtp(otp)
            } else {
                showToast("Please enter the OTP")
            }
        }
    }

    private fun sendOtpToPhoneNumber(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    showToast("Verification failed: ${e.message}")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    this@OTPActivity.verificationId = verificationId
                    showToast("OTP sent successfully")

                    // Start OTPVerificationActivity and pass the verification ID
                    val intent = Intent(this@OTPActivity, OTPVerificationActivity::class.java)
                    intent.putExtra("verificationId", verificationId)
                    startActivity(intent)
                }


            })
    }

    private fun verifyOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    showToast("Verification successful")

                    val phoneNumber = etPhoneNumber.text.toString().trim()
                    val otp = etOtp.text.toString().trim()

                    // Save the OTP and phone number to Firestore
                    val userDocument = FirebaseFirestore.getInstance().collection("users").document(auth.currentUser?.uid ?: "")
                    val data = hashMapOf(
                        "phoneNumber" to phoneNumber,
                        "otp" to otp
                    )

                    userDocument.set(data)
                        .addOnSuccessListener {
                            showToast("OTP and phone number saved to Firestore")
                            // Proceed with the desired actions, such as navigating to the next screen
                        }
                        .addOnFailureListener { exception ->
                            showToast("Error saving OTP and phone number to Firestore: ${exception.message}")
                        }
                } else {
                    showToast("Verification failed: ${task.exception?.message}")
                }
            }
    }


    private fun showToast(message: String) {
        Toast.makeText(this@OTPActivity, message, Toast.LENGTH_SHORT).show()
    }
}
