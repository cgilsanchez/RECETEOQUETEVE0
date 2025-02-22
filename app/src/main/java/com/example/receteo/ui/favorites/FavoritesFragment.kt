package com.example.receteo.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receteo.R
import com.example.receteo.data.remote.models.RecipeModel
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
            onFavoriteClick = { recipeViewModel.toggleFavorite(it.id) },
            onShareClick = { recipe -> compartirReceta(recipe) },
            onRecipeClick = { recipe ->  // ✅ Agregado para manejar el clic en la tarjeta completa
                val bundle = Bundle().apply {
                    putInt("recipeId", recipe.id)
                }
                findNavController().navigate(R.id.recipeDetailFragment, bundle)
            }
        )

        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = adapter

        observeViewModel()  // Llama al método para observar el viewModel
    }

    override fun onResume() {
        super.onResume()
        recipeViewModel.fetchRecipes() // Asegurarnos de actualizar la lista de recetas
    }

    private fun observeViewModel() {
        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            val favoriteRecipes = recipes.filter { it.isFavorite } // Solo las recetas favoritas
            adapter.updateData(favoriteRecipes)
            binding.textNoFavorites.visibility = if (favoriteRecipes.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun compartirReceta(recipe: RecipeModel) {
        val textoCompartir = """
            🍽️ *${recipe.name}*
            
            📝 *Ingredientes:*
            ${recipe.ingredients}
            
            📝 *Descripción:*
            ${recipe.descriptions}
            
            📲 Compartido desde *Receteo*
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, textoCompartir)
        }
        startActivity(Intent.createChooser(intent, "Compartir receta vía"))
    }
}
