package com.example.receteo.di

import com.example.receteo.data.remote.RecipeApi
import com.example.receteo.data.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecipeRepository(recipeApi: RecipeApi): RecipeRepository {
        return RecipeRepository(recipeApi)
    }
}
