package com.example.receteo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receteo.R

class ChefListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chef_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_chefs)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val chefs = listOf("Chef 1", "Chef 2", "Chef 3")
        val adapter = ChefAdapter(chefs) { chefName ->
            // Navegar al detalle del chef si se implementa
        }
        recyclerView.adapter = adapter

        view.findViewById<View>(R.id.buttonAddChef).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_chefListFragment_to_chefCreateFragment)
        }
    }
}