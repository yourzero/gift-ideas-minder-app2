package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.threekidsinatrenchcoat.giftideaminder.ui.components.AssistShelf
import com.threekidsinatrenchcoat.giftideaminder.ui.components.FilterChipsRow
import com.threekidsinatrenchcoat.giftideaminder.ui.components.GiftCard
import com.threekidsinatrenchcoat.giftideaminder.ui.components.ScopeBanner
import com.threekidsinatrenchcoat.giftideaminder.ui.state.*

@Composable
fun GiftsScreen(
    state: GiftsUiState,
    onUpdateFilters: (GiftFilters) -> Unit,
    onResetFilters: () -> Unit,
    onSelectTab: (GiftTab) -> Unit,
    onSelectGrouping: (GiftGrouping) -> Unit,
    onAddGift: () -> Unit,
    onSuggest: () -> Unit,
    onOpenGift: (Long) -> Unit,
    onOpenLink: (Long) -> Unit,
    onToggleShortlist: (Long) -> Unit,
    onMarkPurchased: (Long) -> Unit,
    onAssistAction: (AssistItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gifts") },
                actions = {
                    IconButton(onClick = { /* TODO search */ }) { Icon(Icons.Default.Search, null) }
                    IconButton(onClick = { /* TODO filter sheet */ }) { Icon(Icons.Default.Tune, null) }
                    IconButton(onClick = { /* TODO overflow */ }) { Icon(Icons.Default.MoreVert, null) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGift) {
                Icon(Icons.Default.Add, contentDescription = "Add gift")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            FilterChipsRow(
                filters = state.filters,
                onChange = onUpdateFilters,
                onReset = onResetFilters
            )

            if (state.scope != null) {
                ScopeBanner(
                    scope = state.scope,
                    budget = state.budgetSummary,
                    onAdd = onAddGift,
                    onSuggest = onSuggest,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            GiftsTabsRow(
                selected = state.selectedTab,
                onSelect = onSelectTab
            )

            GroupingToggleRow(
                selected = state.grouping,
                onSelect = onSelectGrouping
            )

            if (state.assists.isNotEmpty()) {
                AssistShelf(items = state.assists, onAction = onAssistAction)
            }

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                state.groups.forEach { group ->
                    // Non-sticky header fallback for broad Compose compatibility
                    if (!group.header.isNullOrBlank()) {
                        item {
                            Surface(color = MaterialTheme.colorScheme.surface) {
                                Text(
                                    group.header,
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                    items(group.items, key = { it.id }) { item ->
                        GiftCard(
                            item = item,
                            onOpen = { onOpenGift(item.id) },
                            onOpenLink = { onOpenLink(item.id) },
                            onToggleShortlist = { onToggleShortlist(item.id) },
                            onMarkPurchased = { onMarkPurchased(item.id) },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GiftsTabsRow(selected: GiftTab, onSelect: (GiftTab) -> Unit) {
    TabRow(selectedTabIndex = selected.ordinal) {
        GiftTab.values().forEachIndexed { index, tab ->
            Tab(
                selected = index == selected.ordinal,
                onClick = { onSelect(tab) },
                text = { Text(tab.name) }
            )
        }
    }
}

@Composable
private fun GroupingToggleRow(selected: GiftGrouping, onSelect: (GiftGrouping) -> Unit) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        GiftGrouping.values().forEachIndexed { i, g ->
            SegmentedButton(
                selected = selected == g,
                onClick = { onSelect(g) },
                shape = SegmentedButtonDefaults.itemShape(i, GiftGrouping.values().size),
                label = { Text(g.name) }
            )
        }
    }
}


