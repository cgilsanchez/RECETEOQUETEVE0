package com.example.receteo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.R

class RecipeListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_recipes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val recipes = listOf("Receta 1", "Receta 2", "Receta 3") // Datos de ejemplo
        val adapter = RecipeAdapter(recipes) { recipeName ->
            // Crear un bundle con los datos de la receta seleccionada
            val bundle = Bundle().apply {
                putString("recipeName", recipeName)
            }

            // Usar Navigation.findNavController para manejar la navegación
            Navigation.findNavController(view).navigate(
                R.id.action_recipeListFragment_to_recipeDetailFragment, bundle
            )
        }

        recyclerView.adapter = adapter
    }
}

