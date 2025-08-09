package com.threekidsinatrenchcoat.giftideaminder.di

import com.threekidsinatrenchcoat.giftideaminder.data.dao.GiftDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.PersonDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.ImportantDateDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.RelationshipTypeDao
import com.threekidsinatrenchcoat.giftideaminder.data.repository.GiftRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.PersonRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.ImportantDateRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.RelationshipTypeRepository
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