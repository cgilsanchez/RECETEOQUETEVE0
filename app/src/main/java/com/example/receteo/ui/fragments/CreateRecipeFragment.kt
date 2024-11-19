package com.example.receteo.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.recipeproject.R
import com.example.recipeproject.data.db.RecipeEntity
import com.example.recipeproject.ui.viewmodels.MainViewModel

class CreateRecipeFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val titleEditText: EditText = view.findViewById(R.id.titleEditText)
        val ingredientsEditText: EditText = view.findViewById(R.id.ingredientsEditText)
        val descriptionEditText: EditText = view.findViewById(R.id.descriptionEditText)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val ingredients = ingredientsEditText.text.toString()
            val description = descriptionEditText.text.toString()

            if (title.isNotEmpty() && ingredients.isNotEmpty() && description.isNotEmpty()) {
                val newRecipe = RecipeEntity(
                    title = title,
                    ingredients = ingredients,
                    description = description
                )
                mainViewModel.addRecipe(newRecipe)
                // Regresar al fragmento principal o lista
            }
        }
    }
}