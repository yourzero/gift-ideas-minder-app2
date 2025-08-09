package com.threekidsinatrenchcoat.giftideaminder.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.threekidsinatrenchcoat.giftideaminder.BuildConfig
import com.threekidsinatrenchcoat.giftideaminder.data.dao.GiftDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.PersonDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.ImportantDateDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.RelationshipTypeDao
import com.threekidsinatrenchcoat.giftideaminder.data.model.AppDatabase
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
        val builder = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gift_database"
        )

        if (BuildConfig.DEBUG) {
            builder
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                context,
                                "⚠️ Destructive DB migration occurred",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
        }

        return builder.build()
    }

    @Provides
    fun provideGiftDao(db: AppDatabase): GiftDao = db.giftDao()

    @Provides
    fun providePersonDao(db: AppDatabase): PersonDao = db.personDao()

    @Provides
    fun provideImportantDateDao(db: AppDatabase): ImportantDateDao = db.importantDateDao()

    @Provides
    fun provideRelationshipTypeDao(db: AppDatabase): RelationshipTypeDao = db.relationshipTypeDao()
}
