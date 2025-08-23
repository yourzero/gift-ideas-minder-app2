package com.threekidsinatrenchcoat.giftideaminder.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.threekidsinatrenchcoat.giftideaminder.core.achievements.AchievementManager
import com.threekidsinatrenchcoat.giftideaminder.data.repository.InterestRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.PersonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// DataStore delegate extension
private val Context.achievementDataStore: DataStore<Preferences> by preferencesDataStore(name = "achievements")

@Module
@InstallIn(SingletonComponent::class)
object AchievementModule {

    @Provides
    @Singleton
    fun provideAchievementDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.achievementDataStore
    }

    @Provides
    @Singleton
    fun provideAchievementManager(
        dataStore: DataStore<Preferences>,
        personRepository: PersonRepository,
        interestRepository: InterestRepository
    ): AchievementManager = AchievementManager(dataStore, personRepository, interestRepository)
}