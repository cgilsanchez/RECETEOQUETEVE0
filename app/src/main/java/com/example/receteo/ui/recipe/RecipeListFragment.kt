package com.example.receteo.ui.recipe

import com.example.receteo.ui.recipe.RecipeAdapter
import android.os.Bundle
import android.util.Log
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
        observeViewModel()
        viewModel.fetchRecipes()

        binding.fabAddRecipe.setOnClickListener {
            findNavController().navigate(R.id.recipeCreateFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = RecipeAdapter(recipeList,
            onEditClick = { recipe ->
                val bundle = Bundle().apply {
                    putInt("recipeId", recipe.id)
                }
                findNavController().navigate(R.id.recipeCreateFragment, bundle)
            },
            onDeleteClick = { recipe ->
                viewModel.deleteRecipe(recipe.id)
                viewModel.fetchRecipes()  // 🔥 ACTUALIZAR UI DESPUÉS DE BORRAR
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecipeListFragment.adapter
        }
    }


    private fun observeViewModel() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            if (recipes.isNotEmpty()) {
                adapter.updateData(recipes)  // 🔥 ACTUALIZA LA LISTA
            } else {
                Log.e("RecipeListFragment", "No recipes found")
            }
        }
    }

}
