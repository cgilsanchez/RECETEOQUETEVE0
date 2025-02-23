package com.example.receteo.di

import android.content.Context
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
        @ApplicationContext context: Context  // 🔥 Inyectamos el Context
    ): RecipeRepository {
        return RecipeRepository(recipeApi, context)  // 🔥 Pasamos el Context
    }

    @Provides
    @Singleton
    fun provideChefRepository(chefApi: ChefApi): ChefRepository {
        return ChefRepository(chefApi)
    }
}
