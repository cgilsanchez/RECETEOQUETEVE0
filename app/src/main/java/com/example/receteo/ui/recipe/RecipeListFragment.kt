package com.example.receteo.ui.recipe

import RecipeAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receteo.R
import com.example.receteo.data.remote.models.RecipeModel
import com.example.receteo.databinding.FragmentRecipeListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    private lateinit var binding: FragmentRecipeListBinding
    private val viewModel: RecipeViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter
    private val recipeList = mutableListOf<RecipeModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeRecipes()
        viewModel.fetchRecipes()
    }

    private fun setupRecyclerView() {
        adapter = RecipeAdapter(recipeList,
            onRecipeClick = { recipe ->
                val bundle = Bundle()
                bundle.putInt("recipeId", recipe.id) // ✅ Solo pasamos el ID
                findNavController().navigate(R.id.recipeCreateFragment, bundle)
            },
            onDeleteClick = { recipe ->
                viewModel.deleteRecipe(recipe.id)
                Toast.makeText(requireContext(), "Eliminando: ${recipe.attributes.name}", Toast.LENGTH_SHORT).show()
            })

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecipeListFragment.adapter
        }
    }

    private fun observeRecipes() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            if (recipes.isNotEmpty()) {
                recipeList.clear()
                recipeList.addAll(recipes)
                adapter.updateData(recipeList) // ✅ Ahora actualiza la lista correctamente
            } else {
                Toast.makeText(requireContext(), "No se encontraron recetas", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
