package com.threekidsinatrenchcoat.giftideaminder.core.analytics

import android.content.Context
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Analytics facade that provides a unified interface for tracking user interactions,
 * feature usage, and error events across the Gift Idea Minder app.
 */
interface Analytics {
    fun logEvent(event: String, properties: Map<String, Any> = emptyMap())
    fun setUserProperty(property: String, value: Any)
    fun setUserId(userId: String?)
}

/**
 * Analytics provider interface for implementing different analytics backends
 * (Firebase Analytics, custom analytics, etc.)
 */
interface AnalyticsProvider {
    fun initialize(context: Context)
    fun logEvent(event: String, properties: Map<String, Any>)
    fun setUserProperty(property: String, value: Any)
    fun setUserId(userId: String?)
}

/**
 * No-op analytics provider for development/testing environments
 */
class NoOpAnalyticsProvider : AnalyticsProvider {
    override fun initialize(context: Context) {
        // No-op
    }

    override fun logEvent(event: String, properties: Map<String, Any>) {
        // No-op
    }

    override fun setUserProperty(property: String, value: Any) {
        // No-op
    }

    override fun setUserId(userId: String?) {
        // No-op
    }
}

/**
 * Firebase Analytics provider stub for future integration
 */
class FirebaseAnalyticsProvider : AnalyticsProvider {
    
    override fun initialize(context: Context) {
        // TODO: Initialize Firebase Analytics
        // FirebaseAnalytics.getInstance(context)
    }

    override fun logEvent(event: String, properties: Map<String, Any>) {
        // TODO: Log event to Firebase Analytics
        // firebaseAnalytics.logEvent(event, Bundle().apply {
        //     properties.forEach { (key, value) ->
        //         when (value) {
        //             is String -> putString(key, value)
        //             is Int -> putInt(key, value)
        //             is Long -> putLong(key, value)
        //             is Double -> putDouble(key, value)
        //             is Boolean -> putBoolean(key, value)
        //             else -> putString(key, value.toString())
        //         }
        //     }
        // })
    }

    override fun setUserProperty(property: String, value: Any) {
        // TODO: Set user property in Firebase Analytics
        // firebaseAnalytics.setUserProperty(property, value.toString())
    }

    override fun setUserId(userId: String?) {
        // TODO: Set user ID in Firebase Analytics
        // firebaseAnalytics.setUserId(userId)
    }
}

/**
 * Main Analytics implementation that delegates to multiple providers
 */
@Singleton
class AnalyticsImpl @Inject constructor(
    private val providers: Set<AnalyticsProvider>,
    private val debugEnabled: Boolean = false
) : Analytics {

    private val tag = "Analytics"

    override fun logEvent(event: String, properties: Map<String, Any>) {
        if (debugEnabled) {
            Log.d(tag, "Event: $event, Properties: $properties")
        }
        
        providers.forEach { provider ->
            try {
                provider.logEvent(event, properties)
            } catch (e: Exception) {
                Log.e(tag, "Failed to log event '$event' with provider ${provider::class.simpleName}", e)
            }
        }
    }

    override fun setUserProperty(property: String, value: Any) {
        if (debugEnabled) {
            Log.d(tag, "User Property: $property = $value")
        }
        
        providers.forEach { provider ->
            try {
                provider.setUserProperty(property, value)
            } catch (e: Exception) {
                Log.e(tag, "Failed to set user property '$property' with provider ${provider::class.simpleName}", e)
            }
        }
    }

    override fun setUserId(userId: String?) {
        if (debugEnabled) {
            Log.d(tag, "User ID: $userId")
        }
        
        providers.forEach { provider ->
            try {
                provider.setUserId(userId)
            } catch (e: Exception) {
                Log.e(tag, "Failed to set user ID with provider ${provider::class.simpleName}", e)
            }
        }
    }
}

/**
 * Common event names used throughout the app
 */
object AnalyticsEvents {
    // User actions
    const val ADD_RECIPIENT = "add_recipient"
    const val ADD_GIFT = "add_gift" 
    const val ADD_INTEREST = "add_interest"
    const val EDIT_RECIPIENT = "edit_recipient"
    const val EDIT_GIFT = "edit_gift"
    const val DELETE_RECIPIENT = "delete_recipient"
    const val DELETE_GIFT = "delete_gift"
    
    // Feature usage
    const val START_TWENTY_QUESTIONS = "start_twenty_questions"
    const val COMPLETE_TWENTY_QUESTIONS = "complete_twenty_questions"
    const val UNLOCK_ACHIEVEMENT = "unlock_achievement"
    const val USE_AI_SUGGESTION = "use_ai_suggestion"
    const val DISMISS_AI_SUGGESTION = "dismiss_ai_suggestion"
    const val OCR_SCAN_GIFT = "ocr_scan_gift"
    const val PRICE_TRACKING_ENABLED = "price_tracking_enabled"
    const val IMPORT_CONTACTS = "import_contacts"
    const val EXPORT_DATA = "export_data"
    const val IMPORT_CSV = "import_csv"
    
    // Navigation
    const val SCREEN_VIEW = "screen_view"
    const val TAB_SWITCH = "tab_switch"
    
    // Errors
    const val ERROR_OCCURRED = "error_occurred"
    const val AI_REQUEST_FAILED = "ai_request_failed"
    const val PRICE_FETCH_FAILED = "price_fetch_failed"
    const val OCR_SCAN_FAILED = "ocr_scan_failed"
    const val DATABASE_ERROR = "database_error"
    
    // App lifecycle
    const val APP_OPENED = "app_opened"
    const val APP_BACKGROUNDED = "app_backgrounded"
    const val FIRST_LAUNCH = "first_launch"
    
    // Security
    const val BIOMETRIC_AUTH_SUCCESS = "biometric_auth_success"
    const val BIOMETRIC_AUTH_FAILED = "biometric_auth_failed"
    const val PASSWORD_AUTH_SUCCESS = "password_auth_success"
    const val PASSWORD_AUTH_FAILED = "password_auth_failed"
}

/**
 * Common property keys for analytics events
 */
object AnalyticsProperties {
    const val SCREEN_NAME = "screen_name"
    const val ITEM_ID = "item_id"
    const val ITEM_TYPE = "item_type"
    const val RECIPIENT_ID = "recipient_id"
    const val GIFT_ID = "gift_id"
    const val INTEREST_ID = "interest_id"
    const val ERROR_MESSAGE = "error_message"
    const val ERROR_CODE = "error_code"
    const val FEATURE_NAME = "feature_name"
    const val SOURCE = "source"
    const val CATEGORY = "category"
    const val PRICE_RANGE = "price_range"
    const val RELATIONSHIP_TYPE = "relationship_type"
    const val AI_MODEL = "ai_model"
    const val RESPONSE_TIME_MS = "response_time_ms"
    const val SUCCESS = "success"
    const val COUNT = "count"
    const val DURATION_MS = "duration_ms"
}