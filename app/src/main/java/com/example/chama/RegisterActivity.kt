package com.example.chama


import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var registerBtn: Button
    private lateinit var guideLinesTv: TextView
    private lateinit var showPasswordCheckbox: CheckBox


    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        registerBtn = findViewById(R.id.register)
        guideLinesTv = findViewById(R.id.guidelinesTextView)
        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox)




        guideLinesTv.setTextColor(ContextCompat.getColor(this, R.color.red))

        showPasswordCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        registerBtn.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Empty credentials", Toast.LENGTH_SHORT).show()
            } //else if (password.length <= 8){
            // Toast.makeText(this,"password must be at least 8 characters long.",Toast.LENGTH_SHORT).show()
            // }
            else if (!isPasswordValid(password)) {
                Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
            }
            else
            {
                registerUser(email,password)
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


    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Account registration successful, activate account
                    activateAccount()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
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

    private fun activateAccount() {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Registration successful. Verification email sent.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
