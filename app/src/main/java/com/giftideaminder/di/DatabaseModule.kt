package com.giftideaminder.di

import android.content.Context
import android.widget.Toast
import androidx.room.Room
import androidx.room.RoomDatabase
import com.giftideaminder.data.dao.GiftDao
import com.giftideaminder.data.dao.PersonDao
import com.giftideaminder.data.model.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.giftideaminder.BuildConfig


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        val builder = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gift_database"
        )

        if (BuildConfig.DEBUG) {
            builder.fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onDestructiveMigration(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)
                        Toast.makeText(
                            context,
                            "⚠️ Destructive DB migration occurred",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }

        return builder.build()
    }

    @Provides
    fun provideGiftDao(db: AppDatabase): GiftDao = db.giftDao()

    @Provides
    fun providePersonDao(db: AppDatabase): PersonDao = db.personDao()
}