package com.example.chama

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

data class ChatMessage(
    val senderName: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    // Add a no-argument constructor
    constructor() : this("", "", 0)
}

class ChatActivity : AppCompatActivity() {

    private lateinit var editTextChatMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages: ArrayList<ChatMessage> = ArrayList()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var senderName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        editTextChatMessage = findViewById(R.id.editTextChatMessage)
        buttonSend = findViewById(R.id.buttonSend)
        recyclerViewChat = findViewById(R.id.recyclerViewChat)

        chatAdapter = ChatAdapter(chatMessages)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerViewChat.layoutManager = layoutManager
        recyclerViewChat.adapter = chatAdapter

        val firstName = intent.getStringExtra("firstName")
        val lastName = intent.getStringExtra("lastName")
        senderName = "$firstName $lastName"

        buttonSend.setOnClickListener {
            val message = editTextChatMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                val chatMessage = ChatMessage(senderName, message)
                saveChatMessage(chatMessage)
            }
        }

        listenForChatMessages()
    }


    private fun saveChatMessage(chatMessage: ChatMessage) {
        val collectionRef = firestore.collection("chats")

        collectionRef.add(chatMessage)
            .addOnSuccessListener {
                editTextChatMessage.text.clear()
                recyclerViewChat.smoothScrollToPosition(chatAdapter.itemCount)
            }
            .addOnFailureListener {
                // Handle the failure
            }
    }


    private fun listenForChatMessages() {
        firestore.collection("chats")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle the exception
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messages = snapshot.toObjects(ChatMessage::class.java)
                    chatAdapter.setChatMessages(messages)
                    recyclerViewChat.smoothScrollToPosition(chatAdapter.itemCount)
                }
            }
    }
}
