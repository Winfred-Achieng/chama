package com.example.chama

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.chama.databinding.ActivityChamaCreatedBinding
import com.google.android.material.tabs.TabLayout

class ChamaCreatedActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var binding: ActivityChamaCreatedBinding
    private var profilePictureUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChamaCreatedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the chamaName and other details from the intent extras
        val chamaId = intent.getStringExtra("chamaId")
        val chamaName = intent.getStringExtra("chamaName")
        val description = intent.getStringExtra("description")
        val goals = intent.getStringExtra("goals")
        val targetPerMonth = intent.getStringExtra("targetPerMonth")
        val numberOfMembers = intent.getStringExtra("numberOfMembers")
        val firstName = intent.getStringExtra("firstName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""
        val userProfilePictureString = intent.getStringExtra("userProfilePicture")
        profilePictureUri = Uri.parse(userProfilePictureString)
        // Retrieve the profilePictureUri from the intent extras
        //val profilePictureUri = intent.getParcelableExtra<Uri>("profilePictureUri")

        // Create an instance of HeaderFragment and pass the chamaName and profilePictureUri as arguments
        val headerFragment = HeaderFragment.newInstance(chamaId, chamaName, profilePictureUri)
        supportFragmentManager.beginTransaction()
            .replace(R.id.headerContainer, headerFragment)
            .commit()

        viewPager = binding.viewPager
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(TransactFragment(), "TRANSACT")

        // Create an instance of GoalsFragment and pass the chama details as arguments
        val goalsFragment = GoalsFragment().apply {
            arguments = Bundle().apply {
                putString("chamaId", chamaId)
                putString("chamaName", chamaName)
                putString("description", description)
                putString("goals", goals)
                putString("targetPerMonth", targetPerMonth)
            }
        }
        adapter.addFragment(goalsFragment, "GOALS")

        adapter.addFragment(MembersFragment(), "MEMBERS")
        adapter.addFragment(LoansFragment(), "LOANS")
        adapter.addFragment(HistoryFragment(), "HISTORY")
        adapter.addFragment(ChatFragment(), "CHAT")

        val chatFragment = ChatFragment().apply {
            arguments = Bundle().apply {
                putString("chamaId", chamaId)
                putString("firstName", firstName)
                putString("lastName", lastName)
            }
        }
        // Add more fragments as needed
        viewPager.adapter = adapter

        // Set up TabLayout
        tabLayout = binding.tabLayout
        tabLayout.setupWithViewPager(viewPager)
    }
}
