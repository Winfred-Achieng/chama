package com.example.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment() {
//
    private lateinit var editTextChatMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages: ArrayList<ChatMessage> = ArrayList()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var senderName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        editTextChatMessage = view.findViewById(R.id.editTextChatMessage)
        buttonSend = view.findViewById(R.id.buttonSend)
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat)

        chatAdapter = ChatAdapter(chatMessages)

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerViewChat.layoutManager = layoutManager
        recyclerViewChat.adapter = chatAdapter

        val firstName = requireActivity().intent.getStringExtra("firstName")
        val lastName = requireActivity().intent.getStringExtra("lastName")
        senderName = "$firstName $lastName"

        buttonSend.setOnClickListener {
            val message = editTextChatMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                val chatMessage = ChatMessage(senderName, message)
                saveChatMessage(chatMessage)
            }
        }

        listenForChatMessages()

        return view
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