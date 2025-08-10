package com.giftideaminder.data.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.giftideaminder.data.dao.GiftDao
import com.giftideaminder.data.dao.PersonDao
import com.giftideaminder.data.dao.PriceRecordDao
import com.giftideaminder.data.dao.SuggestionDao
import com.giftideaminder.data.dao.SuggestionDismissalDao
import com.giftideaminder.data.dao.SettingsDao
import com.giftideaminder.data.dao.ImportantDateDao
import com.giftideaminder.data.dao.RelationshipTypeDao

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
