package com.example.chama

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.chama.R

class TransactFragment : Fragment() {
    private lateinit var buttonContribute: Button
    private lateinit var buttonHistory: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonContribute = view.findViewById(R.id.buttonContribute)
        buttonHistory = view.findViewById(R.id.buttonHistory)

        buttonContribute.setOnClickListener {
            val intent = Intent(activity, ContributeActivity::class.java)
            startActivity(intent)
        }

        buttonHistory.setOnClickListener {
            val intent = Intent(activity, HistoryTransactionActivity::class.java)
            startActivity(intent)
        }
    }
}
