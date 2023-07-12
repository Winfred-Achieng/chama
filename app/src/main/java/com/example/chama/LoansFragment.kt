package com.example.chama

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chama.databinding.FragmentLoansBinding
import com.google.firebase.firestore.FirebaseFirestore

class LoansFragment : Fragment() {

    private var _binding: FragmentLoansBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var loanId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoansBinding.inflate(inflater, container, false)
        val view = binding.root
        firestore = FirebaseFirestore.getInstance()

        binding.btnPayLoan.setOnClickListener {
            val intent = Intent(requireActivity(), PayLoanActivity::class.java)
            startActivity(intent)
        }

        binding.btnApplyLoan.setOnClickListener {
            val intent = Intent(requireActivity(), ApplyLoanActivity::class.java)
            startActivity(intent)
        }

        // Retrieve the loan ID from shared preferences
        val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        loanId = sharedPrefs.getString("loanId", "") ?: ""

        // Retrieve and display loan details
        if (loanId.isNotEmpty()) {
            getLoanDetails(loanId)

        }
        return view
    }

    private fun getLoanDetails(loanId: String) {
        firestore.collection("loans")
            .document(loanId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val loanAmount = documentSnapshot.getString("loanAmount")
                    val loanPurpose = documentSnapshot.getString("loanPurpose")
                    val loanStatus = documentSnapshot.getString("loanStatus")

                    // Update TextViews with loan details
                    binding.loanAmountEditText.text = "Loan Amount: ksh. $loanAmount"
                    binding.dueDateEditText.text = "Due Date: "
                    binding.statusEditText.text = "Loan Status: "
                    binding.balanceEditText.text = "Balance:  "
                } else {
                    // Document does not exist
                    Toast.makeText(requireContext(), "Loan details not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Failed to retrieve loan details
                Toast.makeText(requireContext(), "Failed to retrieve loan details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
