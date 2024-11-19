package com.example.receteo.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeproject.R
import com.example.recipeproject.data.db.RecipeEntity

class RecipeAdapter(
    private val recipes: List<RecipeEntity>,
    private val onFavoriteClick: (RecipeEntity) -> Unit,
    private val onRecipeClick: (RecipeEntity) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe, onFavoriteClick, onRecipeClick)
    }

    override fun getItemCount() = recipes.size

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.recipeTitleTextView)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)

        fun bind(
            recipe: RecipeEntity,
            onFavoriteClick: (RecipeEntity) -> Unit,
            onRecipeClick: (RecipeEntity) -> Unit
        ) {
            titleTextView.text = recipe.title
            favoriteButton.setImageResource(
                if (recipe.isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
            )

            favoriteButton.setOnClickListener { onFavoriteClick(recipe) }
            itemView.setOnClickListener { onRecipeClick(recipe) }
        }
    }
}
