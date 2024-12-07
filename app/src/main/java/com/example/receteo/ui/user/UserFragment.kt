package com.example.receteo.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.receteo.R
import com.example.receteo.ui.auth.LoginFragment

class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Simulación de datos del usuario
        val userName = "Carlos Pérez"
        val userEmail = "carlos.perez@example.com"

        // Mostrar nombre y correo del usuario
        view.findViewById<TextView>(R.id.tv_user_name).text = userName
        view.findViewById<TextView>(R.id.tv_user_email).text = userEmail

        // Configurar el botón de cerrar sesión
        val logoutButton = view.findViewById<Button>(R.id.btn_logout)
        logoutButton.setOnClickListener {
            // Redirigir a la pantalla de inicio de sesión
            val intent = Intent(requireContext(), LoginFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}