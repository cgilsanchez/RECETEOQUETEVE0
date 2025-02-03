package com.example.receteo.ui.recipe

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receteo.R
import com.example.receteo.data.remote.models.*
import com.example.receteo.databinding.FragmentRecipeListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeListFragment : Fragment() {

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter
    private val recipeList = mutableListOf<RecipeData>()

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
        adapter = RecipeAdapter(recipeList,
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
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecipeListFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            Log.d("RecipeListFragment", "Número de recetas recibidas: ${recipes.size}")

            val convertedRecipes = recipes.map { recipeModel ->
                RecipeData(
                    id = recipeModel.id,
                    attributes = RecipeAttributes(
                        name = recipeModel.name,
                        descriptions = recipeModel.descriptions,
                        ingredients = recipeModel.ingredients,
                        createdAt = recipeModel.createdAt,
                        image = ImageData(
                            data = ImageAttributes(
                                attributes = ImageFormats(
                                    url = recipeModel.imageUrl
                                )
                            )
                        )
                    )
                )
            }

            recipeList.clear()
            recipeList.addAll(convertedRecipes)
            Log.d("RecipeListFragment", "Adaptador actualizado con ${recipeList.size} recetas")
            adapter.notifyDataSetChanged()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
