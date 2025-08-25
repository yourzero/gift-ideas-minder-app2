package com.threekidsinatrenchcoat.giftideaminder.core.flags

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Feature flags system for enabling/disabling features.
 * Allows for controlled rollout and A/B testing of new features.
 */
@Singleton
class FeatureFlags @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val sharedPrefs: SharedPreferences = 
        context.getSharedPreferences("feature_flags", Context.MODE_PRIVATE)
    
    companion object {
        private const val TWENTY_QUESTIONS_ENABLED = "twenty_questions_enabled"
        private const val TROPHIES_ENABLED = "trophies_enabled"
        private const val INTEREST_DRILL_DOWN_ENABLED = "interest_drill_down_enabled"
        private const val DISINTERESTS_ENABLED = "disinterests_enabled"
        private const val AI_SUGGESTIONS_ENABLED = "ai_suggestions_enabled"
        
        // Default values for features
        private val DEFAULT_FLAGS = mapOf(
            TWENTY_QUESTIONS_ENABLED to true,
            TROPHIES_ENABLED to true,
            INTEREST_DRILL_DOWN_ENABLED to true,
            DISINTERESTS_ENABLED to true,
            AI_SUGGESTIONS_ENABLED to true
        )
    }
    
    // Core feature flags
    val isTwentyQuestionsEnabled: Boolean
        get() = getFlag(TWENTY_QUESTIONS_ENABLED)
    
    val isTrophiesEnabled: Boolean
        get() = getFlag(TROPHIES_ENABLED)
    
    val isInterestDrillDownEnabled: Boolean
        get() = getFlag(INTEREST_DRILL_DOWN_ENABLED)
    
    val isDisinterestsEnabled: Boolean
        get() = getFlag(DISINTERESTS_ENABLED)
    
    val isAISuggestionsEnabled: Boolean
        get() = getFlag(AI_SUGGESTIONS_ENABLED)
    
    private fun getFlag(key: String): Boolean {
        val defaultValue = DEFAULT_FLAGS[key] ?: false
        return sharedPrefs.getBoolean(key, defaultValue)
    }
    
    fun setFlag(key: String, enabled: Boolean) {
        sharedPrefs.edit()
            .putBoolean(key, enabled)
            .apply()
    }
    
    // Convenience methods for setting specific flags
    fun setTwentyQuestionsEnabled(enabled: Boolean) {
        setFlag(TWENTY_QUESTIONS_ENABLED, enabled)
    }
    
    fun setTrophiesEnabled(enabled: Boolean) {
        setFlag(TROPHIES_ENABLED, enabled)
    }
    
    fun setInterestDrillDownEnabled(enabled: Boolean) {
        setFlag(INTEREST_DRILL_DOWN_ENABLED, enabled)
    }
    
    fun setDisinterestsEnabled(enabled: Boolean) {
        setFlag(DISINTERESTS_ENABLED, enabled)
    }
    
    fun setAISuggestionsEnabled(enabled: Boolean) {
        setFlag(AI_SUGGESTIONS_ENABLED, enabled)
    }
    
    // Get all flags as a map for debugging/admin purposes
    fun getAllFlags(): Map<String, Boolean> {
        return mapOf(
            "Twenty Questions" to isTwentyQuestionsEnabled,
            "Trophies & Achievements" to isTrophiesEnabled,
            "Interest Drill-Down" to isInterestDrillDownEnabled,
            "Disinterests/Hard No's" to isDisinterestsEnabled,
            "AI Suggestions" to isAISuggestionsEnabled
        )
    }
    
    // Reset all flags to defaults
    fun resetToDefaults() {
        DEFAULT_FLAGS.forEach { (key, defaultValue) ->
            setFlag(key, defaultValue)
        }
    }
}