package com.example.receteo.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.receteo.R
import com.example.receteo.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)


        val sharedPreferences = requireActivity().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        if (sharedPreferences.getString("jwt", null) != null) {
            findNavController().navigate(R.id.action_loginFragment_to_nav_recipes)
            return
        }

        binding.apply {
            buttonLogin.setOnClickListener {
                val identifier = editTextUsername?.text?.toString()?.trim()
                val password = editTextPassword?.text?.toString()?.trim()

                if (identifier.isNullOrEmpty() || password.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                authViewModel.login(identifier, password, requireContext()) { user ->
                    if (user != null) {
                        Toast.makeText(requireContext(), "Login exitoso", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_loginFragment_to_nav_recipes)
                    } else {
                        Toast.makeText(requireContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            buttonRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }
}
