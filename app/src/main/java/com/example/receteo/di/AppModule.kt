package com.example.receteo.di

import android.content.Context
import com.example.receteo.data.local.RecipeDao
import com.example.receteo.data.remote.ChefApi
import com.example.receteo.data.remote.RecipeApi
import com.example.receteo.data.repository.ChefRepository
import com.example.receteo.data.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecipeRepository(
        recipeApi: RecipeApi,
        recipeDao: RecipeDao,
        @ApplicationContext context: Context
    ): RecipeRepository {
        return RecipeRepository(recipeApi, recipeDao, context)
    }


    @Provides
    @Singleton
    fun provideChefRepository(chefApi: ChefApi): ChefRepository {
        return ChefRepository(chefApi)
    }
}
