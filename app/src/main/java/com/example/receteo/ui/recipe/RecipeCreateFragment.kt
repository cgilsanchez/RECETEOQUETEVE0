package com.example.receteo.ui.recipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.receteo.data.remote.models.RecipeRequestModel
import com.example.receteo.data.remote.models.ChefModel
import com.example.receteo.data.remote.models.RecipeDataRequest
import com.example.receteo.data.remote.models.RecipeModel
import com.example.receteo.databinding.FragmentRecipeCreateBinding
import com.example.receteo.ui.chef.ChefViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@AndroidEntryPoint
class RecipeCreateFragment : Fragment() {

    private lateinit var binding: FragmentRecipeCreateBinding
    private val viewModel: RecipeViewModel by viewModels()
    private val chefViewModel: ChefViewModel by viewModels() // ViewModel para obtener chefs
    private var recipeId: Int? = null
    private var selectedChefId: Int? = null
    private var selectedImageFile: File? = null

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

        // 🟢 Cargar la lista de chefs para el Spinner
        chefViewModel.fetchChefs()
        chefViewModel.chefs.observe(viewLifecycleOwner) { chefs ->
            setupChefSpinner(chefs)
        }

        // 🟢 Si se está editando, cargar la receta existente
        if (recipeId != null && recipeId != -1) {
            viewModel.getRecipeById(recipeId!!) // Asegurar que se obtienen los datos de la receta
            viewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
                recipe?.let {
                    binding.etRecipeName.setText(it.name)
                    binding.etDescription.setText(it.descriptions)
                    binding.etIngredients.setText(it.ingredients)

                    // 🟢 Seleccionar el chef en el Spinner
                    selectedChefId = it.chef
                    chefViewModel.chefs.observe(viewLifecycleOwner) { chefs ->
                        val chefPosition = chefs.indexOfFirst { chef -> chef.id == it.chef }
                        if (chefPosition != -1) {
                            binding.spinnerChef.setSelection(chefPosition)
                        }
                    }

                    // 🟢 Mostrar imagen con Glide
                    if (!it.imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(it.imageUrl)
                            .into(binding.ivRecipeImage)
                    }
                }
            }
        }

        // 🟢 Botón para seleccionar imagen
        binding.btnSelectImage.setOnClickListener { openGallery() }

        binding.btnSaveRecipe.setOnClickListener { saveOrUpdateRecipe() }
    }


    // 🟢 Configura el Spinner con la lista de chefs
    private fun setupChefSpinner(chefs: List<ChefModel>) {
        val chefNames = chefs.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, chefNames)
        binding.spinnerChef.adapter = adapter

        binding.spinnerChef.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedChefId = chefs[position].id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedChefId = null
            }
        }
    }

    private fun loadRecipeData(recipe: RecipeModel) {
        binding.etRecipeName.setText(recipe.name)
        binding.etDescription.setText(recipe.descriptions)
        binding.etIngredients.setText(recipe.ingredients)

        // 🔹 Seleccionar el chef correcto en el Spinner
        chefViewModel.chefs.observe(viewLifecycleOwner) { chefs ->
            val position = chefs.indexOfFirst { it.id == recipe.chef }
            if (position != -1) {
                binding.spinnerChef.setSelection(position)
            }
        }

        // 🔹 Cargar la imagen si la receta tiene una
        if (!recipe.imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(recipe.imageUrl)
                .into(binding.ivRecipeImage)
        }
    }



    // 🟢 Abre la galería para seleccionar una imagen
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    // 🟢 Maneja la imagen seleccionada
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                binding.ivRecipeImage.setImageURI(it)
                selectedImageFile = uriToFile(it)
            }
        }
    }

    // 🟢 Convierte la URI en un archivo para enviarlo a la API
    private fun uriToFile(uri: Uri): File? {
        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "recipe_image.jpg")
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun saveOrUpdateRecipe() {
        val name = binding.etRecipeName.text.toString().trim()
        val descriptions = binding.etDescription.text.toString().trim()
        val ingredients = binding.etIngredients.text.toString().trim()

        if (name.isEmpty() || descriptions.isEmpty() || ingredients.isEmpty() || selectedChefId == null) {
            Toast.makeText(requireContext(), "❌ Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val isFavorite = recipeId?.let { id ->
            viewModel.selectedRecipe.value?.isFavorite ?: false
        } ?: false

        val recipeRequest = RecipeRequestModel(
            data = RecipeDataRequest(
                name = name,
                descriptions = descriptions,
                ingredients = ingredients,
                chef = selectedChefId!!,
                image = emptyList(), // ✅ Se asignará después de subir la imagen
                isFavorite = isFavorite
            )
        )

        if (recipeId == null || recipeId == -1) {
            viewModel.createRecipe(recipeRequest, selectedImageFile)
        } else {
            viewModel.updateRecipe(recipeRequest, recipeId!!, selectedImageFile)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        viewModel.fetchRecipes()
        findNavController().popBackStack()
    }


}
