package com.example.chama

import android.app.KeyguardManager
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import com.google.android.gms.common.SignInButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


lateinit var binding: ActivityLoginBinding
private lateinit var auth: FirebaseAuth
val RC_SIGN_IN = 123
private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
private var showOneTapUI = true
private lateinit var oneTapClient: SignInClient
private lateinit var signInRequest: BeginSignInRequest


class LoginActivity : AppCompatActivity() {

    private var cancellationSignal:CancellationSignal?=null

    private val authenticationCallback:BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object:BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    notifyUser("Authentication success!")
                    startActivity(Intent(this@LoginActivity,UserProfile::class.java))
                    finish()
                }
            }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkBiometricSupport()




        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val email = binding.email
        val password = binding.password
        val loginBtn = binding.login
        val forgotPassword = binding.forgotPasswordClickableText
        val guideLinesTv = binding.guidelinesTextView
        val showPasswordCheckbox = binding.showPasswordCheckbox
        val googleSignin =binding.googleSignin
        val authentication= binding.fingerprintImageView



// Create an empty list to hold the email suggestions
        val emailSuggestions = mutableListOf<String>()

// Fetch the list of registered emails from Firebase
        val firebaseRef = FirebaseDatabase.getInstance().getReference("users")
        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterate through the dataSnapshot and add each email to the emailSuggestions list
                for (snapshot in dataSnapshot.children) {
                    val email = snapshot.child("email").value?.toString()
                    if (!email.isNullOrEmpty()) {
                        emailSuggestions.add(email)
                    }
                }

                // Create an ArrayAdapter with the email suggestions
                val adapter = ArrayAdapter(this@LoginActivity, android.R.layout.simple_dropdown_item_1line, emailSuggestions.toTypedArray())

                // Set the adapter to the AutoCompleteTextView
                email.setAdapter(adapter)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error if fetching data from Firebase fails
               Toast.makeText(this@LoginActivity,"fetching data from Firebase failed",Toast.LENGTH_SHORT).show()
            }
        })




        googleSignin.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
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

            if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                Toast.makeText(this, "Empty credentials", Toast.LENGTH_SHORT).show()
            } //else if (password.length <= 8){
            // Toast.makeText(this,"password must be at least 8 characters long.",Toast.LENGTH_SHORT).show()
            // }
            else if (!isPasswordValid(txtPassword)) {
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
            }
            else
            {
                loginUser(txtEmail,txtPassword)
            }
        }

        authentication.setOnClickListener{

            val biometricPrompt= BiometricPrompt.Builder(this)
                .setTitle("Login")
                .setSubtitle("Login to your Chama account ")
                .setDescription("This app uses fingerprint protection to keep your data secure")
                .setNegativeButton("Cancel",this.mainExecutor,DialogInterface.OnClickListener { dialog, which ->
                    notifyUser("Authentication cancelled")
                }).build()
            biometricPrompt.authenticate(getCancellationSignal(),mainExecutor,authenticationCallback)


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

    private fun notifyUser(message:String){

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    private fun getCancellationSignal(): CancellationSignal{
        cancellationSignal= CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }


    private fun checkBiometricSupport(): Boolean {

        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        if(!keyguardManager.isKeyguardSecure){
            notifyUser("Fingerprint authentication has not been enabled in the settings")
            return false
        }
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.USE_BIOMETRIC)!=PackageManager.PERMISSION_GRANTED){
            notifyUser("Fingerprint authentication permission is not enabled")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        }else true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // Use the account information to sign in the user with Firebase Authentication
                firebaseAuthWithGoogle(account?.idToken)
            } catch (e: ApiException) {
                // Handle sign-in failure
                Log.e(TAG, "Google sign-in failed", e)
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in with Gmail successful
                    val user = FirebaseAuth.getInstance().currentUser
                    // Continue with app logic
                    Toast.makeText(this, "Sign-in with Gmail successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Handle sign-in failure
                    Log.e(TAG, "Gmail sign-in failed", task.exception)
                    Toast.makeText(this, "Gmail sign-in failed", Toast.LENGTH_SHORT).show()
                }
            }
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
