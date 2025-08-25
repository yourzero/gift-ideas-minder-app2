package com.threekidsinatrenchcoat.giftideaminder.di

import android.content.Context
// import com.threekidsinatrenchcoat.giftideaminder.core.achievements.AchievementManager
import com.threekidsinatrenchcoat.giftideaminder.core.analytics.Analytics
import com.threekidsinatrenchcoat.giftideaminder.core.flags.FeatureFlags
import com.threekidsinatrenchcoat.giftideaminder.data.repository.PersonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AchievementModule {
    
    // AchievementManager will be uncommented when trophies branch is merged
    // @Provides
    // @Singleton
    // fun provideAchievementManager(
    //     @ApplicationContext context: Context,
    //     personRepository: PersonRepository
    // ): AchievementManager {
    //     return AchievementManager(context, personRepository)
    // }
    
    @Provides
    @Singleton
    fun provideAnalytics(): Analytics {
        return Analytics()
    }
    
    @Provides
    @Singleton
    fun provideFeatureFlags(
        @ApplicationContext context: Context
    ): FeatureFlags {
        return FeatureFlags(context)
    }
}