package com.threekidsinatrenchcoat.giftideaminder.ui.state

import androidx.compose.runtime.Immutable

@Immutable
data class GiftsUiState(
    val filters: GiftFilters = GiftFilters(),
    val selectedTab: GiftTab = GiftTab.All,
    val grouping: GiftGrouping = GiftGrouping.None,
    val scope: GiftScope? = null,
    val budgetSummary: BudgetSummary? = null,
    val assists: List<AssistItem> = emptyList(),
    val groups: List<GiftGroup> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@Immutable
data class GiftFilters(
    val persons: List<String> = emptyList(),
    val events: List<String> = emptyList(),
    val statuses: Set<GiftStatus> = emptySet(),
    val stores: List<String> = emptyList(),
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val tags: List<String> = emptyList(),
    val query: String = ""
)

enum class GiftTab { All, Ideas, Shortlist, Purchased, Archived }

enum class GiftGrouping { None, Person, Event }

sealed interface GiftScope {
    data class Person(val name: String) : GiftScope
    data class Event(val name: String) : GiftScope
}

@Immutable
data class BudgetSummary(
    val spent: Double,
    val budget: Double,
    val nextLabel: String? = null,
    val nextCountdownDays: Int? = null
)

enum class GiftStatus { IDEA, SHORTLIST, PURCHASED, ARCHIVED }

@Immutable
data class GiftGroup(
    val header: String?,
    val items: List<GiftUi>
)

@Immutable
data class GiftUi(
    val id: Long,
    val title: String,
    val thumbnailUrl: String? = null,
    val persons: List<String> = emptyList(),
    val events: List<String> = emptyList(),
    val currentPrice: Double? = null,
    val targetPrice: Double? = null,
    val onSale: Boolean = false,
    val status: GiftStatus = GiftStatus.IDEA,
    val linkUrl: String? = null
)

@Immutable
data class AssistItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val ctaLabel: String = "Add",
    val payload: String? = null
)
