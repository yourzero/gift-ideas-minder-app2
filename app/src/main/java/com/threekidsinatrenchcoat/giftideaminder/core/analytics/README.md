# Analytics Module

The Analytics module provides a facade for tracking user interactions, feature usage, and error events across the Gift Idea Minder app.

## Features

- **Abstract Interface**: Hide analytics implementation details from the rest of the app
- **Multiple Providers**: Support for different analytics backends (NoOp for dev, Firebase for prod)
- **Event Logging**: Structured event tracking with custom properties
- **User Properties**: Set user-level properties for analytics
- **Extension Functions**: Convenient helper functions for common analytics patterns
- **Compose Integration**: Automatic screen view tracking for Composables
- **Performance Timing**: Built-in timing utilities for measuring event duration
- **Error Tracking**: Structured error logging with context

## Usage

### Basic Event Logging

```kotlin
@HiltAndroidApp
class MyApplication : Application() {
    @Inject lateinit var analytics: Analytics
    
    fun onCreate() {
        super.onCreate()
        analytics.logEvent(AnalyticsEvents.APP_OPENED)
    }
}
```

### In ViewModels

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val analytics: Analytics
) : ViewModel() {
    
    fun addGift(gift: Gift) {
        analytics.logUserAction(
            action = AnalyticsEvents.ADD_GIFT,
            itemId = gift.id,
            additionalProperties = mapOf(
                AnalyticsProperties.CATEGORY to gift.category,
                AnalyticsProperties.PRICE_RANGE to gift.priceRange
            )
        )
    }
    
    fun handleError(error: Throwable) {
        analytics.logError(error, context = "MyViewModel")
    }
}
```

### In Composables

```kotlin
@Composable
fun MyScreen(analytics: Analytics) {
    // Automatically track screen view
    TrackScreen(analytics, "my_screen")
    
    Button(
        onClick = { 
            analytics.logUserAction(AnalyticsEvents.ADD_RECIPIENT)
        }
    ) {
        Text("Add Recipient")
    }
}
```

### Timing Events

```kotlin
class MyService @Inject constructor(
    private val analytics: Analytics
) {
    suspend fun loadData() {
        analytics.timed(AnalyticsEvents.DATA_LOADED) {
            // Long-running operation
            delay(1000)
        }
    }
}
```

### AI Interactions

```kotlin
analytics.logAIInteraction(
    action = AnalyticsEvents.USE_AI_SUGGESTION,
    model = "gemini-pro",
    responseTimeMs = 1500,
    success = true,
    additionalProperties = mapOf(
        "suggestion_type" to "gift_idea",
        "user_rating" to 5
    )
)
```

## Architecture

### Interfaces
- `Analytics`: Main facade interface
- `AnalyticsProvider`: Interface for implementing different analytics backends

### Implementations
- `AnalyticsImpl`: Main implementation that delegates to multiple providers
- `NoOpAnalyticsProvider`: No-operation provider for development/testing
- `FirebaseAnalyticsProvider`: Firebase Analytics integration (stub implementation)

### Dependency Injection
- `AnalyticsModule`: Hilt module providing Analytics dependencies
- Uses `@IntoSet` to support multiple analytics providers
- Automatically configures based on `BuildConfig.DEBUG`

## Providers

### NoOpAnalyticsProvider
- Always included for development and testing
- Does nothing, ensures app works without external dependencies
- Useful for local development and testing

### FirebaseAnalyticsProvider
- Stub implementation ready for Firebase integration
- Only initialized in non-debug builds
- TODO: Implement actual Firebase Analytics calls

## Event Constants

### User Actions
- `ADD_RECIPIENT`, `ADD_GIFT`, `ADD_INTEREST`
- `EDIT_RECIPIENT`, `EDIT_GIFT`
- `DELETE_RECIPIENT`, `DELETE_GIFT`

### Feature Usage
- `START_TWENTY_QUESTIONS`, `COMPLETE_TWENTY_QUESTIONS`
- `UNLOCK_ACHIEVEMENT`, `USE_AI_SUGGESTION`
- `OCR_SCAN_GIFT`, `PRICE_TRACKING_ENABLED`

### Navigation
- `SCREEN_VIEW`, `TAB_SWITCH`

### Errors
- `ERROR_OCCURRED`, `AI_REQUEST_FAILED`
- `PRICE_FETCH_FAILED`, `OCR_SCAN_FAILED`

### Security
- `BIOMETRIC_AUTH_SUCCESS`, `PASSWORD_AUTH_SUCCESS`

## Property Constants

Common property keys for consistent event tracking:
- `SCREEN_NAME`, `ITEM_ID`, `ITEM_TYPE`
- `ERROR_MESSAGE`, `ERROR_CODE`
- `FEATURE_NAME`, `SOURCE`, `CATEGORY`
- `AI_MODEL`, `RESPONSE_TIME_MS`, `SUCCESS`

## Extension Functions

- `logScreenView()`: Screen view tracking
- `logError()`: Structured error logging
- `logFeatureUsed()`: Feature usage tracking
- `logUserAction()`: User action with timing
- `logAIInteraction()`: AI interaction with metrics
- `timed()`: Execute block and log timing

## Integration Steps

1. **Inject Analytics**: Use `@Inject` to get Analytics instance
2. **Log Events**: Use predefined event constants or custom events
3. **Add Properties**: Include relevant context with events
4. **Track Screens**: Use `TrackScreen` composable for automatic tracking
5. **Handle Errors**: Use `logError` for consistent error tracking

## Future Enhancements

- Complete Firebase Analytics integration
- Add more analytics providers (Mixpanel, Amplitude, etc.)
- Implement event batching for performance
- Add event validation and schema checking
- Support for custom dimensions and metrics