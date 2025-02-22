package com.example.receteo.ui.recipe

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.data.remote.models.RecipeModel
import com.example.receteo.databinding.ItemRecipeBinding
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class RecipeAdapter(
    private val recipes: MutableList<RecipeModel>,
    private val onEditClick: (RecipeModel) -> Unit,
    private val onDeleteClick: (RecipeModel) -> Unit,
    private val onFavoriteClick: (RecipeModel) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: RecipeModel) {
            binding.tvRecipeName.text = recipe.name
            binding.tvRecipeDescription.text = recipe.descriptions ?: "Sin descripción"

            val imageUrl = recipe.imageUrl ?: ""
            if (imageUrl.isNotEmpty()) {
                LoadImageTask(binding.ivRecipeImage).execute(imageUrl)
            } else {
                binding.ivRecipeImage.setImageResource(android.R.color.darker_gray)
            }

            binding.btnEdit.setOnClickListener { onEditClick(recipe) }

            binding.btnDelete.setOnClickListener {
                Log.d("RecipeAdapter", "Botón eliminar presionado para receta: ${recipe.name}")
                onDeleteClick(recipe)
            }








        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    fun updateData(newRecipes: List<RecipeModel>) {
        recipes.clear()
        recipes.addAll(newRecipes)
        notifyDataSetChanged()
    }

    private class LoadImageTask(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg params: String): Bitmap? {
            val url = params[0]
            return try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                imageView.setImageBitmap(result)
            } else {
                imageView.setImageResource(android.R.color.darker_gray)
            }
        }
    }
}
