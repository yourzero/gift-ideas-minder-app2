package com.threekidsinatrenchcoat.giftideaminder.data.repository

import android.util.Log
import com.threekidsinatrenchcoat.giftideaminder.data.api.AIService
import com.threekidsinatrenchcoat.giftideaminder.data.api.AIRequest
import com.threekidsinatrenchcoat.giftideaminder.data.api.GeminiAIService
import com.threekidsinatrenchcoat.giftideaminder.data.api.PersonDTO
import com.threekidsinatrenchcoat.giftideaminder.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsAnalysisRepository @Inject constructor(
    private val smsRepository: SmsRepository,
    private val personRepository: PersonRepository,
    private val giftRepository: GiftRepository,
    private val aiService: AIService
) {
    
    companion object {
        private const val TAG = "SmsAnalysisRepository"
        private const val MAX_CONVERSATIONS = 10 // Limit conversations to analyze
        private const val SMS_LOOKBACK_DAYS = 30 // Only look at recent messages
    }

    /**
     * Analyze SMS messages for a specific person and generate gift suggestions
     */
    suspend fun analyzeSmsForPerson(
        personId: Int,
        navigateToSuggestions: (personId: Int) -> Unit
    ): SmsAnalysisResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting SMS analysis for person $personId")
            
            // Get person details
            val person = personRepository.getById(personId)
            if (person == null) {
                Log.w(TAG, "Person not found: $personId")
                return@withContext SmsAnalysisResult.Error("Person not found")
            }
            
            Log.d(TAG, "Analyzing SMS for person: ${person.name}")
            
            // Check SMS permission
            if (!smsRepository.hasReadSmsPermission()) {
                Log.w(TAG, "No SMS permission available")
                return@withContext SmsAnalysisResult.Error("SMS permission required")
            }
            
            // Get all SMS conversations
            val conversations = smsRepository.getSmsMessages(
                daysLookback = SMS_LOOKBACK_DAYS,
                maxMessagesPerConversation = 50
            ).take(MAX_CONVERSATIONS)
            
            if (conversations.isEmpty()) {
                Log.w(TAG, "No SMS conversations found")
                return@withContext SmsAnalysisResult.Error("No SMS conversations found")
            }
            
            Log.d(TAG, "Found ${conversations.size} SMS conversations to analyze")
            
            // Get existing persons for context
            val allPersons = personRepository.getAll()
            val personHints = allPersons.map { p ->
                PersonDTO(
                    id = p.id,
                    name = p.name,
                    relationships = p.relationships,
                    notes = p.notes,
                    preferences = p.preferences,
                    defaultBudget = p.defaultBudget
                )
            }
            
            // Analyze SMS conversations
            val analysisRequest = SmsAnalysisRequest(
                conversations = conversations,
                personHints = personHints,
                dateRangeStart = System.currentTimeMillis() - (SMS_LOOKBACK_DAYS * 24 * 60 * 60 * 1000L),
                dateRangeEnd = System.currentTimeMillis()
            )
            
            val geminiService = aiService as GeminiAIService
            val analysisResponse = geminiService.analyzeSmsConversations(analysisRequest)
            
            Log.d(TAG, "SMS analysis complete: ${analysisResponse.insights.size} insights found")
            
            // Apply insights to existing persons or suggest new gift ideas
            val appliedInsights = applyInsightsToPersons(analysisResponse.insights, allPersons)
            
            // Generate new gift suggestions based on insights
            val newSuggestions = generateGiftSuggestionsFromInsights(
                analysisResponse.insights,
                personId,
                personHints
            )
            
            Log.d(TAG, "Generated ${newSuggestions.size} new gift suggestions from SMS insights")
            
            // Navigate to suggestions if we have results
            if (newSuggestions.isNotEmpty()) {
                navigateToSuggestions(personId)
            }
            
            SmsAnalysisResult.Success(
                insights = analysisResponse.insights,
                appliedInsights = appliedInsights,
                generatedSuggestions = newSuggestions
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during SMS analysis", e)
            SmsAnalysisResult.Error("SMS analysis failed: ${e.message}")
        }
    }

    private suspend fun applyInsightsToPersons(
        insights: List<SmsPersonInsight>,
        existingPersons: List<Person>
    ): List<PersonInsightApplication> {
        val applications = mutableListOf<PersonInsightApplication>()
        
        insights.forEach { insight ->
            // Try to match SMS insight to existing person
            val matchedPerson = matchInsightToPerson(insight, existingPersons)
            
            if (matchedPerson != null) {
                Log.d(TAG, "Matched SMS insight ${insight.phoneNumber} to person ${matchedPerson.name}")
                
                // Update person with new preferences (non-destructive)
                val updatedPreferences = (matchedPerson.preferences + insight.extractedInterests)
                    .distinct()
                    .filter { it.isNotBlank() }
                
                if (updatedPreferences.size > matchedPerson.preferences.size) {
                    val updatedPerson = matchedPerson.copy(preferences = updatedPreferences)
                    personRepository.update(updatedPerson)
                    
                    applications.add(
                        PersonInsightApplication(
                            personId = matchedPerson.id,
                            personName = matchedPerson.name,
                            newInterests = insight.extractedInterests,
                            confidence = insight.confidence
                        )
                    )
                }
            } else {
                Log.d(TAG, "No matching person found for SMS insight: ${insight.contactName ?: insight.phoneNumber}")
            }
        }
        
        return applications
    }

    private fun matchInsightToPerson(insight: SmsPersonInsight, persons: List<Person>): Person? {
        val contactName = insight.contactName
        val phoneNumber = insight.phoneNumber
        
        // First try exact name match
        if (contactName != null) {
            val exactMatch = persons.find { person ->
                person.name.equals(contactName, ignoreCase = true)
            }
            if (exactMatch != null) return exactMatch
            
            // Try fuzzy name matching
            val fuzzyMatch = persons.find { person ->
                val personNameWords = person.name.split(" ", "-", ".").map { it.lowercase() }
                val contactNameWords = contactName.split(" ", "-", ".").map { it.lowercase() }
                
                // Check if any significant word matches
                personNameWords.any { personWord ->
                    contactNameWords.any { contactWord ->
                        personWord.length > 2 && contactWord.length > 2 && 
                        (personWord == contactWord || 
                         personWord.startsWith(contactWord) || 
                         contactWord.startsWith(personWord))
                    }
                }
            }
            if (fuzzyMatch != null) return fuzzyMatch
        }
        
        // Could potentially match by phone number if we stored them
        // For now, return null if no name match
        return null
    }

    private suspend fun generateGiftSuggestionsFromInsights(
        insights: List<SmsPersonInsight>,
        targetPersonId: Int,
        personHints: List<PersonDTO>
    ): List<Gift> {
        // Find the target person
        val targetPerson = personHints.find { it.id == targetPersonId }
        if (targetPerson == null) {
            Log.w(TAG, "Target person not found for gift suggestions: $targetPersonId")
            return emptyList()
        }
        
        // Combine interests from SMS insights (high confidence only)
        val highConfidenceInsights = insights.filter { it.confidence >= 0.5f }
        val allInterests = highConfidenceInsights.flatMap { it.extractedInterests }
        val allMentionedItems = highConfidenceInsights.flatMap { it.mentionedItems }
        
        if (allInterests.isEmpty() && allMentionedItems.isEmpty()) {
            Log.d(TAG, "No high-confidence interests found from SMS analysis")
            return emptyList()
        }
        
        // Create interests hint for AI
        val interestsHint = allInterests.joinToString("|") { "sms:$it" }
        val mentionedHint = allMentionedItems.joinToString("|") { "mentioned:$it" }
        
        // Create AI request with SMS-derived insights
        val existingGifts = giftRepository.getByPersonId(targetPersonId)
        val hintsGifts = mutableListOf<Gift>()
        
        // Add person hint
        hintsGifts.add(Gift(title = "__PERSON_HINT__", description = "", personId = targetPersonId))
        
        // Add interests hint if we have any
        if (interestsHint.isNotBlank()) {
            hintsGifts.add(Gift(title = "__INTERESTS_HINT__", description = interestsHint))
        }
        
        // Add mentioned items as owned hint (to avoid suggesting)
        if (mentionedHint.isNotBlank()) {
            hintsGifts.add(Gift(title = "__OWNED_HINT__", description = mentionedHint))
        }
        
        val aiRequest = AIRequest(
            gifts = existingGifts + hintsGifts,
            persons = personHints
        )
        
        return try {
            val suggestions = aiService.getSuggestions(aiRequest)
            Log.d(TAG, "AI generated ${suggestions.size} gift suggestions from SMS insights")
            suggestions
        } catch (e: Exception) {
            Log.e(TAG, "Error generating AI suggestions from SMS insights", e)
            emptyList()
        }
    }
}

/**
 * Result of SMS analysis operation
 */
sealed class SmsAnalysisResult {
    data class Success(
        val insights: List<SmsPersonInsight>,
        val appliedInsights: List<PersonInsightApplication>,
        val generatedSuggestions: List<Gift>
    ) : SmsAnalysisResult()
    
    data class Error(val message: String) : SmsAnalysisResult()
}

/**
 * Application of SMS insights to an existing person
 */
data class PersonInsightApplication(
    val personId: Int,
    val personName: String,
    val newInterests: List<String>,
    val confidence: Float
)