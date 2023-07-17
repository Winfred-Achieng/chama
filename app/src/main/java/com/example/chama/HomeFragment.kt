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

class HomeFragment : Fragment() {

    private lateinit var firstName: String
    private lateinit var lastName: String
    private var profilePictureUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the arguments from the bundle
        arguments?.let { args ->
            firstName = args.getString("firstName", "")
            lastName = args.getString("lastName", "")
            profilePictureUri = args.getParcelable("userProfilePicture")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Update the UI with the retrieved data
        // For example, set the first name and last name to TextViews
        //val firstNameTextView = view.findViewById<TextView>(R.id.textViewFirstName)
        //val lastNameTextView = view.findViewById<TextView>(R.id.textViewLastName)
        //firstNameTextView.text = firstName
        //lastNameTextView.text = lastName

        // Load and display the user profile picture if available
        val profilePictureImageView = view.findViewById<ImageView>(R.id.userProfilePicture)
        if (profilePictureUri != null) {
            Glide.with(requireContext())
                .load(profilePictureUri)
                .apply(RequestOptions.circleCropTransform())
                .into(profilePictureImageView)
        } else {
            // Set a default profile picture if the user has not uploaded one
            profilePictureImageView.setImageResource(R.drawable.ic_profile)
        }
        // Rest of your code...

        return view
    }
    companion object {
        fun newInstance(firstName: String, lastName: String, profilePictureUri: Uri?): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle().apply {
                putString("firstName", firstName)
                putString("lastName", lastName)
                putParcelable("userProfilePicture", profilePictureUri)
            }
            fragment.arguments = args
            return fragment
        }
    }

}
    // Rest of your code...

