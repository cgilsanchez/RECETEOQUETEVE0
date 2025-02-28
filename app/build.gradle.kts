plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // Para Room y Hilt
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.receteo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.receteo"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "MAPS_API_KEY", "\"${properties["MAPS_API_KEY"] ?: ""}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"



    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true// Habilita Data Binding
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }


}

dependencies {
    implementation(libs.core.ktx.v1120)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.v1100)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx.v273)
    implementation(libs.androidx.navigation.ui.ktx.v273)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.room.runtime.v260)
    kapt(libs.androidx.room.compiler.v260)
    implementation(libs.androidx.room.ktx.v260)
    implementation(libs.androidx.lifecycle.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.play.services.location)
    implementation(libs.places.v410)
    implementation(libs.transportation.consumer.v300)
    implementation(libs.androidx.tools.core)
    implementation(libs.play.services.maps)
    implementation("androidx.hilt:hilt-work:1.0.0")
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // Para usar Hilt con WorkManager
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    // Dependencias de notificaciones
    implementation("androidx.core:core-ktx:1.9.0")

    val camerax_version = "1.5.0-alpha06" // 📌 Según la documentación

// Core de CameraX
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")

// Manejo de ciclo de vida
    implementation("androidx.camera:camera-lifecycle:$camerax_version")

// Para mostrar la vista previa con PreviewView
    implementation("androidx.camera:camera-view:$camerax_version")

// Extensiones para filtros avanzados
    implementation("androidx.camera:camera-extensions:$camerax_version")

// Para grabación de video (opcional)
    implementation("androidx.camera:camera-video:$camerax_version")



    dependencies {
        // Glide para cargar imágenes
        implementation("com.github.bumptech.glide:glide:4.15.1")
        kapt("com.github.bumptech.glide:compiler:4.15.1")
    }

    // 🟢 Añadir OkHttp (si falta)
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // 🟢 Retrofit con soporte para Multipart y RequestBody
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
}

kapt {
    correctErrorTypes = true
}
secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}