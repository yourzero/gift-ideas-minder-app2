package com.giftideaminder.data.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.giftideaminder.data.dao.GiftDao
import com.giftideaminder.data.dao.PersonDao
import com.giftideaminder.data.dao.PriceRecordDao
import com.giftideaminder.data.dao.SuggestionDao
import com.giftideaminder.data.dao.SettingsDao

@Database(
    entities = [Gift::class, Person::class, PriceRecord::class, Suggestion::class, Settings::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun giftDao(): GiftDao
    abstract fun personDao(): PersonDao
    abstract fun priceRecordDao(): PriceRecordDao
    abstract fun suggestionDao(): SuggestionDao
    abstract fun settingsDao(): SettingsDao
}
