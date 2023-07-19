package com.example.chama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val chatMessages: ArrayList<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
//
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.bind(chatMessage)
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    fun setChatMessages(messages: List<ChatMessage>) {
        chatMessages.clear()
        chatMessages.addAll(messages)
        notifyDataSetChanged()
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderTextView: TextView = itemView.findViewById(R.id.textViewSender)
        private val messageTextView: TextView = itemView.findViewById(R.id.textViewMessage)

        fun bind(chatMessage: ChatMessage) {
            senderTextView.text = chatMessage.senderName
            messageTextView.text = chatMessage.message
        }
    }
}
