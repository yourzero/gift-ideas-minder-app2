package com.giftideaminder.di

import com.giftideaminder.data.api.PriceApi
import com.giftideaminder.data.api.PriceService
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
} 