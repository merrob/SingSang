package com.example.singsang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController


class BlankFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_blank, container, false)

        val btnUeben = view.findViewById<Button>(R.id.btn_ueben)
        btnUeben.setOnClickListener {
            findNavController().navigate(R.id.action_blankFragment_to_ueben)
        }

        val btnSehen = view.findViewById<Button>(R.id.btn_sehen)
        btnSehen.setOnClickListener{
            findNavController().navigate(R.id.action_blankFragment_to_sehen)
        }

        val btnHoeren = view.findViewById<Button>(R.id.btn_hoeren)
        btnHoeren.setOnClickListener {
            findNavController().navigate(R.id.action_blankFragment_to_hoeren)
        }

        return view
    }


}