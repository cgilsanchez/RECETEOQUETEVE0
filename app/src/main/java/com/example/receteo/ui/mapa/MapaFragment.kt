package com.example.receteo.ui.mapa

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.receteo.R
import com.example.receteo.data.remote.PlacesService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.provider.Settings
import android.util.Log
import com.example.receteo.databinding.FragmentMapaBinding
import com.example.receteo.BuildConfig


@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapaBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var googleMap: GoogleMap

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            getCurrentLocationAndSearchCinemas()
        } else {
            Toast.makeText(
                requireContext(),
                "Se requieren permisos de ubicación para encontrar cines cercanos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtiene la API Key de BuildConfig
        val apiKey = BuildConfig.MAPS_API_KEY
        Places.initialize(requireContext(), apiKey)


        if (apiKey.isNotEmpty()) {
            if (!Places.isInitialized()) {
                Places.initialize(requireContext(), apiKey)
            }
        }




        placesClient = Places.createClient(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        try {
            (placesClient as? java.io.Closeable)?.close()
        } catch (e: Exception) {
            Log.e("MapaFragment", "Error cerrando: ${e.message}")
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocationAndSearchCinemas()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Se necesitan permisos de ubicación")
                    .setMessage("Esta app necesita acceso a la ubicación")
                    .setPositiveButton("OK") { _, _ -> requestLocationPermissions() }
                    .create()
                    .show()
            }

            else -> requestLocationPermissions()
        }
    }

    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getCurrentLocationAndSearchCinemas() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        googleMap.isMyLocationEnabled = true

        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGpsEnabled) {
            showEnableLocationDialog()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                searchNearbyCinemas()
            } ?: run {
                showEnableLocationDialog()
            }
        }.addOnFailureListener {
            showEnableLocationDialog()
        }
    }

    private fun showEnableLocationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Ubicación desactivada")
            .setMessage("Necesitas activar la ubicación. ¿Deseas activarla?")
            .setPositiveButton("Activar") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(requireContext(), "No se activó la ubicación", Toast.LENGTH_SHORT).show()
            }
            .show()
    }


    private fun searchNearbyCinemas() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)

                val apiKey = BuildConfig.MAPS_API_KEY

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/maps/api/place/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(PlacesService::class.java)


                lifecycleScope.launch {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            service.getNearbyCinemas(
                                "${currentLatLng.latitude},${currentLatLng.longitude}",
                                10000,
                                "restaurant",
                                "restaurant|food",
                                apiKey
                            )
                        }

                        if (response.isSuccessful) {
                            val placesResponse = response.body()
                            val results = placesResponse?.results ?: emptyList()

                            if (results.isEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    "No se encontraron restaurantes cercanos",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@launch
                            }

                            val restaurantes = results.map { "${it.name} - ${it.vicinity}" }


                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                restaurantes
                            )



                            results.forEach { place ->
                                place.geometry?.location?.let {
                                    googleMap.addMarker(
                                        MarkerOptions()
                                            .position(LatLng(it.lat, it.lng))
                                            .title(place.name)
                                    )
                                }
                            }


                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error en la respuesta de la API",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Mapa cerrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "No se pudo obtener la ubicación actual",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}