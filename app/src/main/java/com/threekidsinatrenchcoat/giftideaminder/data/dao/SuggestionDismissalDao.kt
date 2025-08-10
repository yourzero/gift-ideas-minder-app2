package com.giftideaminder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.giftideaminder.data.model.SuggestionDismissal
import kotlinx.coroutines.flow.Flow

@Dao
interface SuggestionDismissalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dismissal: SuggestionDismissal)

    @Query("SELECT suggestionKey FROM suggestion_dismissals")
    fun getAllDismissedKeys(): Flow<List<String>>
}

