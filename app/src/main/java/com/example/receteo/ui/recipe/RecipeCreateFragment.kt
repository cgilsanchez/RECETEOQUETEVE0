package com.example.receteo.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveRecipe.setOnClickListener {
            val name = binding.etRecipeName.text.toString()
            val ingredients = binding.etIngredients.text.toString()
            val description = binding.etDescription.text.toString()
            val imageUrl = binding.etImageUrl.text.toString()

            if (name.isNotEmpty() && ingredients.isNotEmpty() && description.isNotEmpty()) {
                val newRecipe = RecipeRequestModel(name, ingredients, description, imageUrl)
                viewModel.createRecipe(newRecipe)
                findNavController().popBackStack()
            } else {
                binding.etRecipeName.error = "Campo obligatorio"
                binding.etIngredients.error = "Campo obligatorio"
                binding.etDescription.error = "Campo obligatorio"
            }
        }
    }
}
