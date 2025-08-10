package com.threekidsinatrenchcoat.giftideaminder.data.repository

import com.threekidsinatrenchcoat.giftideaminder.data.api.AIRequest
import com.threekidsinatrenchcoat.giftideaminder.data.api.AIService
import com.threekidsinatrenchcoat.giftideaminder.data.dao.GiftDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.PersonDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.SuggestionDismissalDao
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import com.threekidsinatrenchcoat.giftideaminder.data.model.SuggestionDismissal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AISuggestionRepository(
    private val aiService: AIService,
    private val giftDao: GiftDao,
    private val personDao: PersonDao,
    private val dismissalDao: SuggestionDismissalDao
) {

    suspend fun fetchSuggestions(): List<Gift> {
        val gifts: List<Gift> = giftDao.getAllGifts().first()
        val persons: List<Person> = personDao.getAllPersons().first()
        val response: List<Gift> = aiService.getSuggestions(AIRequest(gifts = gifts, persons = persons))
        val dismissedKeys: List<String> = dismissalDao.getAllDismissedKeys().first()
        return response.filterNot { gift ->
            val key = buildSuggestionKey(gift)
            dismissedKeys.contains(key)
        }.dedupeAgainst(gifts)
    }

    suspend fun dismissSuggestion(gift: Gift) {
        val key = buildSuggestionKey(gift)
        dismissalDao.insert(SuggestionDismissal(suggestionKey = key))
    }

    // Budget picker variant: in a real backend this would be a dedicated endpoint.
    // For now we reuse getSuggestions() and rely on server-side prompt/params.
    suspend fun fetchSuggestionsByBudget(min: Double, max: Double, personId: Int?): List<Gift> {
        val gifts: List<Gift> = giftDao.getAllGifts().first()
        val persons: List<Person> = personDao.getAllPersons().first()
        // Temporary: pass a synthetic gift as a hint in the request (backend interprets budget)
        val budgetHint = Gift(title = "__BUDGET_HINT__", description = "$min-$max", personId = personId)
        val response = aiService.getSuggestions(AIRequest(gifts = gifts + budgetHint, persons = persons))
        val dismissedKeys: List<String> = dismissalDao.getAllDismissedKeys().first()
        return response.filterNot { gift ->
            val key = buildSuggestionKey(gift)
            dismissedKeys.contains(key)
        }.dedupeAgainst(gifts)
    }

    private fun List<Gift>.dedupeAgainst(existing: List<Gift>): List<Gift> {
        val existingKeys = existing.map { buildSuggestionKey(it) }.toSet()
        return this.filter { buildSuggestionKey(it) !in existingKeys }
    }

    private fun buildSuggestionKey(gift: Gift): String {
        val title = gift.title.trim().lowercase()
        val url = gift.url?.trim()?.lowercase().orEmpty()
        return if (url.isNotEmpty()) "url:$url" else "title:$title"
    }
}

