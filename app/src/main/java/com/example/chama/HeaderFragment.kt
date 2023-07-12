package com.example.chama

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chama.databinding.FragmentHeaderBinding

class HeaderFragment(private val profilePictureUri: Uri?) : Fragment() {
    private lateinit var settingsIcon: ImageView
    private lateinit var userProfilePicture: ImageView
    private lateinit var notificationsIcon: ImageView
    private var _binding: FragmentHeaderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHeaderBinding.inflate(inflater, container, false)
        val view = binding.root

        settingsIcon = binding.settingsIcon
        userProfilePicture = binding.userProfilePicture
        notificationsIcon = binding.notificationsIcon

        // Set profile picture if URI is available
        profilePictureUri?.let { uri ->
            Glide.with(requireContext())
                .load(uri)
                .placeholder(R.drawable.default_profile_picture) // Placeholder image if URI is null
                .circleCrop()
                .into(userProfilePicture)
        }

        settingsIcon.setOnClickListener {
            // Handle settings icon click action here
        }

        userProfilePicture.setOnClickListener {
            // Handle user profile picture click action here
        }

        notificationsIcon.setOnClickListener {
            // Handle notifications icon click action here
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setChamaName(chamaName: String?) {
        // Update the UI with the chamaName
        binding.headerTitle.text = chamaName
    }
}
