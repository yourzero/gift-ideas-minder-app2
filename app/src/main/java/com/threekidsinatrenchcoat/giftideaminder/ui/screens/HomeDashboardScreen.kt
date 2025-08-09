// app/src/main/java/com/com.threekidsinatrenchcoat.giftideaminder/ui/screens/HomeDashboardScreen.kt
package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.ui.theme.*
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import java.util.Calendar
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift

// Simple data class for display
data class GiftEvent(val title: String, val subtitle: String)

@Composable
fun HomeDashboardScreen(
    name: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: GiftViewModel = hiltViewModel()
) {
    // 1) Collect your gifts
    val gifts by viewModel.allGifts.collectAsState(initial = emptyList<Gift>())

    // 2) Compute time boundaries
    val now = remember { System.currentTimeMillis() }
    val weekMillis = 7L * 24 * 60 * 60 * 1000

    // 3) Compute stats
    val upcomingCount: Int = gifts.count {
        it.eventDate != null &&
                it.eventDate!! in (now + 1)..(now + weekMillis)
    }
    val pendingCount: Int = gifts.count { it.isPurchased.not() }
    val sentThisMonth: Int = gifts.count { gift ->
        gift.isPurchased &&
                gift.purchaseDate != null &&
                sameMonth(now, gift.purchaseDate!!)
    }
    val missedCount: Int = gifts.count {
        it.eventDate != null &&
                it.eventDate!! < now &&
                it.isPurchased.not()
    }

    val stats = listOf(
        Triple("Upcoming Gifts",    "$upcomingCount in next 7d", StatsYellow),
        Triple("Pending Purchases", "$pendingCount awaiting",    StatsBrown),
        Triple("Sent This Month",   "$sentThisMonth gifts",      StatsPurple),
        Triple("Missed Events",     "$missedCount missed",       StatsOlive)
    )

    // 4) Build upcoming events list
    val upcomingGifts: List<Gift> = gifts
        .filter { it.eventDate != null && it.eventDate!! > now }
    val sortedUpcoming: List<Gift> = upcomingGifts.sortedBy { it.eventDate }
    val upcomingEvents: List<GiftEvent> = sortedUpcoming.map { GiftEvent(it.title, it.description ?: "") }

    // 5) Render content only (outer Scaffold is in AppScaffold)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Greeting
        Text(
            text = "HELLO, ${name.uppercase()}!",
            fontSize = 28.sp,
            color = TextDark,
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(Modifier.height(24.dp))

        // Stats grid
        stats.chunked(2).forEach { row ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { (title, subtitle, bg) ->
                    StatCard(title, subtitle, bg, Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Upcoming Gifts header
        Text(
            text = "Upcoming Gift Events",
            fontSize = 20.sp,
            color = TextDark,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(upcomingEvents) { ev ->
                EventCard(ev)
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    subtitle: String,
    background: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(100.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = TextDark)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextDark)
        }
    }
}

@Composable
private fun EventCard(event: GiftEvent) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(event.title, style = MaterialTheme.typography.bodyLarge, color = TextDark)
                Text(event.subtitle, style = MaterialTheme.typography.bodyMedium, color = TextDark)
            }
            IconButton(onClick = { /* navigate to event detail */ }) {
                Icon(Icons.Filled.Event, contentDescription = "Open Event")
            }
        }
    }
}

/** Returns true if both timestamps fall in the same calendar year & month */
private fun sameMonth(ts1: Long, ts2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = ts1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = ts2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
}

@Preview(showBackground = true)
@Composable
fun HomeDashboardPreview() {
    HomeDashboardScreen(
        name = "Demo User",
        navController = rememberNavController(),
        modifier = Modifier.padding(16.dp) // mimic outer scaffold padding
    )
}
