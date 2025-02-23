package com.example.receteo.ui.recipe

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receteo.R
import com.example.receteo.data.remote.models.*
import com.example.receteo.databinding.FragmentRecipeListBinding
import com.example.receteo.ui.favorites.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeListFragment : Fragment() {
    private val favoritesViewModel: FavoritesViewModel by viewModels()
    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeViewModel by activityViewModels()
    private lateinit var adapter: RecipeAdapter
    private val recipeList = mutableListOf<RecipeModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        viewModel.fetchRecipes()

        binding.fabAddRecipe.setOnClickListener {
            findNavController().navigate(R.id.recipeCreateFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = RecipeAdapter(
            recipeList.toMutableList(),
            onEditClick = { recipe ->
                val bundle = Bundle().apply {
                    putInt("recipeId", recipe.id)
                }
                findNavController().navigate(R.id.recipeCreateFragment, bundle)
            },
            onDeleteClick = { recipe ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar receta")
                    .setMessage("¿Estás seguro de que deseas eliminar esta receta?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        viewModel.deleteRecipe(recipe.id)
                        Toast.makeText(requireContext(), "Receta eliminada", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            },
            onFavoriteClick = { recipe ->
                favoritesViewModel.toggleFavorite(recipe)
            },
            onShareClick = { recipe ->
                compartirReceta(recipe)
            },
            onRecipeClick = { recipe ->
                val bundle = Bundle().apply {
                    putInt("recipeId", recipe.id)
                    putString("recipeName", recipe.name)
                    putString("recipeIngredients", recipe.ingredients)
                    putString("recipeDescription", recipe.descriptions)
                    putString("recipeImageUrl", recipe.imageUrl)
                }
                findNavController().navigate(R.id.recipeDetailFragment, bundle)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecipeListFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.updateData(recipes)
            adapter.notifyDataSetChanged() // 🔥 Fuerza la actualización del RecyclerView para recargar las imágenes
        }

        viewModel.successMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.fetchRecipes() // 🔄 Vuelve a cargar la lista después de editar
            }
        }
    }



    override fun onResume() {
        super.onResume()
        viewModel.fetchRecipes()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
