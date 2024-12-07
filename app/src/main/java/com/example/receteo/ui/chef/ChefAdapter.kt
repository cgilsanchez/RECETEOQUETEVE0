package com.example.receteo.ui.chef

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.R

class ChefAdapter(
    private val chefs: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ChefAdapter.ChefViewHolder>() {

    inner class ChefViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chefName: TextView = view.findViewById(R.id.text_chef_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChefViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chef, parent, false)
        return ChefViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChefViewHolder, position: Int) {
        val chef = chefs[position]
        holder.chefName.text = chef
        holder.itemView.setOnClickListener { onClick(chef) }
    }

    override fun getItemCount(): Int = chefs.size
}