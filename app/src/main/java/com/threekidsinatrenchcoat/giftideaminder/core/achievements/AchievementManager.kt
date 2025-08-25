package com.threekidsinatrenchcoat.giftideaminder.core.achievements

import android.content.Context
import android.content.SharedPreferences
import com.threekidsinatrenchcoat.giftideaminder.data.repository.PersonRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class AchievementType {
    FIRST_RECIPIENT,
    FIVE_RECIPIENTS,
    TEN_RECIPIENTS,
    FIRST_20Q_SESSION,
    TEN_INTEREST_DETAILS,
    FIRST_GIFT_IDEA
}

data class Achievement(
    val type: AchievementType,
    val title: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val unlocksBenefit: String? = null
)

@Singleton
class AchievementManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val personRepository: PersonRepository
) {
    
    private val sharedPrefs: SharedPreferences = 
        context.getSharedPreferences("achievements", Context.MODE_PRIVATE)
    
    companion object {
        private const val FIRST_RECIPIENT_KEY = "first_recipient"
        private const val FIRST_RECIPIENT_TIME_KEY = "first_recipient_time"
        private const val FIVE_RECIPIENTS_KEY = "five_recipients"
        private const val FIVE_RECIPIENTS_TIME_KEY = "five_recipients_time"
        private const val TEN_RECIPIENTS_KEY = "ten_recipients"
        private const val TEN_RECIPIENTS_TIME_KEY = "ten_recipients_time"
        private const val FIRST_20Q_KEY = "first_20q"
        private const val FIRST_20Q_TIME_KEY = "first_20q_time"
        private const val TEN_DETAILS_KEY = "ten_details"
        private const val TEN_DETAILS_TIME_KEY = "ten_details_time"
        private const val FIRST_GIFT_KEY = "first_gift"
        private const val FIRST_GIFT_TIME_KEY = "first_gift_time"
    }
    
    // Get all achievements with their current status
    suspend fun getAllAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                type = AchievementType.FIRST_RECIPIENT,
                title = "Getting Started",
                description = "Add your first recipient",
                emoji = "🏁",
                isUnlocked = sharedPrefs.getBoolean(FIRST_RECIPIENT_KEY, false),
                unlockedAt = sharedPrefs.getLong(FIRST_RECIPIENT_TIME_KEY, 0L).takeIf { it > 0 }
            ),
            Achievement(
                type = AchievementType.FIVE_RECIPIENTS,
                title = "Growing Circle",
                description = "Add 5 recipients",
                emoji = "👥",
                isUnlocked = sharedPrefs.getBoolean(FIVE_RECIPIENTS_KEY, false),
                unlockedAt = sharedPrefs.getLong(FIVE_RECIPIENTS_TIME_KEY, 0L).takeIf { it > 0 },
                unlocksBenefit = "Unlock 1 free AI suggestion per recipient"
            ),
            Achievement(
                type = AchievementType.TEN_RECIPIENTS,
                title = "Social Butterfly",
                description = "Add 10 recipients",
                emoji = "🦋",
                isUnlocked = sharedPrefs.getBoolean(TEN_RECIPIENTS_KEY, false),
                unlockedAt = sharedPrefs.getLong(TEN_RECIPIENTS_TIME_KEY, 0L).takeIf { it > 0 },
                unlocksBenefit = "Unlock premium features"
            ),
            Achievement(
                type = AchievementType.FIRST_20Q_SESSION,
                title = "Discovery Mode",
                description = "Complete your first 20 Questions session",
                emoji = "❓",
                isUnlocked = sharedPrefs.getBoolean(FIRST_20Q_KEY, false),
                unlockedAt = sharedPrefs.getLong(FIRST_20Q_TIME_KEY, 0L).takeIf { it > 0 }
            ),
            Achievement(
                type = AchievementType.TEN_INTEREST_DETAILS,
                title = "Detail Oriented",
                description = "Add 10 interest details",
                emoji = "🔍",
                isUnlocked = sharedPrefs.getBoolean(TEN_DETAILS_KEY, false),
                unlockedAt = sharedPrefs.getLong(TEN_DETAILS_TIME_KEY, 0L).takeIf { it > 0 }
            ),
            Achievement(
                type = AchievementType.FIRST_GIFT_IDEA,
                title = "Thoughtful Giver",
                description = "Add your first gift idea",
                emoji = "💡",
                isUnlocked = sharedPrefs.getBoolean(FIRST_GIFT_KEY, false),
                unlockedAt = sharedPrefs.getLong(FIRST_GIFT_TIME_KEY, 0L).takeIf { it > 0 }
            )
        )
    }
    
    // Check and unlock achievements based on current data
    suspend fun checkAndUnlockAchievements(): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()
        val currentTime = System.currentTimeMillis()
        
        // Count recipients
        val recipientCount = personRepository.getAllPersons().first().size
        
        // Check first recipient
        if (!sharedPrefs.getBoolean(FIRST_RECIPIENT_KEY, false) && recipientCount >= 1) {
            unlockAchievement(AchievementType.FIRST_RECIPIENT, currentTime)
            newlyUnlocked.add(getAllAchievements().find { it.type == AchievementType.FIRST_RECIPIENT }!!)
        }
        
        // Check five recipients
        if (!sharedPrefs.getBoolean(FIVE_RECIPIENTS_KEY, false) && recipientCount >= 5) {
            unlockAchievement(AchievementType.FIVE_RECIPIENTS, currentTime)
            newlyUnlocked.add(getAllAchievements().find { it.type == AchievementType.FIVE_RECIPIENTS }!!)
        }
        
        // Check ten recipients
        if (!sharedPrefs.getBoolean(TEN_RECIPIENTS_KEY, false) && recipientCount >= 10) {
            unlockAchievement(AchievementType.TEN_RECIPIENTS, currentTime)
            newlyUnlocked.add(getAllAchievements().find { it.type == AchievementType.TEN_RECIPIENTS }!!)
        }
        
        return newlyUnlocked
    }
    
    // Manually unlock specific achievements
    suspend fun unlockAchievement(type: AchievementType, timestamp: Long = System.currentTimeMillis()): Boolean {
        val (isUnlockedKey, timeKey) = when (type) {
            AchievementType.FIRST_RECIPIENT -> FIRST_RECIPIENT_KEY to FIRST_RECIPIENT_TIME_KEY
            AchievementType.FIVE_RECIPIENTS -> FIVE_RECIPIENTS_KEY to FIVE_RECIPIENTS_TIME_KEY
            AchievementType.TEN_RECIPIENTS -> TEN_RECIPIENTS_KEY to TEN_RECIPIENTS_TIME_KEY
            AchievementType.FIRST_20Q_SESSION -> FIRST_20Q_KEY to FIRST_20Q_TIME_KEY
            AchievementType.TEN_INTEREST_DETAILS -> TEN_DETAILS_KEY to TEN_DETAILS_TIME_KEY
            AchievementType.FIRST_GIFT_IDEA -> FIRST_GIFT_KEY to FIRST_GIFT_TIME_KEY
        }
        
        val alreadyUnlocked = sharedPrefs.getBoolean(isUnlockedKey, false)
        
        if (!alreadyUnlocked) {
            sharedPrefs.edit()
                .putBoolean(isUnlockedKey, true)
                .putLong(timeKey, timestamp)
                .apply()
            return true
        }
        
        return false
    }
    
    // Check if 5 recipients unlock is available
    suspend fun isFiveRecipientUnlockAvailable(): Boolean {
        return sharedPrefs.getBoolean(FIVE_RECIPIENTS_KEY, false)
    }
    
    // Get available AI suggestions count
    suspend fun getAvailableAISuggestions(): Int {
        val isFiveRecipientUnlocked = isFiveRecipientUnlockAvailable()
        val recipientCount = personRepository.getAllPersons().first().size
        
        return if (isFiveRecipientUnlocked) {
            recipientCount // 1 suggestion per recipient after 5 recipients
        } else {
            0
        }
    }
    
    // Use one AI suggestion
    suspend fun useAISuggestion(): Boolean {
        return getAvailableAISuggestions() > 0
    }
    
    // Get achievements as observable flow
    fun getAchievementsFlow(): Flow<List<Achievement>> {
        return personRepository.getAllPersons().map { persons ->
            val recipientCount = persons.size
            
            listOf(
                Achievement(
                    type = AchievementType.FIRST_RECIPIENT,
                    title = "Getting Started",
                    description = "Add your first recipient",
                    emoji = "🏁",
                    isUnlocked = sharedPrefs.getBoolean(FIRST_RECIPIENT_KEY, false),
                    unlockedAt = sharedPrefs.getLong(FIRST_RECIPIENT_TIME_KEY, 0L).takeIf { it > 0 }
                ),
                Achievement(
                    type = AchievementType.FIVE_RECIPIENTS,
                    title = "Growing Circle",
                    description = "Add 5 recipients",
                    emoji = "👥",
                    isUnlocked = sharedPrefs.getBoolean(FIVE_RECIPIENTS_KEY, false),
                    unlockedAt = sharedPrefs.getLong(FIVE_RECIPIENTS_TIME_KEY, 0L).takeIf { it > 0 },
                    unlocksBenefit = "Unlock 1 free AI suggestion per recipient"
                ),
                Achievement(
                    type = AchievementType.TEN_RECIPIENTS,
                    title = "Social Butterfly",
                    description = "Add 10 recipients",
                    emoji = "🦋",
                    isUnlocked = sharedPrefs.getBoolean(TEN_RECIPIENTS_KEY, false),
                    unlockedAt = sharedPrefs.getLong(TEN_RECIPIENTS_TIME_KEY, 0L).takeIf { it > 0 },
                    unlocksBenefit = "Unlock premium features"
                ),
                Achievement(
                    type = AchievementType.FIRST_20Q_SESSION,
                    title = "Discovery Mode",
                    description = "Complete your first 20 Questions session",
                    emoji = "❓",
                    isUnlocked = sharedPrefs.getBoolean(FIRST_20Q_KEY, false),
                    unlockedAt = sharedPrefs.getLong(FIRST_20Q_TIME_KEY, 0L).takeIf { it > 0 }
                ),
                Achievement(
                    type = AchievementType.TEN_INTEREST_DETAILS,
                    title = "Detail Oriented",
                    description = "Add 10 interest details",
                    emoji = "🔍",
                    isUnlocked = sharedPrefs.getBoolean(TEN_DETAILS_KEY, false),
                    unlockedAt = sharedPrefs.getLong(TEN_DETAILS_TIME_KEY, 0L).takeIf { it > 0 }
                ),
                Achievement(
                    type = AchievementType.FIRST_GIFT_IDEA,
                    title = "Thoughtful Giver",
                    description = "Add your first gift idea",
                    emoji = "💡",
                    isUnlocked = sharedPrefs.getBoolean(FIRST_GIFT_KEY, false),
                    unlockedAt = sharedPrefs.getLong(FIRST_GIFT_TIME_KEY, 0L).takeIf { it > 0 }
                )
            )
        }
    }
}