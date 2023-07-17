package com.example.chama
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chama.R
import com.example.chama.SettingsActivity
import com.example.chama.databinding.FragmentHeaderBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class HeaderFragment(private val profilePictureUri: String?) : Fragment() {
    private lateinit var settingsIcon: ImageView
    private lateinit var userProfilePicture: ImageView
    private lateinit var notificationsIcon: ImageView
    private var _binding: FragmentHeaderBinding? = null
    private val binding get() = _binding!!
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHeaderBinding.inflate(inflater, container, false)
        val view = binding.root

        settingsIcon = binding.headerLayout.settingsIcon
        userProfilePicture = binding.headerLayout.userProfilePicture
        notificationsIcon = binding.headerLayout.notificationsIcon

        // Initialize Firebase Storage
        val storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        // Retrieve the chamaName from the arguments
        val chamaName = arguments?.getString("chamaName")

        // Update the UI with the chamaName
        setChamaName(chamaName)

         userProfilePicture= binding.headerLayout.userProfilePicture



        if (!profilePictureUri.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(profilePictureUri)
                .placeholder(R.drawable.ic_profile) // Placeholder image if URI is null
                .circleCrop()
                .into(userProfilePicture)

        } else {
            // If the profilePictureUri is null, retrieve the user profile picture from Firestore
            val userId = getUserId() // Replace this with your own logic to get the current user ID

            if (userId != null) {
                val profilePictureRef = storageReference.child("profilePictures/$userId.jpg")
                profilePictureRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        // Display the retrieved profile picture in the userProfilePicture ImageView
                        Glide.with(requireContext())
                            .load(uri)
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .into(userProfilePicture)
                    }
                    .addOnFailureListener { exception ->
                        // Failed to retrieve the profile picture from Firestore
                    }
            }
        }

        settingsIcon.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        notificationsIcon.setOnClickListener {

        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setChamaName(chamaName: String?) {
        // Update the UI with the chamaName
        binding.headerLayout.headerTitle.text = chamaName
    }

    private fun getUserId(): String? {
        // Replace this with your own logic to get the current user ID
        // For example, if you are using Firebase Authentication, you can use FirebaseAuth.getInstance().currentUser?.uid
        return null
    }

    companion object {
        fun newInstance(chamaId: String?, chamaName: String?, profilePictureUri: Uri?): HeaderFragment {
            val fragment = HeaderFragment(profilePictureUri?.toString())
            val args = Bundle().apply {
                putString("chamaId", chamaId)
                putString("chamaName", chamaName)
                putParcelable("profilePictureUri", profilePictureUri)
            }
            fragment.arguments = args
            return fragment
        }
    }
}