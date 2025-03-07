package com.example.receteo.ui.auth

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.receteo.R
import com.example.receteo.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        binding.apply {
            buttonSubmitRegister.setOnClickListener {
                val username = editTextUsername?.text?.toString()?.trim()
                val email = editTextEmail?.text?.toString()?.trim()
                val password = editTextPassword?.text?.toString()?.trim()

                if (username.isNullOrEmpty() || email.isNullOrEmpty() || password.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(requireContext(), "Correo electrónico inválido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (password.length < 8) {
                    Toast.makeText(requireContext(), "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                authViewModel.register(username, email, password, requireContext()) { user ->
                    if (user != null) {
                        Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    } else {
                        Toast.makeText(requireContext(), "Error en el registro", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            buttonBackToLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }
}
