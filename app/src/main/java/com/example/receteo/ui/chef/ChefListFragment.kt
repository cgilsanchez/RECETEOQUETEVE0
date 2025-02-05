package com.example.receteo.ui.chef

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.receteo.R
import com.example.receteo.data.remote.models.ChefModel
import com.example.receteo.databinding.FragmentChefListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChefListFragment : Fragment() {

    private lateinit var binding: FragmentChefListBinding
    private val viewModel: ChefViewModel by viewModels()
    private lateinit var adapter: ChefAdapter
    private val chefList = mutableListOf<ChefModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChefListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeChefs()
        viewModel.fetchChefs()

        binding.fabAddChef.setOnClickListener {
            findNavController().navigate(R.id.chefCreateFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = ChefAdapter(
            chefList,
            onEditClick = { chef ->
                val bundle = Bundle().apply {
                    putInt("chefId", chef.id)
                }
                findNavController().navigate(R.id.chefCreateFragment, bundle)
            },
            onDeleteClick = { chef ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar chef")
                    .setMessage("¿Estás seguro de que deseas eliminar este chef?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        viewModel.deleteChef(chef.id)
                        Toast.makeText(requireContext(), "Chef eliminado", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ChefListFragment.adapter
        }
    }

    private fun observeChefs() {
        viewModel.chefs.observe(viewLifecycleOwner) { chefs ->
            adapter.updateData(chefs)
        }
    }
}
