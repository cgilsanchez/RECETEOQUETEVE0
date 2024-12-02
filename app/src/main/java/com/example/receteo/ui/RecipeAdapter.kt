package com.example.receteo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.R

class RecipeAdapter(
    private val recipes: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.titleTextView.text = recipe
        holder.itemView.setOnClickListener { onClick(recipe) }
    }

    override fun getItemCount(): Int = recipes.size

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.text_recipe_title)
    }
}
