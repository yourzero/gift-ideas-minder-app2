package com.giftideaminder.di

import com.giftideaminder.data.api.PriceApi
import com.giftideaminder.data.api.PriceService
import com.giftideaminder.data.api.AIApi
import com.giftideaminder.data.api.AIService
import com.giftideaminder.data.api.NetworkClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providePriceService(): PriceService {
        return PriceApi.service
    }

    @Provides
    @Singleton
    fun provideAIService(): AIService {
        // In a real app, fetch from secure config. For now, empty apiKey means no auth header.
        val retrofit = NetworkClient.createRetrofit(apiKey = "")
        return retrofit.create(AIService::class.java)
    }
} 