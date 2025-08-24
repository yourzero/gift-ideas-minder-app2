package com.threekidsinatrenchcoat.giftideaminder.data.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.threekidsinatrenchcoat.giftideaminder.data.dao.GiftDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.ImportantDateDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.InterestDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.PersonDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.PriceRecordDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.RelationshipTypeDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.SettingsDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.SuggestionDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.SuggestionDismissalDao
import com.threekidsinatrenchcoat.giftideaminder.data.model.SuggestionDismissal

@Database(
    entities = [Gift::class, Person::class, PriceRecord::class, Suggestion::class, Settings::class, RelationshipType::class, ImportantDate::class, SuggestionDismissal::class, Interest::class, InterestEntity::class],
    version = 4,  // Incremented version for InterestEntity table
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun giftDao(): GiftDao
    abstract fun personDao(): PersonDao
    abstract fun priceRecordDao(): PriceRecordDao
    abstract fun suggestionDao(): SuggestionDao
    abstract fun suggestionDismissalDao(): SuggestionDismissalDao
    abstract fun settingsDao(): SettingsDao
    abstract fun relationshipTypeDao(): RelationshipTypeDao
    abstract fun importantDateDao(): ImportantDateDao
    abstract fun interestDao(): InterestDao

    // For development: no explicit migrations. Use fallbackToDestructiveMigration in builder.
    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add imageUrl column to gifts table
                database.execSQL("ALTER TABLE gifts ADD COLUMN imageUrl TEXT")
            }
        }
        
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add InterestEntity table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `interest_entities` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `personId` INTEGER NOT NULL,
                        `parentId` INTEGER,
                        `label` TEXT NOT NULL,
                        `isDislike` INTEGER NOT NULL DEFAULT 0,
                        `isOwned` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL,
                        FOREIGN KEY(`personId`) REFERENCES `persons`(`id`) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_interest_entities_personId` ON `interest_entities` (`personId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_interest_entities_parentId` ON `interest_entities` (`parentId`)")
            }
        }
    }
}