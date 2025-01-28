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
class LoginFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Encuentra las vistas
        val usernameEditText = view.findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = view.findViewById<EditText>(R.id.editTextPassword)
        val loginButton = view.findViewById<Button>(R.id.buttonLogin)
        val registerButton = view.findViewById<Button>(R.id.buttonRegister)

        loginButton.setOnClickListener {
            val username = usernameEditText?.text.toString().trim() // Manejo seguro con "?.text"
            val password = passwordEditText?.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.login(username, password)
        }

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        authViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Toast.makeText(requireContext(), "Bienvenido ${user.username}", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_nav_recipes)
            } else {
                Toast.makeText(requireContext(), "Error en login, verifica tus datos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
