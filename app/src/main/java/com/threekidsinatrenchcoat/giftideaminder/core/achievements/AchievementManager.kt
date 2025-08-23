package com.threekidsinatrenchcoat.giftideaminder.core.achievements

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.threekidsinatrenchcoat.giftideaminder.core.repository.InterestRepository
import com.threekidsinatrenchcoat.giftideaminder.core.repository.PersonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data class representing an achievement in the app
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val unlockedAt: Long? = null
)

/**
 * Sealed class for achievement-related events
 */
sealed class AchievementEvent {
    data class Unlocked(val achievement: Achievement) : AchievementEvent()
    data class FeatureUnlocked(val feature: String, val description: String) : AchievementEvent()
}

/**
 * Manager for tracking user achievements and unlocking features
 * 
 * Handles milestone tracking, persistent storage via DataStore,
 * and emits events when achievements are unlocked.
 */
@Singleton
class AchievementManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val personRepository: PersonRepository,
    private val interestRepository: InterestRepository
) {
    
    // DataStore keys for achievement timestamps
    private companion object {
        val FIRST_RECIPIENT_KEY = longPreferencesKey("achievement_first_recipient")
        val FIVE_RECIPIENTS_KEY = longPreferencesKey("achievement_five_recipients")
        val TEN_DETAILS_KEY = longPreferencesKey("achievement_ten_details")
        val FIRST_TWENTY_Q_KEY = longPreferencesKey("achievement_first_twenty_q")
        val GIFT_GIVER_KEY = longPreferencesKey("achievement_gift_giver")
        val ORGANIZED_KEY = longPreferencesKey("achievement_organized")
    }
    
    // Available achievements
    private val achievements = listOf(
        Achievement(
            id = "FIRST_RECIPIENT",
            title = "First Friend",
            description = "Add your first recipient"
        ),
        Achievement(
            id = "FIVE_RECIPIENTS", 
            title = "Social Circle",
            description = "Add 5 recipients"
        ),
        Achievement(
            id = "TEN_DETAILS",
            title = "Detail Oriented", 
            description = "Add 10 interest details total"
        ),
        Achievement(
            id = "FIRST_TWENTY_Q",
            title = "Curious Mind",
            description = "Complete first 20Q session"
        ),
        Achievement(
            id = "GIFT_GIVER",
            title = "Generous Heart",
            description = "Give your first gift"
        ),
        Achievement(
            id = "ORGANIZED",
            title = "Party Planner",
            description = "Add 3 recipients with birthdays"
        )
    )
    
    private val _achievementEvents = MutableSharedFlow<AchievementEvent>()
    val achievementEvents: Flow<AchievementEvent> = _achievementEvents.asSharedFlow()
    
    /**
     * Check all achievement conditions and emit events for newly unlocked achievements
     */
    suspend fun checkAchievements() {
        val personCount = personRepository.getAllPersons().first().size
        val totalInterestDetails = interestRepository.getAllInterests().first().sumOf { it.details.size }
        val personsWithBirthdays = personRepository.getAllPersons().first().count { it.birthday != null }
        
        // Check each achievement
        checkAndUnlockAchievement(
            "FIRST_RECIPIENT",
            FIRST_RECIPIENT_KEY,
            personCount >= 1
        )
        
        checkAndUnlockAchievement(
            "FIVE_RECIPIENTS",
            FIVE_RECIPIENTS_KEY,
            personCount >= 5
        )
        
        checkAndUnlockAchievement(
            "TEN_DETAILS",
            TEN_DETAILS_KEY,
            totalInterestDetails >= 10
        )
        
        checkAndUnlockAchievement(
            "ORGANIZED",
            ORGANIZED_KEY,
            personsWithBirthdays >= 3
        )
        
        // Check feature unlocks after checking achievements
        checkFeatureUnlocks(personCount)
    }
    
    /**
     * Check if a specific achievement is unlocked
     */
    suspend fun isAchievementUnlocked(id: String): Boolean {
        val key = getKeyForAchievement(id) ?: return false
        return dataStore.data.map { preferences ->
            preferences[key] != null
        }.first()
    }
    
    /**
     * Get all unlocked achievements with their unlock timestamps
     */
    suspend fun getUnlockedAchievements(): List<Achievement> {
        val preferences = dataStore.data.first()
        return achievements.mapNotNull { achievement ->
            val key = getKeyForAchievement(achievement.id)
            val unlockedAt = key?.let { preferences[it] }
            if (unlockedAt != null) {
                achievement.copy(unlockedAt = unlockedAt)
            } else null
        }
    }
    
    /**
     * Check if a specific feature is unlocked based on achievements
     */
    suspend fun isFeatureUnlocked(feature: String): Boolean {
        return when (feature) {
            "ai_suggestions" -> isAchievementUnlocked("FIVE_RECIPIENTS")
            else -> false
        }
    }
    
    /**
     * Mark an achievement as unlocked (used for achievements that can't be automatically detected)
     */
    suspend fun unlockAchievement(achievementId: String) {
        val key = getKeyForAchievement(achievementId) ?: return
        val isAlreadyUnlocked = isAchievementUnlocked(achievementId)
        
        if (!isAlreadyUnlocked) {
            val timestamp = Instant.now().epochSecond
            dataStore.edit { preferences ->
                preferences[key] = timestamp
            }
            
            val achievement = achievements.find { it.id == achievementId }
            if (achievement != null) {
                _achievementEvents.emit(
                    AchievementEvent.Unlocked(achievement.copy(unlockedAt = timestamp))
                )
            }
        }
    }
    
    private suspend fun checkAndUnlockAchievement(
        achievementId: String,
        key: Preferences.Key<Long>,
        condition: Boolean
    ) {
        if (condition && !isAchievementUnlocked(achievementId)) {
            val timestamp = Instant.now().epochSecond
            dataStore.edit { preferences ->
                preferences[key] = timestamp
            }
            
            val achievement = achievements.find { it.id == achievementId }
            if (achievement != null) {
                _achievementEvents.emit(
                    AchievementEvent.Unlocked(achievement.copy(unlockedAt = timestamp))
                )
            }
        }
    }
    
    private suspend fun checkFeatureUnlocks(personCount: Int) {
        if (personCount >= 5 && !isFeatureUnlocked("ai_suggestions")) {
            // Feature is now unlocked, emit event
            _achievementEvents.emit(
                AchievementEvent.FeatureUnlocked(
                    feature = "ai_suggestions",
                    description = "You've unlocked 1 free AI suggestion per recipient!"
                )
            )
        }
    }
    
    private fun getKeyForAchievement(achievementId: String): Preferences.Key<Long>? {
        return when (achievementId) {
            "FIRST_RECIPIENT" -> FIRST_RECIPIENT_KEY
            "FIVE_RECIPIENTS" -> FIVE_RECIPIENTS_KEY
            "TEN_DETAILS" -> TEN_DETAILS_KEY
            "FIRST_TWENTY_Q" -> FIRST_TWENTY_Q_KEY
            "GIFT_GIVER" -> GIFT_GIVER_KEY
            "ORGANIZED" -> ORGANIZED_KEY
            else -> null
        }
    }
}