package com.example.receteo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.R

class RecipeAdapter(
    private val recipes: List<String>,
    private val onClick: (String) -> Unit,
    private val onFavoriteClick: (String) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private val favoriteRecipes = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.titleTextView.text = recipe

        // Cambiar el ícono del botón dependiendo del estado de favorito
        val isFavorite = favoriteRecipes.contains(recipe)
        holder.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        )

        holder.itemView.setOnClickListener { onClick(recipe) }

        holder.favoriteButton.setOnClickListener {
            if (isFavorite) {
                favoriteRecipes.remove(recipe)
            } else {
                favoriteRecipes.add(recipe)
            }
            onFavoriteClick(recipe)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = recipes.size

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.text_recipe_title)
        val favoriteButton: ImageButton = view.findViewById(R.id.button_favorite)
    }
}

