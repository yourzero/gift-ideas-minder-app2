package com.threekidsinatrenchcoat.giftideaminder.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PriceHistoryConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromPriceHistoryList(value: List<Pair<String, Double>>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPriceHistoryList(value: String?): List<Pair<String, Double>>? {
        val listType = object : TypeToken<List<Pair<String, Double>>>() {}.type
        return gson.fromJson(value, listType)
    }
} 