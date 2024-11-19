package com.example.receteo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.recipeproject.R

class RecipeDetailsFragment : Fragment() {

    private val args: RecipeDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeTitleTextView: TextView = view.findViewById(R.id.recipeTitleTextView)
        val recipeIngredientsTextView: TextView = view.findViewById(R.id.recipeIngredientsTextView)
        val recipeDescriptionTextView: TextView = view.findViewById(R.id.recipeDescriptionTextView)

        val recipe = args.recipe

        recipeTitleTextView.text = recipe.title
        recipeIngredientsTextView.text = recipe.ingredients
        recipeDescriptionTextView.text = recipe.description
    }
}