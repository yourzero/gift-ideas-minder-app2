package com.threekidsinatrenchcoat.giftideaminder.core.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

/**
 * Extension functions for convenient analytics usage throughout the app
 */

/**
 * Log a screen view event with the given screen name
 */
fun Analytics.logScreenView(screenName: String) {
    logEvent(
        event = AnalyticsEvents.SCREEN_VIEW,
        properties = mapOf(AnalyticsProperties.SCREEN_NAME to screenName)
    )
}

/**
 * Log an error event with error details
 */
fun Analytics.logError(
    error: Throwable,
    context: String? = null,
    additionalProperties: Map<String, Any> = emptyMap()
) {
    val properties = buildMap {
        put(AnalyticsProperties.ERROR_MESSAGE, error.message ?: "Unknown error")
        put(AnalyticsProperties.ERROR_CODE, error::class.simpleName ?: "UnknownException")
        context?.let { put(AnalyticsProperties.SOURCE, it) }
        putAll(additionalProperties)
    }
    
    logEvent(
        event = AnalyticsEvents.ERROR_OCCURRED,
        properties = properties
    )
}

/**
 * Log a feature usage event
 */
fun Analytics.logFeatureUsed(
    featureName: String,
    additionalProperties: Map<String, Any> = emptyMap()
) {
    val properties = buildMap {
        put(AnalyticsProperties.FEATURE_NAME, featureName)
        putAll(additionalProperties)
    }
    
    logEvent(
        event = featureName,
        properties = properties
    )
}

/**
 * Log user action with timing
 */
fun Analytics.logUserAction(
    action: String,
    itemId: String? = null,
    itemType: String? = null,
    durationMs: Long? = null,
    additionalProperties: Map<String, Any> = emptyMap()
) {
    val properties = buildMap {
        itemId?.let { put(AnalyticsProperties.ITEM_ID, it) }
        itemType?.let { put(AnalyticsProperties.ITEM_TYPE, it) }
        durationMs?.let { put(AnalyticsProperties.DURATION_MS, it) }
        putAll(additionalProperties)
    }
    
    logEvent(
        event = action,
        properties = properties
    )
}

/**
 * Log AI interaction with performance metrics
 */
fun Analytics.logAIInteraction(
    action: String,
    model: String,
    responseTimeMs: Long? = null,
    success: Boolean,
    additionalProperties: Map<String, Any> = emptyMap()
) {
    val properties = buildMap {
        put(AnalyticsProperties.AI_MODEL, model)
        put(AnalyticsProperties.SUCCESS, success)
        responseTimeMs?.let { put(AnalyticsProperties.RESPONSE_TIME_MS, it) }
        putAll(additionalProperties)
    }
    
    logEvent(
        event = action,
        properties = properties
    )
}

/**
 * Composable function to automatically track screen views
 */
@Composable
fun TrackScreen(
    analytics: Analytics,
    screenName: String
) {
    val currentAnalytics by rememberUpdatedState(analytics)
    val currentScreenName by rememberUpdatedState(screenName)
    
    DisposableEffect(screenName) {
        currentAnalytics.logScreenView(currentScreenName)
        onDispose { }
    }
}

/**
 * Utility class for timing analytics events
 */
class AnalyticsTimer {
    private val startTime = System.currentTimeMillis()
    
    fun stop(): Long = System.currentTimeMillis() - startTime
    
    fun logTimed(
        analytics: Analytics,
        event: String,
        additionalProperties: Map<String, Any> = emptyMap()
    ) {
        val duration = stop()
        analytics.logEvent(
            event = event,
            properties = additionalProperties + (AnalyticsProperties.DURATION_MS to duration)
        )
    }
}

/**
 * Create a timer for measuring event duration
 */
fun Analytics.startTimer(): AnalyticsTimer = AnalyticsTimer()

/**
 * Execute a block and log the time it took
 */
inline fun <T> Analytics.timed(
    event: String,
    additionalProperties: Map<String, Any> = emptyMap(),
    block: () -> T
): T {
    val timer = startTimer()
    return try {
        val result = block()
        timer.logTimed(
            analytics = this,
            event = event,
            additionalProperties = additionalProperties + (AnalyticsProperties.SUCCESS to true)
        )
        result
    } catch (e: Exception) {
        timer.logTimed(
            analytics = this,
            event = event,
            additionalProperties = additionalProperties + mapOf(
                AnalyticsProperties.SUCCESS to false,
                AnalyticsProperties.ERROR_MESSAGE to (e.message ?: "Unknown error")
            )
        )
        throw e
    }
}