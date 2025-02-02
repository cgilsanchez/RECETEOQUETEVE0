package com.example.receteo.ui.recipe  // 🔥 Asegura que el package sea correcto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.receteo.R
import com.example.receteo.data.remote.models.RecipeModel
import com.example.receteo.databinding.ItemRecipeBinding

class RecipeAdapter(
    private val recipes: MutableList<RecipeModel>,
    private val onEditClick: (RecipeModel) -> Unit,
    private val onDeleteClick: (RecipeModel) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {


    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: RecipeModel) {
            binding.tvRecipeName.text = recipe.name
            binding.tvRecipeDescription.text = recipe.descriptions

            // 🔥 Verifica que recipe.imageUrl contiene la URL correcta
            Glide.with(binding.root.context)
                .load(recipe.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.recipeImage)

            binding.btnEdit.setOnClickListener { onEditClick(recipe) }
            binding.btnDelete.setOnClickListener {
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
        notifyDataSetChanged()  // 🔥 IMPORTANTE: Notificar cambios a la UI
    }

}
