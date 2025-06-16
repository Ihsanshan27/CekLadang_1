package com.dicoding.cekladang.di

import android.content.Context
import com.dicoding.cekladang.data.remote.retrofit.ApiConfig
import com.dicoding.cekladang.repository.ArticleRepository
import com.dicoding.cekladang.repository.HistoryRepository

object Injection {
    fun provideHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepository.getInstance(context)
    }

    fun provideRepository(): ArticleRepository {
        val apiService = ApiConfig.getApiConfig()

        return ArticleRepository(apiService)
    }
}