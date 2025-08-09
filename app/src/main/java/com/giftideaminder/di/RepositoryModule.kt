package com.giftideaminder.di

import com.giftideaminder.data.dao.GiftDao
import com.giftideaminder.data.dao.PersonDao
import com.giftideaminder.data.dao.ImportantDateDao
import com.giftideaminder.data.dao.RelationshipTypeDao
import com.giftideaminder.data.repository.GiftRepository
import com.giftideaminder.data.repository.PersonRepository
import com.giftideaminder.data.repository.ImportantDateRepository
import com.giftideaminder.data.repository.RelationshipTypeRepository
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

    @Provides
    @Singleton
    fun provideImportantDateRepository(dao: ImportantDateDao): ImportantDateRepository = ImportantDateRepository(dao)

    @Provides
    @Singleton
    fun provideRelationshipTypeRepository(dao: RelationshipTypeDao): RelationshipTypeRepository = RelationshipTypeRepository(dao)
} 