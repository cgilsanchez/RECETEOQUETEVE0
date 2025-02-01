package com.example.receteo.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.receteo.databinding.FragmentRecipeCreateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeCreateFragment : Fragment() {

    private lateinit var binding: FragmentRecipeCreateBinding
    private val recipeViewModel: RecipeViewModel by viewModels()
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

        recipeId = arguments?.getInt("recipeId", -1) // Obtenemos el ID si existe

        if (recipeId != null && recipeId != -1) {
            recipeViewModel.getRecipeById(recipeId!!)
            recipeViewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
                recipe?.let {
                    binding.etRecipeName.setText(it.attributes.name)
                    binding.etDescription.setText(it.attributes.descriptions)
                }
            }
        }

        binding.btnSaveRecipe.setOnClickListener { saveOrUpdateRecipe() }
    }

    private fun saveOrUpdateRecipe() {
        val name = binding.etRecipeName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (recipeId == -1) {
            Toast.makeText(requireContext(), "Receta creada con éxito", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Receta actualizada con éxito", Toast.LENGTH_SHORT).show()
        }

        findNavController().popBackStack()
    }
}
