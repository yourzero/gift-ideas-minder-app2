package com.giftideaminder.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.giftideaminder.data.model.Gift
import kotlinx.coroutines.flow.Flow

@Dao
interface GiftDao {
    @Query("SELECT * FROM gifts ORDER BY eventDate ASC")
    fun getAllGifts(): Flow<List<Gift>>

    @Insert
    suspend fun insert(gift: Gift)

    @Update
    suspend fun update(gift: Gift)

    @Delete
    suspend fun delete(gift: Gift)

    @Query("SELECT * FROM gifts WHERE id = :id")
    fun getGiftById(id: Int): Flow<Gift>
} 