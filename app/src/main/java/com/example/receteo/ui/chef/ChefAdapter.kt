package com.example.receteo.ui.chef

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.data.remote.models.ChefModel
import com.example.receteo.databinding.ItemChefBinding

class ChefAdapter(
    private val chefs: MutableList<ChefModel>,
    private val onEditClick: (ChefModel) -> Unit,
    private val onDeleteClick: (ChefModel) -> Unit
) : RecyclerView.Adapter<ChefAdapter.ChefViewHolder>() {

    inner class ChefViewHolder(private val binding: ItemChefBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chef: ChefModel) {
            binding.tvChefName.text = chef.name

            binding.btnEditChef.setOnClickListener { onEditClick(chef) }
            binding.btnDeleteChef.setOnClickListener { onDeleteClick(chef) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChefViewHolder {
        val binding = ItemChefBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChefViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChefViewHolder, position: Int) {
        holder.bind(chefs[position])
    }

    override fun getItemCount(): Int = chefs.size

    fun updateData(newChefs: List<ChefModel>) {
        chefs.clear()
        chefs.addAll(newChefs)
        notifyDataSetChanged()
    }
}
