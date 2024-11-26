package com.example.receteo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.receteo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Establece el layout principal
        setContentView(R.layout.activity_main)

        // Manejo robusto para evitar errores
        initializeNavigation()
    }

    private fun initializeNavigation() {
        try {
            // Inicializa el NavHostFragment
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    as? NavHostFragment ?: throw IllegalStateException("NavHostFragment no encontrado")
            navController = navHostFragment.navController

            // Configura el BottomNavigationView
            val bottomNavigationView: BottomNavigationView =
                findViewById(R.id.bottom_navigation)
            bottomNavigationView.setupWithNavController(navController)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MainActivity", "Error durante la inicialización: ${e.message}")
        }
    }
}



