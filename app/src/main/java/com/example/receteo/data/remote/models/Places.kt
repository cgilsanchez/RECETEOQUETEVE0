package com.example.receteo.data.remote.models

data class PlacesResponse(
    val results: List<PlaceResult>
)

data class PlaceResult(
    val name: String,
    val vicinity: String,
    val geometry: Geometry?
)

data class Geometry(
    val location: LocationData
)

data class LocationData(
    val lat: Double,
    val lng: Double
)