package com.giftideaminder
//package com.threekidsinatrenchcoat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard   // same path, but backed by M3 artifact
import androidx.compose.material.icons.filled.Event

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.giftideaminder.viewmodel.GiftViewModel
import java.util.Calendar

// Simple data model for events
// Simple data model for events
data class GiftEvent(val title: String, val subtitle: String)

// Pastel & brand colors
private val BgPink       = Color(0xFFFFEBEE)
private val StatsYellow  = Color(0xFFFFF9C4)
private val StatsBrown   = Color(0xFF6D4C41)
private val StatsPurple  = Color(0xFFE1BEE7)
private val StatsOlive   = Color(0xFFCDDC39)
private val NavBarBg     = Color(0xFFDCEDC8)
private val FabPeach     = Color(0xFFFFAB91)
private val TextDark     = Color(0xFF5D4037)

@Composable
fun HomeDashboardScreen(name: String,
                        navController: NavController,
                        viewModel: GiftViewModel = hiltViewModel()
) {
    // 1) Collect your gifts
    val gifts by viewModel.allGifts.collectAsState(initial = emptyList())

    // 2) Compute timestamps
    val now = remember { System.currentTimeMillis() }
    val weekMillis = 7L * 24 * 60 * 60 * 1000

    // 3) Compute stats
    val upcomingCount = gifts.count {
        it.eventDate != null &&
                it.eventDate!! > now &&
                it.eventDate!! <= now + weekMillis
    }
    val pendingCount = gifts.count { !it.isPurchased }
    val sentThisMonth = gifts.count { gift ->
        gift.isPurchased &&
                gift.purchaseDate?.let { sameMonth(now, it) } == true
    }
    val missedCount = gifts.count {
        it.eventDate != null &&
                it.eventDate!! < now &&
                !it.isPurchased
    }


//    val stats = listOf(
//        Triple("Upcoming Gifts",     "5 in the next week",  StatsYellow),
//        Triple("Pending Purchases",  "3 awaiting action",  StatsBrown),
//        Triple("Gift Sent This Month","12 gifts",           StatsPurple),
//        Triple("Missed Events",      "2 events missed",     StatsOlive)
//    )
//    val upcomingEvents = listOf(
//        GiftEvent("Mom's Birthday",     "Send flowers"),
//        GiftEvent("Anniversary Dinner", "Book a table"),
//        GiftEvent("Friend's Wedding",   "Buy a gift card")
//    )

    val stats = listOf(
        Triple("Upcoming Gifts",     "$upcomingCount in next 7d",    StatsYellow),
        Triple("Pending Purchases",  "$pendingCount awaiting",      StatsBrown),
        Triple("Sent This Month",    "$sentThisMonth gifts",        StatsPurple),
        Triple("Missed Events",      "$missedCount missed",         StatsOlive)
    )

    // 4) Build upcoming event list
    val upcomingEvents = gifts
        .filter { it.eventDate != null && it.eventDate!! > now }
        .sortedBy { it.eventDate }
        .map { GiftEvent(it.title, it.description ?: "") }

    Scaffold(
        containerColor = BgPink,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = FabPeach,
                modifier = Modifier.offset(y = (-100).dp)
            ) { Icon(Icons.Filled.Add, contentDescription = "Add Gift") }
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { (title, subtitle, bg) ->
                            StatCard(title, subtitle, bg, Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Upcoming events
                Text(
                    text = "Upcoming Gift Events",
                    fontSize = 20.sp,
                    color = TextDark,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(upcomingEvents) { event ->
                        EventCard(event)
                    }
                }
            }

            // Floating, pill-shaped bottom nav
            NavigationBar(
                containerColor = NavBarBg,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(60.dp)
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    selected = true, onClick = { /* TODO */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.CardGiftcard, contentDescription = "Gifts") },
                    selected = false, onClick = { /* TODO */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Event, contentDescription = "Events") },
                    selected = false, onClick = { /* TODO */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "People") },
                    selected = false, onClick = {
navController.navigate("person_list") {
    // optional: popUpTo/saveState so you don’t stack lots of copies
    launchSingleTop = true
    restoreState = true
}

                    }
                )
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
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = TextDark)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = TextDark)
        }
    }
}

@Composable
private fun EventCard(event: GiftEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = event.title, style = MaterialTheme.typography.bodyLarge, color = TextDark)
                Text(text = event.subtitle, style = MaterialTheme.typography.bodyMedium, color = TextDark)
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Filled.Event, contentDescription = "Open Event")
            }
        }
    }
}

private fun sameMonth(ts1: Long, ts2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = ts1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = ts2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
            && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
}

@Preview(showBackground = true)
@Composable
fun HomeDashboardFloatingNavPreview() {
    val navController = rememberNavController()
    HomeDashboardScreen(
        name = "Demo",
        navController = navController
    )
}