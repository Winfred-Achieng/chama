package com.example.chama

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class TransactFragment : Fragment() {

    private lateinit var buttonContribute: Button
    private lateinit var buttonHistory: Button
    private lateinit var backButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transact, container, false)
        buttonContribute = view.findViewById(R.id.buttonContribute)
        buttonHistory = view.findViewById(R.id.buttonHistory)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonContribute.setOnClickListener {
            val intent = Intent(requireContext(), ContributeActivity::class.java)
            startActivity(intent)
        }

        buttonHistory.setOnClickListener {
            val intent = Intent(requireContext(), HistoryTransactionActivity::class.java)
            startActivity(intent)
        }


    }
}