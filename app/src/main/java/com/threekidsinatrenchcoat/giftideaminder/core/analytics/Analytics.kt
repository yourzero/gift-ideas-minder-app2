package com.threekidsinatrenchcoat.giftideaminder.core.analytics

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Analytics facade for tracking user interactions.
 * Provides a consistent interface for logging events across the app.
 */
@Singleton
class Analytics @Inject constructor() {
    
    fun logEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        // TODO: Implement with actual analytics provider (Firebase, etc.)
        // For now, just log to console in debug builds
        if (BuildConfig.DEBUG) {
            println("Analytics Event: $eventName, Parameters: $parameters")
        }
    }
    
    // Interest-related events
    fun logInterestAdded(parentLabel: String, isDetail: Boolean) {
        logEvent("interest_added", mapOf(
            "parent_label" to parentLabel,
            "is_detail" to isDetail
        ))
    }
    
    fun logInterestToggleOwned(isOwned: Boolean) {
        logEvent("interest_toggle_owned", mapOf("is_owned" to isOwned))
    }
    
    fun logInterestToggleDislike(isDislike: Boolean) {
        logEvent("interest_toggle_dislike", mapOf("is_dislike" to isDislike))
    }
    
    // 20 Questions events
    fun logTwentyQuestionsStarted(category: String) {
        logEvent("twenty_questions_started", mapOf("category" to category))
    }
    
    fun logTwentyQuestionsCompleted(category: String, questionsAnswered: Int) {
        logEvent("twenty_questions_completed", mapOf(
            "category" to category,
            "questions_answered" to questionsAnswered
        ))
    }
    
    fun logTwentyQuestionsSkipped(category: String, questionIndex: Int) {
        logEvent("twenty_questions_skipped", mapOf(
            "category" to category,
            "question_index" to questionIndex
        ))
    }
    
    // Achievement events
    fun logAchievementUnlocked(achievementType: String) {
        logEvent("achievement_unlocked", mapOf("achievement_type" to achievementType))
    }
    
    fun logAchievementChecked() {
        logEvent("achievement_checked")
    }
    
    fun logAISuggestionUsed(remainingCount: Int) {
        logEvent("ai_suggestion_used", mapOf("remaining_count" to remainingCount))
    }
    
    // Person events
    fun logPersonAdded(hasRelationship: Boolean, hasImportantDates: Boolean) {
        logEvent("person_added", mapOf(
            "has_relationship" to hasRelationship,
            "has_important_dates" to hasImportantDates
        ))
    }
    
    fun logPersonDetailsViewed(personId: Long) {
        logEvent("person_details_viewed", mapOf("person_id" to personId))
    }
    
    // Navigation events
    fun logScreenViewed(screenName: String) {
        logEvent("screen_viewed", mapOf("screen_name" to screenName))
    }
    
    fun logFeatureUsed(featureName: String) {
        logEvent("feature_used", mapOf("feature_name" to featureName))
    }
}

// Build configuration stub - normally would come from BuildConfig
private object BuildConfig {
    const val DEBUG = true
}