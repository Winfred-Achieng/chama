package com.example.chama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chama.R
import com.example.chama.models.Member

class MembersAdapter(private val members: List<Member>) :
    RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.members_recycler_view, parent, false)
        return MemberViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]
        holder.bind(member)
    }

    override fun getItemCount(): Int = members.size

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        private val memberNameTextView: TextView = itemView.findViewById(R.id.memberNameTextView)

        fun bind(member: Member) {
            // Set the member name
            memberNameTextView.text = member.name

            // Set the profile picture (Replace placeholder image with actual member image)
            profileImageView.setImageResource(R.drawable.ic_profile)
        }
    }
}



