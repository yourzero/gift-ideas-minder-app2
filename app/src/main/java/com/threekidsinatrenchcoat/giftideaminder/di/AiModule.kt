package com.threekidsinatrenchcoat.giftideaminder.di

import com.threekidsinatrenchcoat.giftideaminder.core.ai.InterestAiQuestioner
import com.threekidsinatrenchcoat.giftideaminder.core.ai.InterestAiQuestionerImpl
import com.threekidsinatrenchcoat.giftideaminder.core.ai.InterestAiSuggester
import com.threekidsinatrenchcoat.giftideaminder.core.ai.InterestAiSuggesterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindInterestAiSuggester(
        interestAiSuggesterImpl: InterestAiSuggesterImpl
    ): InterestAiSuggester

    @Binds
    @Singleton
    abstract fun bindInterestAiQuestioner(
        interestAiQuestionerImpl: InterestAiQuestionerImpl
    ): InterestAiQuestioner
}