package com.threekidsinatrenchcoat.giftideaminder.di

import com.threekidsinatrenchcoat.giftideaminder.data.api.AIService
import com.threekidsinatrenchcoat.giftideaminder.data.dao.GiftDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.PersonDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.ImportantDateDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.InterestDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.RelationshipTypeDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.SuggestionDismissalDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.SettingsDao
import com.threekidsinatrenchcoat.giftideaminder.data.repository.GiftRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.PersonRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.ImportantDateRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.RelationshipTypeRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.AISuggestionRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.ContactsRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.SettingsRepository
import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideAISuggestionRepository(
        aiService: AIService,
        giftDao: GiftDao,
        personDao: PersonDao,
        importantDateDao: ImportantDateDao,
        interestDao: InterestDao,
        dismissalDao: SuggestionDismissalDao
    ): AISuggestionRepository = AISuggestionRepository(aiService, giftDao, personDao, importantDateDao, interestDao, dismissalDao)

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsDao: SettingsDao): SettingsRepository = SettingsRepository(settingsDao)
    
    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver = context.contentResolver
    
    @Provides
    @Singleton
    fun provideContactsRepository(contentResolver: ContentResolver): ContactsRepository = ContactsRepository(contentResolver)
}