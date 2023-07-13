package com.example.chama


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StartActivity : AppCompatActivity() {

    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //val intent = Intent(this, UserProfile::class.java)
        //startActivity(intent)
        //finish()

        btnRegister = findViewById(R.id.btn_register)
        btnLogin = findViewById(R.id.btn_login)

        btnRegister.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)

        }

        btnLogin.setOnClickListener {
            val intent = Intent(this,CreateNewChamaActivity::class.java)
            startActivity(intent)

        }
    }
}