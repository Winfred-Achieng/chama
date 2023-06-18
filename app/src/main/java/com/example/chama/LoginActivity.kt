package com.example.chama

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chama.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


private lateinit var binding: ActivityLoginBinding
private lateinit var auth: FirebaseAuth
private val RC_SIGN_IN = 123
private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
private var showOneTapUI = true
private lateinit var oneTapClient: SignInClient
private lateinit var signInRequest: BeginSignInRequest


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val email = binding.email
        val password = binding.password
        val loginBtn = binding.login
        val forgotPassword = binding.forgotPassword
        val guideLinesTv = binding.guidelinesTextView
        val showPasswordCheckbox = binding.showPasswordCheckbox
        val googleSignin =binding.googleSignin




        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                // Your server's client ID, not your Android client ID.
                .setServerClientId(getString(R.string.web_client_id))
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(true)
                .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build();




//the button for sign in with gmail option
        googleSignin.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(TAG, e.localizedMessage)
                }
        }



        guideLinesTv.setTextColor(ContextCompat.getColor(this, R.color.red))

        showPasswordCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        forgotPassword.setOnClickListener {
            val email = email.text.toString()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter your email to reset password", Toast.LENGTH_SHORT).show()
            } else {
                resetPassword(email)
            }
        }

        loginBtn.setOnClickListener {
            val txtEmail = email.text.toString()
            val txtPassword = password.text.toString()

            if (isPasswordValid(txtPassword)) {
                loginUser(txtEmail, txtPassword)
            } else {
                val guidelines = getPasswordGuidelines(txtPassword)
                Toast.makeText(this, guidelines, Toast.LENGTH_SHORT).show()
            }
        }

        // Set a TextWatcher on the password EditText
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the guidelines text based on the entered password
                val password = s.toString()
                val guidelines = getPasswordGuidelines(password)
                guideLinesTv.text = guidelines
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val email = credential.id
                    val password = credential.password
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithCredential:success")
                                        val user = auth.currentUser
                                        Toast.makeText(this,"signInWithCredential:success",Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this,MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                                        Toast.makeText(this,"signInWithCredential:failure",Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this,StartActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token or password!")
                        }
                    }
                }  catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(TAG, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(TAG, "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }
                        else -> {
                            Log.d(TAG, "Couldn't get credential from result." +
                                    " (${e.localizedMessage})")
                        }
                    }
                }
            }
        }
    }



    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        Toast.makeText(this,"user is already signed in",Toast.LENGTH_SHORT).show()
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }



    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Password reset email sent. Check your inbox.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to send password reset email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getPasswordGuidelines(password: String): String {
        val minLength = 8
        val hasDigit = password.any { it.isDigit() }

        val guidelines = StringBuilder()

        if (password.length < minLength) {
            guidelines.append("Password must be at least $minLength characters long.\n")
        }
        if (!hasDigit) {
            guidelines.append("Password should include at least one digit.\n")
        }

        return guidelines.toString().trim()
    }

    private fun isPasswordValid(password: String): Boolean {
        val minLength = 8
        val hasDigit = password.any { it.isDigit() }

        val isValidLength = password.length >= minLength
        val hasValidDigit = hasDigit

        return isValidLength && hasValidDigit
    }
}
