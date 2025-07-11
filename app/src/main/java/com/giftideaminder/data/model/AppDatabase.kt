package com.giftideaminder.data.model

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.giftideaminder.data.dao.GiftDao
import com.giftideaminder.data.dao.PersonDao
import android.content.Context

@Database(entities = [Gift::class, Person::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun giftDao(): GiftDao
    abstract fun personDao(): PersonDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gift_database"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE gifts ADD COLUMN reminderOffset INTEGER NOT NULL DEFAULT 7")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE gifts ADD COLUMN currentPrice REAL")
                database.execSQL("ALTER TABLE gifts ADD COLUMN budget REAL")
                database.execSQL("ALTER TABLE gifts ADD COLUMN isPurchased INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
} 