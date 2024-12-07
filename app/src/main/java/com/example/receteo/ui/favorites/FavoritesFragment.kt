package com.example.receteo.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.R
import com.example.receteo.ui.recipe.RecipeAdapter
import com.example.receteo.ui.viewModel.SharedViewModel

class FavoritesFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_favorites)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observar los favoritos desde el SharedViewModel
        sharedViewModel.favoriteRecipes.observe(viewLifecycleOwner) { favorites ->
            val adapter = RecipeAdapter(
                recipes = favorites,
                onClick = { recipeName ->
                    // Navegar al detalle de la receta favorita
                },
                onFavoriteClick = { recipeName ->
                    sharedViewModel.removeFavorite(recipeName)
                }
            )
            recyclerView.adapter = adapter
        }
    }
}
