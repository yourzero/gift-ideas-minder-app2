package com.threekidsinatrenchcoat.giftideaminder.data.api

import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class AIRequest(
    val gifts: List<Gift>,
    val persons: List<Person>  // Assuming Person is imported or defined
)

data class MessagePayload(
    val text: String,
    val timestamp: Long
)

data class PersonHint(
    val name: String,
    val relationships: List<String> = emptyList()
)

data class SummarizeMessagesRequest(
    val messages: List<MessagePayload>,
    val persons: List<PersonHint>
)

data class PersonInsightDTO(
    val name: String,
    val interests: List<String> = emptyList(),
    val avoid: List<String> = emptyList(),
    val sizes: String? = null,
    val notes: String? = null,
    val specialDates: List<String> = emptyList()
)

data class SummarizeMessagesResponse(
    val insights: List<PersonInsightDTO>
)

interface AIService {
    @POST("suggestions")
    suspend fun getSuggestions(@Body request: AIRequest): List<Gift>  // Returns list of suggested gifts

    @POST("summarize/messages")
    suspend fun summarizeMessages(@Body request: SummarizeMessagesRequest): SummarizeMessagesResponse
}

object AIApi {
    private const val BASE_URL = "https://your-ai-backend.com/api/"  // Replace with actual URL

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: AIService = retrofit.create(AIService::class.java)
} 