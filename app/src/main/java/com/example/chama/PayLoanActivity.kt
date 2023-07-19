package com.example.chama

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.json.JSONException


class PayLoanActivity : AppCompatActivity() {

    private lateinit var phoneNumberEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var firestore: FirebaseFirestore

    private val consumerKey = "m1YMZotp2Th9KvoZZA27qj8M1Fgj6JSI"
    private val consumerSecret = "kA2lElqkff9vEGmf"
    private var accessToken: String? = null

    private val TAG = "ContributeActivity"
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_loan)

        phoneNumberEditText = findViewById(R.id.loanIdEditText)
        amountEditText = findViewById(R.id.amountEditText)

        val payButton: Button = findViewById(R.id.submitLoanBtn)
        payButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString().trim()
            val amount = amountEditText.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please input phone number", Toast.LENGTH_SHORT).show()
            } else if (amount.isEmpty()) {
                Toast.makeText(this, "Please input amount", Toast.LENGTH_SHORT).show()
            } else {
                generateAccessToken { success ->
                    if (success) {
                        val timestamp = getCurrentTimestamp()
                        val password = generatePassword(consumerSecret, timestamp)

                        val requestBody = """
                            {
                                "BusinessShortCode": 174379,
                                "Password": "MTc0Mzc5YmZiMjc5ZjlhYTliZGJjZjE1OGU5N2RkNzFhNDY3Y2QyZTBjODkzMDU5YjEwZjc4ZTZiNzJhZGExZWQyYzkxOTIwMjMwNzE3MTIwMDA2",
                                "Timestamp": "20230717120006",
                                "TransactionType": "CustomerPayBillOnline",
                                "Amount": $amount,
                                "PartyA": 254708374149,
                                "PartyB": 174379,
                                "PhoneNumber": $phoneNumber,
                                "CallBackURL": "https://mydomain.com/path",
                                "AccountReference": "CompanyXLTD",
                                "TransactionDesc": "Payment of X" 
                             }
                        """.trimIndent()

                        try {
                            initiateMpesaPayment(requestBody, phoneNumber, amount)
                        } catch (e: IOException) {
                            showToast("Failed to initiate M-Pesa payment")
                            Log.e(TAG, "Network failure: ${e.message}")
                        } catch (e: JSONException) {
                            showToast("Failed to initiate M-Pesa payment")
                            Log.e(TAG, "JSON parsing error: ${e.message}")
                        }
                    } else {
                        showToast("Failed to generate access token")
                    }
                }
            }
        }


        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateAccessToken(completion: (Boolean) -> Unit) {
        val credentials = "$consumerKey:$consumerSecret"
        val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val request = Request.Builder()
            .url("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials")
            .header("Authorization", "Basic $base64Credentials")
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle network failure
                Log.e(TAG, "Network failure: ${e.message}")
                completion(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val jsonObject = JSONObject(responseBody)
                    accessToken = jsonObject.getString("access_token")
                    Log.d(TAG, "Access Token: $accessToken") // Log the access token
                    completion(true)
                } else {
                    // Handle API response error
                    val errorBody = responseBody ?: "Unknown error"
                    Log.e(TAG, "API response error: $errorBody")
                    completion(false)
                }
            }
        })
    }

// ...

    private fun initiateMpesaPayment(requestBody: String, phoneNumber: String, amount: String) {
        generateAccessToken { success ->
            if (success) {
                val mediaType = "application/json".toMediaTypeOrNull()
                val request = Request.Builder()
                    .url("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest")
                    .header("Authorization", "Bearer $accessToken")
                    .header("Content-Type", "application/json")
                    .post(requestBody.toRequestBody(mediaType))
                    .build()

                val client = OkHttpClient()

                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()

                    if (response.isSuccessful && responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        val responseCode = jsonObject.getString("ResponseCode")

                        if (responseCode == "0") {
                            showToast("M-Pesa payment initiated successfully")
                            savePaymentDetails(phoneNumber, amount.toDouble())
                        } else {
                            // Handle API response error
                            val errorBody = responseBody ?: "Unknown error"
                            Log.e(TAG, "API response error: $errorBody")
                            showToast("Failed to initiate M-Pesa payment")
                        }
                    } else {
                        // Handle API response error
                        val errorBody = responseBody ?: "Unknown error"
                        Log.e(TAG, "API response error: $errorBody")
                        showToast("Failed to initiate M-Pesa payment")
                    }
                }
            } else {
                showToast("Failed to generate access token")
            }
        }
    }

// ...


    private fun getCurrentTimestamp(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun generatePassword(consumerSecret: String, timestamp: String): String {
        val passwordData = "$consumerSecret$timestamp"
        val secretKeySpec = SecretKeySpec(consumerSecret.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKeySpec)
        val hashBytes = mac.doFinal(passwordData.toByteArray())
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }

    private fun savePaymentDetails(phoneNumber: String, amount: Double) {
        val payment = hashMapOf(
            "phoneNumber" to phoneNumber,
            "amount" to amount.toString(),
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("payments") // Specify the collection name here
            .add(payment)
            .addOnSuccessListener { documentReference ->
                showToast("Payment details saved: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                showToast("Failed to save payment details: ${e.message}")
                Log.e(TAG, "Failed to save payment details: ${e.message}")
            }
    }



}

