package com.threekidsinatrenchcoat.giftideaminder.core.flags

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Feature flag constants for controlling app features
 */
object FeatureFlag {
    const val TWENTY_QUESTIONS_ENABLED = "twenty_questions_enabled"
    const val TROPHIES_ENABLED = "trophies_enabled"
    const val AI_SUGGESTIONS_ENABLED = "ai_suggestions_enabled"
    const val INTEREST_DRILL_DOWN_ENABLED = "interest_drill_down_enabled"
    const val DEBUG_MODE_ENABLED = "debug_mode_enabled"
}

/**
 * Manages feature flags with persistent storage using DataStore.
 * Provides runtime control over feature availability throughout the app.
 */
@Singleton
class FeatureFlags @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    
    companion object {
        // DataStore preference keys
        private val TWENTY_QUESTIONS_KEY = booleanPreferencesKey(FeatureFlag.TWENTY_QUESTIONS_ENABLED)
        private val TROPHIES_KEY = booleanPreferencesKey(FeatureFlag.TROPHIES_ENABLED)
        private val AI_SUGGESTIONS_KEY = booleanPreferencesKey(FeatureFlag.AI_SUGGESTIONS_ENABLED)
        private val INTEREST_DRILL_DOWN_KEY = booleanPreferencesKey(FeatureFlag.INTEREST_DRILL_DOWN_ENABLED)
        private val DEBUG_MODE_KEY = booleanPreferencesKey(FeatureFlag.DEBUG_MODE_ENABLED)
        
        // Default values for feature flags
        private val DEFAULT_VALUES = mapOf(
            FeatureFlag.TWENTY_QUESTIONS_ENABLED to true,
            FeatureFlag.TROPHIES_ENABLED to true,
            FeatureFlag.AI_SUGGESTIONS_ENABLED to true,
            FeatureFlag.INTEREST_DRILL_DOWN_ENABLED to true,
            FeatureFlag.DEBUG_MODE_ENABLED to false
        )
    }

    /**
     * Get the enabled state of a feature flag as a Flow
     */
    fun isEnabled(flag: String): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            val key = getPreferenceKey(flag)
            val defaultValue = DEFAULT_VALUES[flag] ?: false
            preferences[key] ?: defaultValue
        }
    }

    /**
     * Get the enabled state of a feature flag as a suspend function
     */
    suspend fun isEnabledSuspend(flag: String): Boolean {
        return isEnabled(flag).first()
    }

    /**
     * Set the enabled state of a feature flag
     */
    suspend fun setEnabled(flag: String, enabled: Boolean) {
        val key = getPreferenceKey(flag)
        dataStore.edit { preferences ->
            preferences[key] = enabled
        }
    }

    /**
     * Get all feature flags and their current states
     */
    fun getAllFlags(): Flow<Map<String, Boolean>> {
        return dataStore.data.map { preferences ->
            mapOf(
                FeatureFlag.TWENTY_QUESTIONS_ENABLED to (preferences[TWENTY_QUESTIONS_KEY] ?: DEFAULT_VALUES[FeatureFlag.TWENTY_QUESTIONS_ENABLED]!!),
                FeatureFlag.TROPHIES_ENABLED to (preferences[TROPHIES_KEY] ?: DEFAULT_VALUES[FeatureFlag.TROPHIES_ENABLED]!!),
                FeatureFlag.AI_SUGGESTIONS_ENABLED to (preferences[AI_SUGGESTIONS_KEY] ?: DEFAULT_VALUES[FeatureFlag.AI_SUGGESTIONS_ENABLED]!!),
                FeatureFlag.INTEREST_DRILL_DOWN_ENABLED to (preferences[INTEREST_DRILL_DOWN_KEY] ?: DEFAULT_VALUES[FeatureFlag.INTEREST_DRILL_DOWN_ENABLED]!!),
                FeatureFlag.DEBUG_MODE_ENABLED to (preferences[DEBUG_MODE_KEY] ?: DEFAULT_VALUES[FeatureFlag.DEBUG_MODE_ENABLED]!!)
            )
        }
    }

    /**
     * Reset all feature flags to their default values
     */
    suspend fun resetToDefaults() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Enable debug mode (convenience method)
     */
    suspend fun enableDebugMode() {
        setEnabled(FeatureFlag.DEBUG_MODE_ENABLED, true)
    }

    /**
     * Disable debug mode (convenience method)
     */
    suspend fun disableDebugMode() {
        setEnabled(FeatureFlag.DEBUG_MODE_ENABLED, false)
    }

    /**
     * Get the appropriate preference key for a feature flag
     */
    private fun getPreferenceKey(flag: String): Preferences.Key<Boolean> {
        return when (flag) {
            FeatureFlag.TWENTY_QUESTIONS_ENABLED -> TWENTY_QUESTIONS_KEY
            FeatureFlag.TROPHIES_ENABLED -> TROPHIES_KEY
            FeatureFlag.AI_SUGGESTIONS_ENABLED -> AI_SUGGESTIONS_KEY
            FeatureFlag.INTEREST_DRILL_DOWN_ENABLED -> INTEREST_DRILL_DOWN_KEY
            FeatureFlag.DEBUG_MODE_ENABLED -> DEBUG_MODE_KEY
            else -> throw IllegalArgumentException("Unknown feature flag: $flag")
        }
    }
}

/**
 * Extension functions for easier feature flag checking
 */
suspend fun FeatureFlags.isTwentyQuestionsEnabled(): Boolean = 
    isEnabledSuspend(FeatureFlag.TWENTY_QUESTIONS_ENABLED)

suspend fun FeatureFlags.isTrophiesEnabled(): Boolean = 
    isEnabledSuspend(FeatureFlag.TROPHIES_ENABLED)

suspend fun FeatureFlags.isAiSuggestionsEnabled(): Boolean = 
    isEnabledSuspend(FeatureFlag.AI_SUGGESTIONS_ENABLED)

suspend fun FeatureFlags.isInterestDrillDownEnabled(): Boolean = 
    isEnabledSuspend(FeatureFlag.INTEREST_DRILL_DOWN_ENABLED)

suspend fun FeatureFlags.isDebugModeEnabled(): Boolean = 
    isEnabledSuspend(FeatureFlag.DEBUG_MODE_ENABLED)

/**
 * Flow extension functions for reactive feature flag checking
 */
fun FeatureFlags.twentyQuestionsEnabledFlow(): Flow<Boolean> = 
    isEnabled(FeatureFlag.TWENTY_QUESTIONS_ENABLED)

fun FeatureFlags.trophiesEnabledFlow(): Flow<Boolean> = 
    isEnabled(FeatureFlag.TROPHIES_ENABLED)

fun FeatureFlags.aiSuggestionsEnabledFlow(): Flow<Boolean> = 
    isEnabled(FeatureFlag.AI_SUGGESTIONS_ENABLED)

fun FeatureFlags.interestDrillDownEnabledFlow(): Flow<Boolean> = 
    isEnabled(FeatureFlag.INTEREST_DRILL_DOWN_ENABLED)

fun FeatureFlags.debugModeEnabledFlow(): Flow<Boolean> = 
    isEnabled(FeatureFlag.DEBUG_MODE_ENABLED)