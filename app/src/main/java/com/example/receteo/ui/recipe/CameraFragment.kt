package com.example.receteo.ui.recipe

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.receteo.data.repository.RecipeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment() {

    @Inject
    lateinit var recipeRepository: RecipeRepository

    private var imageUri: Uri? = null
    private var imageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelable<Uri>("imageUri")?.let { uri ->
            imageUri = uri
            imageFile = uriToFile(uri)
            uploadImageToServer()
        }
    }

    private fun uploadImageToServer() {
        val fileToUpload = imageFile ?: imageUri?.let { uriToFile(it) }

        if (fileToUpload == null) {
            Toast.makeText(requireContext(), "No se pudo obtener la imagen", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val uploadedImageId = recipeRepository.uploadImage(fileToUpload)

            if (uploadedImageId != null) {
                Toast.makeText(requireContext(), "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val file = File(requireContext().cacheDir, "selected_image.jpg")

        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file
        } catch (e: Exception) {
            Log.e("CameraFragment", " Error convirtiendo URI a File: ${e.message}")
            null
        }
    }
}
