package com.example.receteo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.R

class RecipeListFragment : Fragment() {

    private val favoriteRecipes = mutableListOf<String>()

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

        val recipes = listOf("Receta 1", "Receta 2", "Receta 3")
        val adapter = RecipeAdapter(recipes,
            onClick = { recipeName ->
                val bundle = Bundle().apply {
                    putString("recipeName", recipeName)
                }
                Navigation.findNavController(view).navigate(
                    R.id.action_recipeListFragment_to_recipeDetailFragment, bundle
                )
            },
            onFavoriteClick = { recipeName ->
                if (favoriteRecipes.contains(recipeName)) {
                    favoriteRecipes.remove(recipeName)
                    Toast.makeText(requireContext(), "$recipeName eliminado de favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    favoriteRecipes.add(recipeName)
                    Toast.makeText(requireContext(), "$recipeName añadido a favoritos", Toast.LENGTH_SHORT).show()
                }
            }
        )

        recyclerView.adapter = adapter
    }
}
