package com.threekidsinatrenchcoat.giftideaminder.data.repository

import com.threekidsinatrenchcoat.giftideaminder.data.api.AIRequest
import com.threekidsinatrenchcoat.giftideaminder.data.api.AIService
import com.threekidsinatrenchcoat.giftideaminder.data.dao.GiftDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.PersonDao
import com.threekidsinatrenchcoat.giftideaminder.data.dao.ImportantDateDao
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
    private val importantDateDao: ImportantDateDao,
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

    suspend fun fetchSuggestionsForPerson(personId: Int, perPerson: Int = 3): List<Gift> {
        // Backward-compatible hint: include a sentinel gift with personId set.
        val gifts = giftDao.getAllGifts().first()
        val persons = personDao.getAllPersons().first()
        val personHint = Gift(title = "__PERSON_HINT__", description = "focus", personId = personId)
        val response = aiService.getSuggestions(AIRequest(gifts = gifts + personHint, persons = persons))
        val dismissedKeys = dismissalDao.getAllDismissedKeys().first().toSet()
        val filtered = response.filterNot { buildSuggestionKey(it) in dismissedKeys }
            .dedupeAgainst(gifts)
            .filter { it.personId == null || it.personId == personId }
        return if (filtered.size <= perPerson) filtered else filtered.take(perPerson)
    }

    suspend fun fetchSuggestionsPersonCentric(topN: Int = 3, perPerson: Int = 3): List<Gift> {
        val gifts = giftDao.getAllGifts().first()
        val persons = personDao.getAllPersons().first()
        val dates = importantDateDao.getAll().first()

        val priority = PersonPriority.compute(persons = persons, gifts = gifts, importantDates = dates)
        val top = priority.take(topN)
        val per = top.flatMap { (personId, _) -> fetchSuggestionsForPerson(personId, perPerson) }
        val dismissedKeys = dismissalDao.getAllDismissedKeys().first().toSet()
        return per.filterNot { buildSuggestionKey(it) in dismissedKeys }.dedupeAgainst(gifts)
    }

    private object PersonPriority {
        fun compute(
            persons: List<Person>,
            gifts: List<Gift>,
            importantDates: List<com.threekidsinatrenchcoat.giftideaminder.data.model.ImportantDate>
        ): List<Pair<Int, Double>> {
            val now = java.time.LocalDate.now()

            val giftsByPerson: Map<Int, List<Gift>> = gifts.filter { it.personId != null }
                .groupBy { it.personId!! }

            val lastPurchasedDays: Map<Int, Int> = giftsByPerson.mapValues { (_, list) ->
                val last = list.filter { it.isPurchased && it.purchaseDate != null }
                    .maxByOrNull { it.purchaseDate!! }
                if (last?.purchaseDate != null) {
                    val days = ((System.currentTimeMillis() - last.purchaseDate!!) / (1000L * 60 * 60 * 24)).toInt()
                    days
                } else 365
            }

            val openGiftPenalty: Map<Int, Int> = giftsByPerson.mapValues { (_, list) ->
                list.count { !it.isPurchased }
            }

            val datesByPerson: Map<Int, List<java.time.LocalDate>> = importantDates.groupBy { it.personId }
                .mapValues { (_, d) -> d.map { it.date } }

            fun daysToNextDate(dates: List<java.time.LocalDate>): Int {
                if (dates.isEmpty()) return 365
                val upcoming = dates.minOf { d ->
                    val next = d.withYear(now.year).let { if (it.isBefore(now)) it.plusYears(1) else it }
                    java.time.temporal.ChronoUnit.DAYS.between(now, next).toInt()
                }
                return upcoming
            }

            fun relationshipWeight(p: Person): Double {
                val rels = p.relationships.map { it.lowercase() }
                return when {
                    rels.any { it.contains("spouse") || it.contains("partner") } -> 1.0
                    rels.any { it.contains("parent") || it.contains("child") || it.contains("son") || it.contains("daughter") } -> 0.8
                    rels.any { it.contains("sibling") } -> 0.7
                    rels.any { it.contains("friend") } -> 0.6
                    rels.any { it.contains("coworker") || it.contains("colleague") } -> 0.4
                    else -> 0.5
                }
            }

            return persons.map { p ->
                val nextDays = daysToNextDate(datesByPerson[p.id] ?: emptyList())
                val scoreEvent = kotlin.math.max(0.0, 1.0 - (kotlin.math.min(nextDays, 90).toDouble() / 90.0))
                val openCount = openGiftPenalty[p.id] ?: 0
                val scoreOpen = 1.0 - (kotlin.math.min(openCount, 3).toDouble() / 3.0)
                val lastDays = lastPurchasedDays[p.id] ?: 365
                val scoreLast = kotlin.math.min(lastDays.toDouble(), 365.0) / 365.0
                val relWeight = relationshipWeight(p)
                val score = 0.4 * scoreEvent + 0.3 * scoreOpen + 0.2 * scoreLast + 0.1 * relWeight
                p.id to score
            }.sortedByDescending { it.second }
        }
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

