package com.giftideaminder.di

import android.content.Context
import androidx.room.Room
import com.giftideaminder.data.dao.GiftDao
import com.giftideaminder.data.dao.PersonDao
import com.giftideaminder.data.model.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gift_database"
        ).build()
    }

    @Provides
    fun provideGiftDao(db: AppDatabase): GiftDao = db.giftDao()

    @Provides
    fun providePersonDao(db: AppDatabase): PersonDao = db.personDao()
} 