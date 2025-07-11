package com.giftideaminder.data.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.giftideaminder.data.dao.GiftDao
import com.giftideaminder.data.dao.PersonDao

@Database(entities = [Gift::class, Person::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun giftDao(): GiftDao
    abstract fun personDao(): PersonDao
} 