package com.threekidsinatrenchcoat.giftideaminder.data.api

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
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
        val prompt = buildSuggestionsPrompt(request)
        val jsonText: String = callGemini(prompt)
        val jsonArray: JsonElement? = extractTopLevelJsonArray(jsonText)
        if (jsonArray == null || !jsonArray.isJsonArray) return emptyList()

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
                    url = imageUrl ?: url,  // Prioritize image URL for display, fallback to product URL
                    currentPrice = estimatedPrice,
                    personId = personId,
                    tags = tags
                )
                suggestions.add(gift)
            }
        }
        return suggestions
    }

    override suspend fun summarizeMessages(request: SummarizeMessagesRequest): SummarizeMessagesResponse {
        val prompt: String = buildSummarizePrompt(request)
        val jsonText: String = callGemini(prompt)
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

    // --- Internal helpers ---
    private suspend fun callGemini(prompt: String): String = withContext(Dispatchers.IO) {
        val url = "$endpointBase$modelName:generateContent?key=$apiKey"
        val req = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt))))
        )
        val json = gson.toJson(req)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toRequestBody(mediaType)
        val httpRequest = Request.Builder()
            .url(url)
            .post(body)
            .build()

        httpClient.newCall(httpRequest).execute().use { resp ->
            if (!resp.isSuccessful) throw IllegalStateException("Gemini API error: HTTP ${'$'}{resp.code}")
            val respBody = resp.body?.string().orEmpty()
            val parsed: GeminiResponse = gson.fromJson(respBody, GeminiResponse::class.java)
            val text: String = parsed.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.joinToString(separator = "") { it.text }
                .orEmpty()
            text
        }
    }

    private fun buildSuggestionsPrompt(request: AIRequest): String {
        val giftsJson = gson.toJson(request.gifts.map { g ->
            mapOf(
                "id" to g.id,
                "title" to g.title,
                "description" to (g.description ?: ""),
                "url" to (g.url ?: ""),
                "currentPrice" to (g.currentPrice ?: 0.0),
                "personId" to (g.personId ?: 0),
                "tags" to (g.tags ?: emptyList<String>())
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
            appendLine("- estimatedPrice (number, optional)")
            appendLine("- personId (integer, optional — if a clear match)")
            appendLine("- tags (array of strings, optional)")
            appendLine("- reason (string, optional)")
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

