package com.example.receteo.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.receteo.R

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navegar al registro
        view.findViewById<Button>(R.id.buttonRegister).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Navegar al RecipeListFragment al presionar el botón de inicio de sesión
        view.findViewById<Button>(R.id.buttonLogin).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recipeListFragment)
        }
    }
}

