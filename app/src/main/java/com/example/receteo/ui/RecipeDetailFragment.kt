package com.example.receteo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.receteo.R

class RecipeDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el argumento enviado desde el RecipeListFragment
        val recipeName = arguments?.getString("recipeName") ?: "Detalle de la receta"

        val titleTextView = view.findViewById<TextView>(R.id.text_recipe_title)
        titleTextView.text = recipeName
    }

}
