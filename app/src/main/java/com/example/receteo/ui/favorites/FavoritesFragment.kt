package com.example.receteo.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receteo.databinding.FragmentFavoritesBinding
import com.example.receteo.ui.recipe.RecipeAdapter
import com.example.receteo.ui.recipe.RecipeViewModel

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: RecipeAdapter

    // Usamos activityViewModels para compartir la instancia con otros fragments
    private val favoritesViewModel: FavoritesViewModel by activityViewModels()
    private val recipeViewModel: RecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RecipeAdapter(
            mutableListOf(),
            onEditClick = { /* Implementar edición */ },
            onDeleteClick = { /* Implementar eliminación */ },
            onFavoriteClick = { recipeViewModel.toggleFavorite(it) }
        )

        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        favoritesViewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            adapter.updateData(favorites)
            binding.textNoFavorites.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
        }
    }


}
