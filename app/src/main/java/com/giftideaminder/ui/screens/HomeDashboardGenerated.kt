package com.threekidsinatrenchcoat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

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
fun HomeDashboardScreen(name: String) {
    val stats = listOf(
        Triple("Upcoming Gifts",    "5 in the next week",  StatsYellow),
        Triple("Pending Purchases", "3 awaiting action",  StatsBrown),
        Triple("Gift Sent This Month","12 gifts",          StatsPurple),
        Triple("Missed Events",     "2 events missed",     StatsOlive)
    )
    val upcomingEvents = listOf(
        GiftEvent("Mom's Birthday",     "Send flowers"),
        GiftEvent("Anniversary Dinner", "Book a table"),
        GiftEvent("Friend's Wedding",   "Buy a gift card")
    )

    Scaffold(
        containerColor = BgPink,
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO */ }, containerColor = FabPeach) {
                Icon(Icons.Default.Add, contentDescription = "Add Gift")
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavBarBg,
                tonalElevation = 4.dp,
                modifier = Modifier.height(56.dp)
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home,   contentDescription = "Home") },
                    selected = true, onClick = { /*TODO*/ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CardGiftcard, contentDescription = "Gifts") },
                    selected = false, onClick = { /*TODO*/ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Event, contentDescription = "Events") },
                    selected = false, onClick = { /*TODO*/ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    selected = false, onClick = { /*TODO*/ }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

            // Stats 2×2 grid
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

            // Upcoming Events header
            Text(
                text = "Upcoming Gift Events",
                fontSize = 20.sp,
                color = TextDark,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            // List of events
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(upcomingEvents) { event ->
                    EventCard(event)
                }
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
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
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
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium,
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
                Text(
                    event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDark
                )
                Text(
                    event.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDark
                )
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Event, contentDescription = "Open Event")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeDashboardPreview() {
    HomeDashboardScreen(name = "Krissy")
}
