package com.example.receteo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeproject.R
import com.example.recipeproject.ui.adapters.RecipeAdapter
import com.example.recipeproject.ui.viewmodels.MainViewModel

class FavoritesFragment : Fragment() {

    private lateinit var favoriteRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        favoriteRecyclerView = view.findViewById(R.id.favoriteRecyclerView)
        favoriteRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observamos las recetas favoritas desde el ViewModel
        mainViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            val favoriteRecipes = recipes.filter { it.isFavorite }
            recipeAdapter = RecipeAdapter(
                favoriteRecipes,
                onFavoriteClick = { recipe ->
                    mainViewModel.toggleFavorite(recipe)
                },
                onRecipeClick = { recipe ->
                    // Navegar a detalles
                }
            )
            favoriteRecyclerView.adapter = recipeAdapter
        }
    }
}