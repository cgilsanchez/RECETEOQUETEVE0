package com.example.receteo.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.receteo.databinding.FragmentRecipeDetailBinding

class RecipeDetailFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar los datos enviados desde RecipeListFragment
        val recipeName = arguments?.getString("recipeName") ?: "Sin nombre"
        val recipeIngredients = arguments?.getString("recipeIngredients") ?: "Sin ingredientes"
        val recipeDescription = arguments?.getString("recipeDescription") ?: "Sin descripción"
        val recipeImageUrl = arguments?.getString("recipeImageUrl") ?: ""

        // Mostrar los datos en la UI
        binding.textRecipeTitle.text = recipeName
        binding.textRecipeIngredientsContent.text = recipeIngredients
        binding.textRecipeDescriptionContent.text = recipeDescription

        // Cargar la imagen con Glide
        Glide.with(this)
            .load(recipeImageUrl)
            .placeholder(com.example.receteo.R.drawable.ic_placeholder)
            .error(com.example.receteo.R.drawable.ic_placeholder)
            .into(binding.imageRecipe)

        // Configurar botón de retroceso
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}
