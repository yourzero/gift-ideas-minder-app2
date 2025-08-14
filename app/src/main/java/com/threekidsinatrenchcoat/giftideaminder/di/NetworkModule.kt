package com.threekidsinatrenchcoat.giftideaminder.di

import com.threekidsinatrenchcoat.giftideaminder.data.api.PriceApi
import com.threekidsinatrenchcoat.giftideaminder.data.api.PriceService
import com.threekidsinatrenchcoat.giftideaminder.data.api.AIService
import com.threekidsinatrenchcoat.giftideaminder.data.api.GeminiAIService
import com.threekidsinatrenchcoat.giftideaminder.BuildConfig
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
        val apiKey: String = BuildConfig.GEMINI_API_KEY
        return GeminiAIService(apiKey = apiKey)
    }
} 