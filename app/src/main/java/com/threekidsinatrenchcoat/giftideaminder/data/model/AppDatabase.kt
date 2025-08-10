package com.threekidsinatrenchcoat.giftideaminder.data.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.threekidsinatrenchcoat.giftideaminder.data.dao.GiftDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.ImportantDateDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.PersonDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.PriceRecordDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.RelationshipTypeDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.SettingsDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.SuggestionDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.SuggestionDismissalDao
import com.threekidsinatrenchcoat.giftideaminder.data.model.SuggestionDismissal

@Database(
    entities = [Gift::class, Person::class, PriceRecord::class, Suggestion::class, Settings::class, RelationshipType::class, ImportantDate::class, SuggestionDismissal::class],
    version = 5,
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

    // For development: no explicit migrations. Use fallbackToDestructiveMigration in builder.
}