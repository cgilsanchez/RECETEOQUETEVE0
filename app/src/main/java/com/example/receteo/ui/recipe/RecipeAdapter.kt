package com.example.receteo.ui.recipe

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.data.remote.models.RecipeData
import com.example.receteo.databinding.ItemRecipeBinding
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class RecipeAdapter(
    private val recipes: MutableList<RecipeData>,
    private val onEditClick: (RecipeData) -> Unit,
    private val onDeleteClick: (RecipeData) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: RecipeData) {
            binding.tvRecipeName.text = recipe.attributes.name
            binding.tvRecipeDescription.text = recipe.attributes.descriptions ?: "Sin descripción"

            val imageUrl = recipe.attributes.image?.data?.attributes?.url ?: ""
            if (imageUrl.isNotEmpty()) {
                LoadImageTask(binding.ivRecipeImage).execute(imageUrl)
            }

            binding.btnEdit.setOnClickListener { onEditClick(recipe) }
            binding.btnDelete.setOnClickListener { onDeleteClick(recipe) }
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

    fun updateData(newRecipes: List<RecipeData>) {
        recipes.clear()
        recipes.addAll(newRecipes)
        notifyDataSetChanged()
    }

    private class LoadImageTask(private val imageView: ImageView) :
        AsyncTask<String, Void, Bitmap?>() {

        override fun doInBackground(vararg params: String): Bitmap? {
            val url = params[0]
            return try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            result?.let {
                imageView.setImageBitmap(it)
            }
        }
    }
}
