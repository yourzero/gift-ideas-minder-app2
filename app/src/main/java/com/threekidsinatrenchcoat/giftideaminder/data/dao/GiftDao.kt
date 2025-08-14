package com.threekidsinatrenchcoat.giftideaminder.data.dao

import androidx.room.*
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import com.threekidsinatrenchcoat.giftideaminder.data.model.GiftWithHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface GiftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gift: Gift)

    @Update
    suspend fun update(gift: Gift)

    @Delete
    suspend fun delete(gift: Gift)

    @Query("SELECT * FROM gifts")
    fun getAllGifts(): Flow<List<Gift>>

    @Query("SELECT * FROM gifts WHERE id = :id")
    fun getGiftById(id: Int): Flow<Gift>

    @Transaction
    @Query("SELECT * FROM gifts WHERE id = :id")
    fun getGiftWithHistoryById(id: Int): Flow<GiftWithHistory>
}
