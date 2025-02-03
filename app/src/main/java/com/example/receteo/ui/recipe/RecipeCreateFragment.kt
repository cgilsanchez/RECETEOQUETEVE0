package com.example.receteo.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.receteo.data.remote.models.RecipeRequestModel
import com.example.receteo.databinding.FragmentRecipeCreateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeCreateFragment : Fragment() {

    private lateinit var binding: FragmentRecipeCreateBinding
    private val viewModel: RecipeViewModel by viewModels()
    private var recipeId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeId = arguments?.getInt("recipeId", -1)

        if (recipeId != null && recipeId != -1) {
            viewModel.getRecipeById(recipeId!!)
            viewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
                recipe?.let {
                    binding.etRecipeName.setText(it.name)
                    binding.etDescription.setText(it.descriptions)
                    binding.etIngredients.setText(it.ingredients)
                    binding.etImageUrl.setText(it.imageUrl ?: "")
                }
            }
        }

        binding.btnSaveRecipe.setOnClickListener { saveOrUpdateRecipe() }
    }

    private fun saveOrUpdateRecipe() {
        val name = binding.etRecipeName.text.toString().trim()
        val descriptions = binding.etDescription.text.toString().trim()
        val ingredients = binding.etIngredients.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim()

        val recipeRequest = RecipeRequestModel(name, descriptions, ingredients, imageUrl)

        if (recipeId == null || recipeId == -1) {
            viewModel.createRecipe(recipeRequest)
        } else {
            viewModel.updateRecipe(recipeRequest, recipeId!!)
        }

        findNavController().popBackStack()
    }
}
