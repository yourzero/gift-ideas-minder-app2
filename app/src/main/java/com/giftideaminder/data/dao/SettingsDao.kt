package com.giftideaminder.data.dao

import androidx.room.*
import com.giftideaminder.data.model.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: Settings)

    @Query("SELECT * FROM settings WHERE key = :key")
    fun getSetting(key: String): Flow<Settings?>

    @Query("SELECT * FROM settings")
    fun getAllSettings(): Flow<List<Settings>>
}
