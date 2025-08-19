package com.threekidsinatrenchcoat.giftideaminder.data.model

import com.threekidsinatrenchcoat.giftideaminder.data.api.PersonDTO

/**
 * Represents an SMS message for gift insight analysis
 */
data class SmsMessage(
    val id: String,
    val address: String, // Phone number or contact
    val body: String,
    val date: Long, // Timestamp
    val type: Int, // 1 = received, 2 = sent
    val threadId: String? = null
)

/**
 * Represents a conversation thread with a contact
 */
data class SmsConversation(
    val contactName: String?,
    val phoneNumber: String,
    val messages: List<SmsMessage>,
    val lastMessageDate: Long
)

/**
 * DTO for AI analysis request containing SMS messages
 */
data class SmsAnalysisRequest(
    val conversations: List<SmsConversation>,
    val personHints: List<PersonDTO>, // Existing persons to match against
    val dateRangeStart: Long? = null, // Optional date filtering
    val dateRangeEnd: Long? = null
)

/**
 * Response from AI analysis containing insights extracted from SMS
 */
data class SmsAnalysisResponse(
    val insights: List<SmsPersonInsight>
)

/**
 * Person insight extracted from SMS conversations
 */
data class SmsPersonInsight(
    val contactName: String?,
    val phoneNumber: String,
    val extractedInterests: List<String>,
    val mentionedItems: List<String>, // Things they talked about wanting/needing
    val avoidItems: List<String>, // Things they dislike or already have
    val specialDates: List<String>, // Birthdays, anniversaries mentioned
    val notes: String?, // Additional context from conversations
    val confidence: Float // 0.0 - 1.0 confidence in the analysis
)