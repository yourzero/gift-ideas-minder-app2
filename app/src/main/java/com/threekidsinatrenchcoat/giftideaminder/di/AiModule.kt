package com.threekidsinatrenchcoat.giftideaminder.di

import com.threekidsinatrenchcoat.giftideaminder.core.ai.InterestAiQuestioner
import com.threekidsinatrenchcoat.giftideaminder.core.ai.InterestAiSuggester
import com.threekidsinatrenchcoat.giftideaminder.core.analytics.Analytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    
    @Provides
    @Singleton
    fun provideInterestAiSuggester(
        analytics: Analytics
    ): InterestAiSuggester {
        return InterestAiSuggester(analytics)
    }
    
    @Provides
    @Singleton
    fun provideInterestAiQuestioner(
        analytics: Analytics
    ): InterestAiQuestioner {
        return InterestAiQuestioner(analytics)
    }
}