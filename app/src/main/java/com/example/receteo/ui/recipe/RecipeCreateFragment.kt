package com.example.receteo.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.receteo.R

class RecipeCreateFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleField = view.findViewById<EditText>(R.id.editTextTitle)
        val ingredientsField = view.findViewById<EditText>(R.id.editTextIngredients)
        val descriptionField = view.findViewById<EditText>(R.id.editTextDescription)
        val saveButton = view.findViewById<Button>(R.id.buttonSave)

        saveButton.setOnClickListener {
            val title = titleField.text.toString()
            val ingredients = ingredientsField.text.toString()
            val description = descriptionField.text.toString()

            if (title.isEmpty() || ingredients.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Aquí puedes agregar la lógica para guardar la receta
                Toast.makeText(requireContext(), "Receta guardada: $title", Toast.LENGTH_SHORT).show()
                // Opcionalmente, puedes navegar de regreso al RecipeListFragment
                requireActivity().onBackPressed()
            }
        }
    }
}
