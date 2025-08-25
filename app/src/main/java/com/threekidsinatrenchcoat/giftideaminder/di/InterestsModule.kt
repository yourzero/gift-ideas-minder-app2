package com.threekidsinatrenchcoat.giftideaminder.di

import com.threekidsinatrenchcoat.giftideaminder.data.dao.InterestDao
import com.threekidsinatrenchcoat.giftideaminder.data.repository.InterestRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InterestsModule {
    
    @Provides
    @Singleton
    fun provideInterestRepository(
        interestDao: InterestDao
    ): InterestRepository {
        return InterestRepository(interestDao)
    }
}