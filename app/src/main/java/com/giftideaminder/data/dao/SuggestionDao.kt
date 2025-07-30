package com.giftideaminder.data.dao

import androidx.room.*
import com.giftideaminder.data.model.Suggestion
import kotlinx.coroutines.flow.Flow

@Dao
interface SuggestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(suggestion: Suggestion)

    @Query("SELECT * FROM suggestions WHERE giftId = :giftId ORDER BY createdAt DESC")
    fun getSuggestionsForGift(giftId: Int): Flow<List<Suggestion>>
}
