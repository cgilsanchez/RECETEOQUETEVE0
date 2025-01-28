package com.example.receteo.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.receteo.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Encuentra las vistas
        val usernameEditText = view.findViewById<EditText>(R.id.editTextUsername)
        val emailEditText = view.findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = view.findViewById<EditText>(R.id.editTextPassword)
        val registerButton = view.findViewById<Button>(R.id.buttonSubmitRegister)
        val backToLoginButton = view.findViewById<Button>(R.id.buttonBackToLogin)

        registerButton.setOnClickListener {
            val username = usernameEditText?.text.toString().trim()
            val email = emailEditText?.text.toString().trim()
            val password = passwordEditText?.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.register(username, email, password)
        }

        backToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        authViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Toast.makeText(requireContext(), "Registro exitoso, inicia sesión", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            } else {
                Toast.makeText(requireContext(), "Error en el registro", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
