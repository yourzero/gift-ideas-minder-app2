package com.threekidsinatrenchcoat.giftideaminder.data.repository

import com.threekidsinatrenchcoat.giftideaminder.data.dao.SettingsDao
import com.threekidsinatrenchcoat.giftideaminder.data.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao
) {
    
    suspend fun setBooleanSetting(key: String, value: Boolean) {
        settingsDao.insert(Settings(key = key, value = value.toString()))
    }
    
    suspend fun setStringSetting(key: String, value: String) {
        settingsDao.insert(Settings(key = key, value = value))
    }
    
    fun getBooleanSetting(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        return settingsDao.getSetting(key).map { settings ->
            settings?.value?.toBooleanStrictOrNull() ?: defaultValue
        }
    }
    
    fun getStringSetting(key: String, defaultValue: String = ""): Flow<String> {
        return settingsDao.getSetting(key).map { settings ->
            settings?.value ?: defaultValue
        }
    }
    
    companion object {
        const val KEY_DEBUG_AI_PROMPTS = "debug_ai_prompts"
        const val KEY_PLAY_LOADING_SOUNDS = "play_loading_sounds"
        // KEY_INTERESTS_ADVANCED_MODE removed - simplified UX with always-available tabs
    }
}