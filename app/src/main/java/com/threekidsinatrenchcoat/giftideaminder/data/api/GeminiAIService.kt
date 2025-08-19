package com.threekidsinatrenchcoat.giftideaminder.data.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import com.threekidsinatrenchcoat.giftideaminder.data.model.SmsAnalysisRequest
import com.threekidsinatrenchcoat.giftideaminder.data.model.SmsAnalysisResponse
import com.threekidsinatrenchcoat.giftideaminder.data.model.SmsPersonInsight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * AIService implementation backed by Google Generative Language API (Gemini 1.5 Flash).
 *
 * Calls the v1beta generateContent endpoint with response_mime_type = application/json
 * and parses the model's JSON output into domain models.
 */
class GeminiAIService(
    private val apiKey: String,
    private val modelName: String = "gemini-1.5-flash-002"
) : AIService {

    private val gson = Gson()
    private val httpClient: OkHttpClient = OkHttpClient()

    private val endpointBase: String = "https://generativelanguage.googleapis.com/v1beta/models/"

    // --- Google Generative Language API request/response types ---
    private data class GenerationConfig(
        val temperature: Double? = 0.4,
        @SerializedName("topP") val topP: Double? = 0.95,
        @SerializedName("topK") val topK: Int? = 40,
        @SerializedName("response_mime_type") val responseMimeType: String? = "application/json"
    )

    private data class GeminiPart(val text: String)
    private data class GeminiContent(val role: String = "user", val parts: List<GeminiPart>)
    private data class GeminiRequest(
        val contents: List<GeminiContent>,
        @SerializedName("generationConfig") val generationConfig: GenerationConfig = GenerationConfig()
    )

    private data class GeminiCandidate(val content: GeminiContentResponse?)
    private data class GeminiContentResponse(val parts: List<GeminiPart>?)
    private data class GeminiResponse(val candidates: List<GeminiCandidate>?)

    // --- Public API (AIService) ---
    override suspend fun getSuggestions(request: AIRequest): List<Gift> {
        Log.d("GeminiAIService", "Starting getSuggestions - building prompt")
        val prompt = buildSuggestionsPrompt(request)
        Log.d("GeminiAIService", "Prompt built, calling Gemini API")
        Log.d("GeminiAIService", "Full prompt: $prompt")
        
        // Check for budget constraint
        val budgetHint = request.gifts.find { it.title == "__BUDGET_HINT__" }
        val budgetRange = budgetHint?.description?.let { desc ->
            val parts = desc.split("-")
            if (parts.size == 2) {
                val min = parts[0].toDoubleOrNull()
                val max = parts[1].toDoubleOrNull()
                if (min != null && max != null) Pair(min, max) else null
            } else null
        }

        val jsonText: String = try {
            callGemini(prompt)
        } catch (e: Exception) {
            Log.e("GeminiAIService", "Error calling Gemini API", e)
            throw e
        }
        
        Log.d("GeminiAIService", "Gemini API response received: ${jsonText.take(500)}...")
        Log.d("GeminiAIService", "Full response: $jsonText")
        
        val jsonArray: JsonElement? = extractTopLevelJsonArray(jsonText)
        if (jsonArray == null || !jsonArray.isJsonArray) {
            Log.w("GeminiAIService", "Failed to extract JSON array from response")
            return emptyList()
        }
        
        Log.d("GeminiAIService", "JSON array extracted, parsing gifts")

        val suggestions = mutableListOf<Gift>()
        jsonArray.asJsonArray.forEach { element ->
            runCatching {
                val obj: JsonObject = element.asJsonObject
                val title: String = obj.get("title")?.asString?.trim().orEmpty()
                if (title.isBlank()) return@runCatching
                val description: String? = obj.get("description")?.takeIf { !it.isJsonNull }?.asString
                val url: String? = obj.get("url")?.takeIf { !it.isJsonNull }?.asString
                val imageUrl: String? = obj.get("imageUrl")?.takeIf { !it.isJsonNull }?.asString
                val estimatedPrice: Double? = obj.get("estimatedPrice")?.takeIf { !it.isJsonNull }?.let {
                    runCatching { it.asDouble }.getOrNull()
                }
                val personId: Int? = obj.get("personId")?.takeIf { !it.isJsonNull }?.let {
                    runCatching { it.asInt }.getOrNull()
                }
                val tags: List<String>? = obj.get("tags")?.takeIf { it.isJsonArray }?.asJsonArray?.mapNotNull { je ->
                    runCatching { je.asString }.getOrNull()
                }

                val gift = Gift(
                    title = title,
                    description = description,
                    url = url,  // Product purchase URL
                    imageUrl = imageUrl,  // Image URL for display
                    currentPrice = estimatedPrice,
                    personId = personId,
                    tags = tags
                )
                suggestions.add(gift)
                Log.d("GeminiAIService", "Parsed gift: ${gift.title}")
            }.onFailure { e ->
                Log.w("GeminiAIService", "Failed to parse gift from JSON element", e)
            }
        }
        
        // Apply budget filter as fallback if AI didn't respect budget constraints
        val filteredSuggestions = if (budgetRange != null) {
            val (minBudget, maxBudget) = budgetRange
            suggestions.filter { gift ->
                val price = gift.currentPrice
                if (price == null) {
                    Log.w("GeminiAIService", "Suggestion '${gift.title}' has no price, excluding from budget-filtered results")
                    false
                } else {
                    price in minBudget..maxBudget
                }
            }.also { filtered ->
                if (filtered.size < suggestions.size) {
                    Log.w("GeminiAIService", "Budget filter removed ${suggestions.size - filtered.size} suggestions outside $minBudget-$maxBudget range")
                }
            }
        } else {
            suggestions
        }

        Log.d("GeminiAIService", "Final suggestions count: ${filteredSuggestions.size}")

        // If budget filtering resulted in no suggestions, return an empty list
        if (budgetRange != null && filteredSuggestions.isEmpty()) {
            Log.w("GeminiAIService", "No suggestions found within budget range ${budgetRange.first}-${budgetRange.second}")
        }

        return filteredSuggestions
    }

    override suspend fun summarizeMessages(request: SummarizeMessagesRequest): SummarizeMessagesResponse {
        Log.d("GeminiAIService", "Starting summarizeMessages - building prompt")
        val prompt: String = buildSummarizePrompt(request)
        Log.d("GeminiAIService", "Summarize prompt: $prompt")
        val jsonText: String = callGemini(prompt)
        Log.d("GeminiAIService", "Summarize response: $jsonText")
        val jsonObj: JsonObject? = extractTopLevelJsonObject(jsonText)
        if (jsonObj == null || !jsonObj.has("insights")) {
            return SummarizeMessagesResponse(insights = emptyList())
        }
        val insightsArray: JsonElement = jsonObj.get("insights")
        val insights = mutableListOf<PersonInsightDTO>()
        if (insightsArray.isJsonArray) {
            insightsArray.asJsonArray.forEach { el ->
                runCatching {
                    val o = el.asJsonObject
                    val name = o.get("name")?.asString ?: return@runCatching
                    val interests: List<String> = o.get("interests")?.takeIf { it.isJsonArray }?.asJsonArray?.mapNotNull { it.asString } ?: emptyList()
                    val avoid: List<String> = o.get("avoid")?.takeIf { it.isJsonArray }?.asJsonArray?.mapNotNull { it.asString } ?: emptyList()
                    val sizes: String? = o.get("sizes")?.takeIf { !it.isJsonNull }?.asString
                    val notes: String? = o.get("notes")?.takeIf { !it.isJsonNull }?.asString
                    val specialDates: List<String> = o.get("specialDates")?.takeIf { it.isJsonArray }?.asJsonArray?.mapNotNull { it.asString } ?: emptyList()
                    insights.add(
                        PersonInsightDTO(
                            name = name,
                            interests = interests,
                            avoid = avoid,
                            sizes = sizes,
                            notes = notes,
                            specialDates = specialDates
                        )
                    )
                }
            }
        }
        return SummarizeMessagesResponse(insights = insights)
    }

    /**
     * Analyze SMS conversations to extract gift-related insights
     */
    suspend fun analyzeSmsConversations(request: SmsAnalysisRequest): SmsAnalysisResponse {
        Log.d("GeminiAIService", "Starting analyzeSmsConversations - building prompt")
        val prompt = buildSmsAnalysisPrompt(request)
        Log.d("GeminiAIService", "SMS analysis prompt built, calling Gemini API")
        Log.d("GeminiAIService", "SMS analysis prompt: $prompt")
        
        val jsonText: String = try {
            callGemini(prompt)
        } catch (e: Exception) {
            Log.e("GeminiAIService", "Error calling Gemini API for SMS analysis", e)
            throw e
        }
        
        Log.d("GeminiAIService", "SMS analysis response received: ${jsonText.take(500)}...")
        Log.d("GeminiAIService", "Full SMS analysis response: $jsonText")
        
        val jsonObj: JsonObject? = extractTopLevelJsonObject(jsonText)
        if (jsonObj == null || !jsonObj.has("insights")) {
            Log.w("GeminiAIService", "Failed to extract insights from SMS analysis response")
            return SmsAnalysisResponse(insights = emptyList())
        }
        
        val insightsArray: JsonElement = jsonObj.get("insights")
        val insights = mutableListOf<SmsPersonInsight>()
        
        if (insightsArray.isJsonArray) {
            insightsArray.asJsonArray.forEach { element ->
                runCatching {
                    val obj = element.asJsonObject
                    val contactName = obj.get("contactName")?.takeIf { !it.isJsonNull }?.asString
                    val phoneNumber = obj.get("phoneNumber")?.asString ?: return@runCatching
                    val extractedInterests = obj.get("extractedInterests")?.takeIf { it.isJsonArray }?.asJsonArray?.mapNotNull { it.asString } ?: emptyList()
                    val mentionedItems = obj.get("mentionedItems")?.takeIf { it.isJsonArray }?.asJsonArray?.mapNotNull { it.asString } ?: emptyList()
                    val avoidItems = obj.get("avoidItems")?.takeIf { it.isJsonArray }?.asJsonArray?.mapNotNull { it.asString } ?: emptyList()
                    val specialDates = obj.get("specialDates")?.takeIf { it.isJsonArray }?.asJsonArray?.mapNotNull { it.asString } ?: emptyList()
                    val notes = obj.get("notes")?.takeIf { !it.isJsonNull }?.asString
                    val confidence = obj.get("confidence")?.takeIf { !it.isJsonNull }?.asFloat ?: 0.5f
                    
                    insights.add(
                        SmsPersonInsight(
                            contactName = contactName,
                            phoneNumber = phoneNumber,
                            extractedInterests = extractedInterests,
                            mentionedItems = mentionedItems,
                            avoidItems = avoidItems,
                            specialDates = specialDates,
                            notes = notes,
                            confidence = confidence
                        )
                    )
                    Log.d("GeminiAIService", "Parsed SMS insight for ${contactName ?: phoneNumber}")
                }.onFailure { e ->
                    Log.w("GeminiAIService", "Failed to parse SMS insight from JSON element", e)
                }
            }
        }
        
        Log.d("GeminiAIService", "SMS analysis complete: ${insights.size} insights extracted")
        return SmsAnalysisResponse(insights = insights)
    }

    // --- Internal helpers ---
    private suspend fun callGemini(prompt: String): String = withContext(Dispatchers.IO) {
        Log.d("GeminiAIService", "Calling Gemini API with model: $modelName")
        
        val url = "$endpointBase$modelName:generateContent?key=$apiKey"
        val req = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt))))
        )
        val json = gson.toJson(req)
        Log.d("GeminiAIService", "Request JSON length: ${json.length}")
        
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toRequestBody(mediaType)
        val httpRequest = Request.Builder()
            .url(url)
            .post(body)
            .build()

        httpClient.newCall(httpRequest).execute().use { resp ->
            Log.d("GeminiAIService", "HTTP response code: ${resp.code}")
            
            if (!resp.isSuccessful) {
                val errorBody = resp.body?.string()
                Log.e("GeminiAIService", "Gemini API error: HTTP ${resp.code}, body: $errorBody")
                throw IllegalStateException("Gemini API error: HTTP ${resp.code}")
            }
            
            val respBody = resp.body?.string().orEmpty()
            Log.d("GeminiAIService", "Raw response body length: ${respBody.length}")
            
            val parsed: GeminiResponse = try {
                gson.fromJson(respBody, GeminiResponse::class.java)
            } catch (e: Exception) {
                Log.e("GeminiAIService", "Failed to parse Gemini response as JSON", e)
                Log.d("GeminiAIService", "Raw response: $respBody")
                throw e
            }
            
            val text: String = parsed.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.joinToString(separator = "") { it.text }
                .orEmpty()
                
            Log.d("GeminiAIService", "Extracted text length: ${text.length}")
            Log.d("GeminiAIService", "Extracted text preview: ${text.take(200)}...")
            
            text
        }
    }

    private fun buildSuggestionsPrompt(request: AIRequest): String {
        // Check if there's a person hint in the gifts list
        val personHint = request.gifts.find { it.title == "__PERSON_HINT__" }
        val focusPersonId = personHint?.personId

        // Check if there's a budget hint
        val budgetHint = request.gifts.find { it.title == "__BUDGET_HINT__" }
        val budgetRange = budgetHint?.description?.let { desc ->
            val parts = desc.split("-")
            if (parts.size == 2) {
                val min = parts[0].toDoubleOrNull()
                val max = parts[1].toDoubleOrNull()
                if (min != null && max != null) Pair(min, max) else null
            } else null
        }

        // Check if there's an occasion hint
        val occasionHint = request.gifts.find { it.title == "__OCCASION_HINT__" }
        val occasion = occasionHint?.description


        val interestsHint = request.gifts.find { it.title == "__INTERESTS_HINT__" }
        val ownedHint = request.gifts.find { it.title == "__OWNED_HINT__" }

        val actualGifts = request.gifts.filter {
            !listOf("__PERSON_HINT__", "__BUDGET_HINT__", "__OCCASION_HINT__", "__INTERESTS_HINT__", "__OWNED_HINT__").contains(it.title)
        }


        val giftsJson = gson.toJson(actualGifts.map { g ->
            mapOf(
                "id" to g.id,
                "title" to g.title,
                "description" to (g.description ?: ""),
                "url" to (g.url ?: ""),
                "currentPrice" to (g.currentPrice ?: 0.0),
                "personId" to (g.personId ?: 0),
                "tags" to (g.tags ?: emptyList<String>()),
                "budget" to (g.budget ?: 0.0)
            )
        })
        val personsJson = gson.toJson(request.persons.map { p ->
            mapOf(
                "id" to p.id,
                "name" to p.name,
                "relationships" to p.relationships,
                "notes" to (p.notes ?: ""),
                "preferences" to p.preferences,
                "defaultBudget" to (p.defaultBudget ?: 0.0)
            )
        })

        return buildString {
            appendLine("You are an assistant generating thoughtful gift suggestions with visual previews.")
            
            // Add occasion context if specified
            if (!occasion.isNullOrBlank()) {
                appendLine()
                appendLine("OCCASION CONTEXT: These gifts are for \"$occasion\".")
                appendLine("Tailor all suggestions to be appropriate for this specific occasion.")
                appendLine("Consider traditional gifts, cultural expectations, and seasonal relevance for this occasion.")
            }

            // Add budget constraint if specified
            if (budgetRange != null) {
                appendLine()
                appendLine("BUDGET CONSTRAINT: ALL suggestions must be priced between $${budgetRange.first} and $${budgetRange.second}.")
                appendLine("If you cannot find suitable suggestions within this budget range, return an empty array [].")
                appendLine("Do not suggest items outside this price range under any circumstances.")
            }

            // Add person-specific instruction if we have a focus person
            if (focusPersonId != null) {
                val focusPerson = request.persons.find { it.id == focusPersonId }
                if (focusPerson != null) {
                    appendLine("FOCUS: Generate gift suggestions specifically for ${focusPerson.name} (ID: $focusPersonId).")
                    appendLine("Consider their preferences: ${focusPerson.preferences.joinToString(", ")}")
                    if (focusPerson.notes?.isNotBlank() == true) {
                        appendLine("Additional context: ${focusPerson.notes}")
                    }
                    appendLine("Set personId to $focusPersonId for ALL suggestions.")

                    // Add budget consideration from person's default budget
                    val personBudget = focusPerson.defaultBudget
                    if (personBudget != null && personBudget > 0 && budgetRange == null) {
                        appendLine("PERSON BUDGET: Consider ${focusPerson.name}'s default budget of $$personBudget when suggesting items.")
                        appendLine("Prefer suggestions within or slightly below this budget range.")
                    }

                    // Add interests context
                    if (interestsHint != null && !interestsHint.description.isNullOrBlank()) {
                        appendLine()
                        appendLine("INTERESTS TO CONSIDER:")
                        val interests = interestsHint.description!!.split("|")
                        interests.forEach { interest ->
                            val (type, value) = interest.split(":", limit = 2)
                            appendLine("- $value (${type.lowercase()} interest)")
                        }
                        appendLine("Base your suggestions on these interests, especially the specific ones.")
                    }

                    // Add already owned items to avoid
                    if (ownedHint != null && !ownedHint.description.isNullOrBlank()) {
                        appendLine()
                        appendLine("ITEMS ALREADY OWNED (DO NOT SUGGEST):")
                        val ownedItems = ownedHint.description!!.split("|")
                        ownedItems.forEach { ownedItem ->
                            val (type, value) = ownedItem.split(":", limit = 2)
                            appendLine("- $value (already owned)")
                        }
                        appendLine("IMPORTANT: Do not suggest these items or very similar items.")
                    }

                    appendLine()
                }
            }

            // Add general budget guidance from existing gifts
            val budgetsFromGifts = actualGifts.mapNotNull { it.budget }.filter { it > 0 }
            if (budgetsFromGifts.isNotEmpty() && budgetRange == null) {
                val avgBudget = budgetsFromGifts.average()
                appendLine("BUDGET GUIDANCE: Based on existing gifts, typical budget appears to be around $${"%.2f".format(avgBudget)}.")
                appendLine("Consider this as a guide for price ranges of suggestions.")
                appendLine()
            }
            
            appendLine("Input gifts (existing and recent):")
            appendLine(giftsJson)
            appendLine("People context (include preferences to tailor suggestions):")
            appendLine(personsJson)
            appendLine()
            appendLine("Return ONLY a strict JSON array (no code fences, no extra text). Each element object must include:")
            appendLine("- title (string, required)")
            appendLine("- description (string, optional)")
            appendLine("- url (string, optional - product purchase URL)")
            appendLine("- imageUrl (string, HIGHLY PREFERRED - provide a direct image URL that shows what the gift looks like)")
            appendLine("- estimatedPrice (number, ${if (budgetRange != null) "REQUIRED - must be between $${budgetRange.first} and $${budgetRange.second}" else "optional"})")
            appendLine("- personId (integer, ${if (focusPersonId != null) "REQUIRED - must be $focusPersonId" else "optional — if a clear match"})")
            appendLine("- tags (array of strings, optional)")
            appendLine("- reason (string, optional)")
            appendLine()
            appendLine("PRICE REQUIREMENTS:")
            if (budgetRange != null) {
                appendLine("- estimatedPrice is MANDATORY for all suggestions")
                appendLine("- ALL prices must be between $${budgetRange.first} and $${budgetRange.second}")
                appendLine("- If no items can be found within budget, return empty array: []")
            } else {
                appendLine("- Include realistic estimated prices when possible")
                appendLine("- Consider budget context from person's defaultBudget or existing gift budgets")
            }
            appendLine()
            appendLine("IMAGE REQUIREMENTS:")
            appendLine("- Always try to provide 'imageUrl' with a real, working image URL")
            appendLine("- Use publicly accessible images from: Amazon, Target, Walmart, Etsy, stock photos, or other retailers")
            appendLine("- Prefer actual product images over generic category images")
            appendLine("- Image URLs should be direct links to .jpg, .png, .webp files or retailer image endpoints")
            appendLine("- Examples of good image URLs:")
            appendLine("  - https://m.media-amazon.com/images/I/[product-id].jpg")
            appendLine("  - https://target.scene7.com/is/image/Target/[product-id]")
            appendLine("  - https://i5.walmartimages.com/[image-path]")
            appendLine("- If you cannot find a specific product image, use a representative stock photo")
            
            if (focusPersonId != null) {
                appendLine()
                appendLine("REMINDER: ALL suggestions must have personId set to $focusPersonId")
            }

            if (budgetRange != null) {
                appendLine()
                appendLine("FINAL REMINDER: ALL suggestions must have estimatedPrice between $${budgetRange.first} and $${budgetRange.second}")
            }

            if (!occasion.isNullOrBlank()) {
                appendLine()
                appendLine("FINAL REMINDER: ALL suggestions must be appropriate for \"$occasion\"")
            }
        }
    }

    private fun buildSummarizePrompt(request: SummarizeMessagesRequest): String {
        val messagesJson = gson.toJson(request.messages)
        val personsJson = gson.toJson(request.persons)
        return buildString {
            appendLine("You analyze SMS-like messages and extract per-person gift-related insights.")
            appendLine("Messages (text + timestamp):")
            appendLine(messagesJson)
            appendLine("Person hints (names, relationships):")
            appendLine(personsJson)
            appendLine()
            appendLine("Return ONLY a strict JSON object with key 'insights' mapping to an array of person insights.")
            appendLine("Each insight must be: { name: string, interests: string[], avoid: string[], sizes?: string, notes?: string, specialDates: string[] }")
        }
    }

    private fun buildSmsAnalysisPrompt(request: SmsAnalysisRequest): String {
        val conversationsJson = gson.toJson(request.conversations.map { conversation ->
            mapOf(
                "contactName" to conversation.contactName,
                "phoneNumber" to conversation.phoneNumber,
                "lastMessageDate" to conversation.lastMessageDate,
                "messages" to conversation.messages.map { message ->
                    mapOf(
                        "id" to message.id,
                        "body" to message.body,
                        "date" to message.date,
                        "type" to message.type, // 1 = received, 2 = sent
                        "address" to message.address
                    )
                }
            )
        })

        val personHintsJson = gson.toJson(request.personHints.map { person ->
            mapOf(
                "id" to person.id,
                "name" to person.name,
                "relationships" to person.relationships,
                "notes" to (person.notes ?: ""),
                "preferences" to person.preferences
            )
        })

        return buildString {
            appendLine("You are an AI assistant that analyzes SMS conversations to extract gift-related insights and preferences.")
            appendLine()
            appendLine("TASK: Analyze the provided SMS conversations to identify:")
            appendLine("1. Interests and hobbies mentioned by each contact")
            appendLine("2. Items they want, need, or have expressed interest in")
            appendLine("3. Things they dislike or already own (to avoid as gifts)")
            appendLine("4. Special dates mentioned (birthdays, anniversaries, etc.)")
            appendLine("5. General notes about their preferences or lifestyle")
            appendLine()
            
            appendLine("ANALYSIS GUIDELINES:")
            appendLine("- Focus on gift-relevant information only")
            appendLine("- Look for mentions of hobbies, interests, wants, needs")
            appendLine("- Identify items they've purchased, received, or talked about wanting")
            appendLine("- Note any size preferences, brand preferences, or specific requirements")
            appendLine("- Pay attention to upcoming events or occasions they mention")
            appendLine("- Assign confidence scores based on clarity and frequency of mentions")
            appendLine("- Only include insights with confidence >= 0.3")
            appendLine()
            
            appendLine("SMS CONVERSATIONS:")
            appendLine(conversationsJson)
            appendLine()
            
            appendLine("PERSON CONTEXT (existing contacts to potentially match):")
            appendLine(personHintsJson)
            appendLine()
            
            if (request.dateRangeStart != null && request.dateRangeEnd != null) {
                appendLine("DATE FILTER: Only analyze messages between ${request.dateRangeStart} and ${request.dateRangeEnd}")
                appendLine()
            }
            
            appendLine("Return ONLY a strict JSON object with the following structure:")
            appendLine("{")
            appendLine("  \"insights\": [")
            appendLine("    {")
            appendLine("      \"contactName\": \"string (null if unknown)\",")
            appendLine("      \"phoneNumber\": \"string (required)\",")
            appendLine("      \"extractedInterests\": [\"string array of interests/hobbies\"],")
            appendLine("      \"mentionedItems\": [\"string array of specific items they want/need\"],")
            appendLine("      \"avoidItems\": [\"string array of items they dislike/already have\"],")
            appendLine("      \"specialDates\": [\"string array of important dates mentioned\"],")
            appendLine("      \"notes\": \"string (additional context or preferences)\",")
            appendLine("      \"confidence\": 0.0-1.0")
            appendLine("    }")
            appendLine("  ]")
            appendLine("}")
            appendLine()
            
            appendLine("CONFIDENCE SCORING:")
            appendLine("- 0.9-1.0: Explicitly stated preferences (\"I love...\", \"I want...\", \"I need...\")")
            appendLine("- 0.7-0.8: Strong implications from context (frequent mentions, enthusiastic discussion)")
            appendLine("- 0.5-0.6: Moderate implications (casual mentions, indirect references)")
            appendLine("- 0.3-0.4: Weak implications (single mentions, unclear context)")
            appendLine("- Below 0.3: Exclude from results")
            appendLine()
            
            appendLine("IMPORTANT:")
            appendLine("- Only return insights with meaningful gift-related information")
            appendLine("- Ignore generic pleasantries, work discussions, or irrelevant chat")
            appendLine("- Focus on actionable gift insights only")
            appendLine("- Be conservative with confidence scores - accuracy is more important than quantity")
        }
    }

    private fun extractTopLevelJsonArray(text: String): JsonElement? {
        val start = text.indexOf('[')
        val end = text.lastIndexOf(']')
        if (start >= 0 && end > start) {
            return runCatching { gson.fromJson(text.substring(start, end + 1), JsonElement::class.java) }.getOrNull()
        }
        return null
    }

    private fun extractTopLevelJsonObject(text: String): JsonObject? {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start >= 0 && end > start) {
            return runCatching { gson.fromJson(text.substring(start, end + 1), JsonObject::class.java) }.getOrNull()
        }
        return null
    }
}

