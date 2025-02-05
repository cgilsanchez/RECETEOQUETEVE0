package com.example.receteo.ui.recipe

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.receteo.ui.recipe.RecipeViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receteo.R
import com.example.receteo.data.remote.models.*
import com.example.receteo.databinding.FragmentRecipeListBinding
import com.example.receteo.ui.favorites.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

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
                        viewModel.deleteRecipe(recipe.id) // ✅ Ahora debe reconocer deleteRecipe correctamente
                        Toast.makeText(requireContext(), "Receta eliminada", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            ,
            onFavoriteClick = { recipe ->
                favoritesViewModel.toggleFavorite(recipe) // 🔥 Usa la instancia correcta
            }

        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecipeListFragment.adapter
        }
    }



    private fun observeViewModel() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.updateData(recipes) // Actualiza la lista de recetas con el estado actualizado
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.fetchRecipes() // Recarga las recetas cada vez que el fragmento es visible
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
