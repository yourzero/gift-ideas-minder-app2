package com.threekidsinatrenchcoat.giftideaminder.data.dao

import androidx.room.*
import com.threekidsinatrenchcoat.giftideaminder.data.model.PriceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: PriceRecord)

    @Query("SELECT * FROM price_records WHERE giftId = :giftId ORDER BY timestamp ASC")
    fun getRecordsForGift(giftId: Int): Flow<List<PriceRecord>>
}
