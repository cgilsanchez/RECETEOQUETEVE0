package com.example.receteo.ui.chef

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.receteo.databinding.FragmentChefCreateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChefCreateFragment : Fragment() {

    private lateinit var binding: FragmentChefCreateBinding
    private val viewModel: ChefViewModel by viewModels()
    private var chefId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChefCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chefId = arguments?.getInt("chefId", -1)

        if (chefId != null && chefId != -1) {
            viewModel.fetchChefs()
            viewModel.chefs.observe(viewLifecycleOwner) { chefs ->
                val chef = chefs.find { it.id == chefId }
                chef?.let {
                    binding.etChefName.setText(it.name)
                }
            }
        }

        binding.btnSaveChef.setOnClickListener { saveOrUpdateChef() }
    }

    private fun saveOrUpdateChef() {
        val name = binding.etChefName.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre del chef es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        if (chefId == null || chefId == -1) {
            viewModel.createChef(name)
        } else {
            viewModel.updateChef(chefId!!, name)
        }

        findNavController().popBackStack()
    }
}
