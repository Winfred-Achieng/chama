package com.example.chama

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class ChamaCreatedActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chama_created)


        // Retrieve the chamaName and other details from the intent extras
        val chamaName = intent.getStringExtra("chamaName")
        val description = intent.getStringExtra("description")
        val goals = intent.getStringExtra("goals")
        val targetPerMonth = intent.getStringExtra("targetPerMonth")
        val numberOfMembers = intent.getStringExtra("numberOfMembers")

// Retrieve the profilePictureUri from the intent extras
        val profilePictureUri = intent.getParcelableExtra<Uri>("profilePictureUri")

// Create an instance of HeaderFragment and pass the chamaName and profilePictureUri as arguments
        val headerFragment = HeaderFragment.newInstance(chamaName, profilePictureUri)

        supportFragmentManager.beginTransaction()
            .replace(R.id.headerContainer, headerFragment)
            .commit()




        viewPager = findViewById(R.id.viewPager)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(TransactFragment(), "TRANSACT")
        adapter.addFragment(GoalsFragment(), "GOALS")
        adapter.addFragment(MembersFragment(), "MEMBERS")
        adapter.addFragment(LoansFragment(), "LOANS")
        adapter.addFragment(HistoryFragment(), "HISTORY")
        adapter.addFragment(ChatFragment(), "CHAT")
        // Add more fragments as needed
        viewPager.adapter = adapter

        // Set up TabLayout
        tabLayout = findViewById(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)
    }
}
