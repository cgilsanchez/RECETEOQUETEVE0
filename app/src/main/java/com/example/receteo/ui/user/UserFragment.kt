package com.example.receteo.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.receteo.R
import com.example.receteo.ui.auth.AuthViewModel
import com.example.receteo.ui.auth.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment(R.layout.fragment_user) {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt", null)

        val usernameTextView: TextView = view.findViewById(R.id.tv_user_name)
        val emailTextView: TextView = view.findViewById(R.id.tv_user_email)
        val logoutButton: Button = view.findViewById(R.id.btn_logout)

        if (!token.isNullOrEmpty()) {
            authViewModel.getUserData(token)
        }

        authViewModel.userData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                usernameTextView.text = user.username
                emailTextView.text = user.email
            }
        }

        // Cerrar sesión
        logoutButton.setOnClickListener {
            sharedPreferences.edit().clear().apply()  // Borra el token
            val intent = Intent(requireContext(), LoginFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
