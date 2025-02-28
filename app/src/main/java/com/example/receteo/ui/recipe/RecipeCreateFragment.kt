package com.example.receteo.ui.recipe



import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.receteo.R
import com.example.receteo.data.remote.models.RecipeRequestModel
import com.example.receteo.data.remote.models.ChefModel
import com.example.receteo.data.remote.models.RecipeDataRequest
import com.example.receteo.data.remote.models.RecipeModel
import com.example.receteo.data.repository.RecipeRepository
import com.example.receteo.databinding.FragmentRecipeCreateBinding
import com.example.receteo.ui.chef.ChefViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class RecipeCreateFragment : Fragment() {


    @Inject
    lateinit var recipeRepository: RecipeRepository
    private lateinit var binding: FragmentRecipeCreateBinding
    private val viewModel: RecipeViewModel by viewModels()
    private val chefViewModel: ChefViewModel by viewModels()
    private var recipeId: Int? = null
    private var selectedChefId: Int? = null
    private var selectedImageFile: File? = null
    private var selectedImageUrl: String? = null
    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
        private const val GALLERY_REQUEST_CODE = 1002
    }


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

        chefViewModel.fetchChefs()
        chefViewModel.chefs.observe(viewLifecycleOwner) { chefs ->
            setupChefSpinner(chefs)
        }

        if (recipeId != null && recipeId != -1) {
            viewModel.getRecipeById(recipeId!!)
            viewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
                recipe?.let { loadRecipeData(it) }
            }
        }


        binding.btnSelectImage.setOnClickListener {
            val options = arrayOf("Tomar foto", "Elegir de la galería", "Cancelar")

            AlertDialog.Builder(requireContext())
                .setTitle("Selecciona una opción")
                .setItems(options) { dialog, which ->
                    when (which) {
                        0 -> checkCameraPermission()  // 📸 Tomar foto
                        1 -> openGallery()           // 🖼 Elegir de la galería
                        2 -> dialog.dismiss()        // ❌ Cancelar
                    }
                }
                .show()
        }

        binding.btnSaveRecipe.setOnClickListener { saveOrUpdateRecipe() }

    }

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
        selectedChefId = recipe.chef
        selectedImageUrl = recipe.imageUrl // ✅ Guarda la URL de la imagen

        chefViewModel.chefs.observe(viewLifecycleOwner) { chefs ->
            val position = chefs.indexOfFirst { it.id == recipe.chef }
            if (position != -1) {
                binding.spinnerChef.setSelection(position)
            }
        }

        if (!recipe.imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(recipe.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.image_error)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 🔥 Evita imágenes antiguas en caché
                .skipMemoryCache(true) // 🔥 Carga la imagen más reciente de Strapi
                .into(binding.ivRecipeImage)

        }
    }


    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openCamera() // ✅ Si ya tiene permisos, abrir la cámara
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Permiso requerido")
                    .setMessage("Se necesita acceso a la cámara para tomar fotos.")
                    .setPositiveButton("Aceptar") { _, _ ->
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            }
        }
    }

    // ✅ Manejar la respuesta del usuario al otorgar permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera() // 📸 Si acepta el permiso, abrir la cámara
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                binding.ivRecipeImage.setImageURI(it)
                selectedImageFile = uriToFile(it)
            }
        }
    }

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



    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            cameraLauncher.launch(takePictureIntent)
        } else {
            Toast.makeText(requireContext(), "No se puede abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadImage(file: File) {
        lifecycleScope.launch {
            val uploadedImageId = recipeRepository.uploadImage(file) // 🔥 Llamada correcta
            if (uploadedImageId != null) {
                Toast.makeText(requireContext(), "✅ Imagen subida con éxito", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "❌ Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }




    // ✅ Manejar la foto tomada correctamente
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                binding.ivRecipeImage.setImageBitmap(imageBitmap)
                selectedImageFile = saveBitmapToFile(imageBitmap)

                // 📤 Subir imagen automáticamente después de tomar la foto
                selectedImageFile?.let { uploadImage(it) }
            } else {
                Toast.makeText(requireContext(), "No se pudo capturar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }





    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val file = File(requireContext().cacheDir, "recipe_image.jpg")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
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

        val previousImage = viewModel.selectedRecipe.value?.imageUrl

        val imageList = if (selectedImageFile != null) {
            emptyList() // 🔥 Strapi manejará la nueva imagen cuando se suba
        } else if (!previousImage.isNullOrEmpty()) {
            listOf(previousImage) // 🔥 Mantener la imagen anterior si no se cambia
        } else {
            emptyList()
        }

        val recipeRequest = RecipeRequestModel(
            data = RecipeDataRequest(
                name = name,
                descriptions = descriptions,
                ingredients = ingredients,
                chef = selectedChefId!!,
                image = imageList,
                isFavorite = isFavorite
            )
        )

        // Mostrar un mensaje de carga
        val loadingToast = Toast.makeText(requireContext(), "Guardando receta...", Toast.LENGTH_SHORT)
        loadingToast.show()

        if (recipeId == null || recipeId == -1) {
            viewModel.createRecipe(recipeRequest, selectedImageFile)
        } else {
            viewModel.updateRecipe(recipeRequest, recipeId!!, selectedImageFile)
        }

        // Observar el resultado y navegar solo después de completar la operación
        viewModel.successMessage.observe(viewLifecycleOwner) { successMessage ->
            loadingToast.cancel()
            Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_recipeCreateFragment_to_recipeListFragment2)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            loadingToast.cancel()
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
