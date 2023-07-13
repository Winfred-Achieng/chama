package com.example.chama

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        val buttonContribute: Button = findViewById(R.id.buttonContribute)
        val buttonHistory: Button = findViewById(R.id.buttonHistory)

        buttonContribute.setOnClickListener {
            val intent = Intent(this, ContributeActivity::class.java)
            startActivity(intent)
        }

        buttonHistory.setOnClickListener {
            val intent = Intent(this, HistoryTransactionActivity::class.java)
            startActivity(intent)
        }
//add
        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            finish() // Close the current activity and go back to the previous activity (ContributeActivity)
        }
    }
}
