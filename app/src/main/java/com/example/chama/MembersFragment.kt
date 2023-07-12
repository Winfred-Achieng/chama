package com.example.chama

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chama.models.Member

class MembersFragment : Fragment() {

    // Declare a list of members (Replace with your actual member data)
    private val members: List<Member> = listOf(
        Member("John Doe", "profile_image_url_1"),
        Member("Jane Smith", "profile_image_url_2"),
        // Add more members as needed
    )

    private lateinit var recyclerView: RecyclerView
    private lateinit var membersAdapter: MembersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_members, container, false)
        recyclerView = view.findViewById(R.id.membersRecyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        membersAdapter = MembersAdapter(members)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = membersAdapter
        }
    }
}
