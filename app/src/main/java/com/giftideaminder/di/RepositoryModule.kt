package com.giftideaminder.di

import com.giftideaminder.data.dao.GiftDao
import com.giftideaminder.data.dao.PersonDao
import com.giftideaminder.data.repository.GiftRepository
import com.giftideaminder.data.repository.PersonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGiftRepository(giftDao: GiftDao): GiftRepository = GiftRepository(giftDao)

    @Provides
    @Singleton
    fun providePersonRepository(personDao: PersonDao): PersonRepository = PersonRepository(personDao)
} 