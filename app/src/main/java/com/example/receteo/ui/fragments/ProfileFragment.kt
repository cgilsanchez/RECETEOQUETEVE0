package com.example.receteo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.recipeproject.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
        val userEmailTextView: TextView = view.findViewById(R.id.userEmailTextView)

        // Simulando datos de usuario (reemplazar con datos reales del ViewModel o DataStore)
        val userName = "John Doe"
        val userEmail = "john.doe@example.com"

        userNameTextView.text = userName
        userEmailTextView.text = userEmail
    }
}