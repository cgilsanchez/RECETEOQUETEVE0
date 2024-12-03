package com.example.receteo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.R
import com.example.receteo.ui.viewModel.SharedViewModel

class RecipeListFragment : Fragment() {

    // Usamos el SharedViewModel para manejar los favoritos
    private val sharedViewModel: SharedViewModel by activityViewModels()

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

        // Lista de recetas de ejemplo
        val recipes = listOf("Receta 1", "Receta 2", "Receta 3")

        // Configuración del adaptador
        val adapter = RecipeAdapter(
            recipes,
            onClick = { recipeName ->
                // Navegar al RecipeDetailFragment con el nombre de la receta
                val bundle = Bundle().apply { putString("recipeName", recipeName) }
                Navigation.findNavController(view).navigate(
                    R.id.action_recipeListFragment2_to_recipeDetailFragment,
                    bundle
                )
            },
            onFavoriteClick = { recipeName ->
                // Manejar la lógica de favoritos con el SharedViewModel
                if (sharedViewModel.favoriteRecipes.value!!.contains(recipeName)) {
                    sharedViewModel.removeFavorite(recipeName)
                    Toast.makeText(requireContext(), "$recipeName eliminado de favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    sharedViewModel.addFavorite(recipeName)
                    Toast.makeText(requireContext(), "$recipeName añadido a favoritos", Toast.LENGTH_SHORT).show()
                }
            }
        )

        recyclerView.adapter = adapter

        // Manejar clic en el botón para agregar receta
        view.findViewById<View>(R.id.buttonAddRecipe).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_recipeListFragment2_to_recipeCreateFragment)
        }
    }
}

