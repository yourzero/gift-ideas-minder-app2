//package com.threekidsinatrenchcoat.giftideaminder.data.model
//
//import androidx.room.TypeConverter
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//
//class Converters {
//    private val gson = Gson()
//
//    @TypeConverter
//    fun fromStringList(value: List<String>?): String? = value?.let { gson.toJson(it) }
//
//    @TypeConverter
//    fun toStringList(value: String?): List<String>? = value?.let {
//        val listType = object : TypeToken<List<String>>() {}.type
//        gson.fromJson<List<String>>(it, listType)
//    }
//}
