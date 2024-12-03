package com.example.receteo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.receteo.R

class ChefCreateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chef_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameInput = view.findViewById<EditText>(R.id.editTextChefName)
        val specialtyInput = view.findViewById<EditText>(R.id.editTextChefSpecialty)
        val saveButton = view.findViewById<Button>(R.id.buttonSaveChef)

        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val specialty = specialtyInput.text.toString()

            if (name.isNotEmpty() && specialty.isNotEmpty()) {
                Toast.makeText(requireContext(), "Chef $name guardado!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed() // Volver a la lista de chefs
            } else {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}