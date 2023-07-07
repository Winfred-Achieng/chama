package com.example.chama


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StartActivity : AppCompatActivity() {

    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button
    private lateinit var btnChama: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        btnRegister = findViewById(R.id.btn_register)
        btnLogin = findViewById(R.id.btn_login)
        btnChama = findViewById(R.id.btn_chama)

        //val deepLinkUri: Uri? = intent.data
       // if (deepLinkUri != null) {
            // Extract data from the deep link URL and perform registration tasks
         //   val userId = deepLinkUri.getQueryParameter("userId")
           // val token = deepLinkUri.getQueryParameter("token")
        //}

        btnRegister.setOnClickListener {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)

            }

        btnLogin.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

            }

        btnChama.setOnClickListener {
                val intent = Intent(this, CreateNewChamaActivity::class.java)
                startActivity(intent)
            }



    }
}