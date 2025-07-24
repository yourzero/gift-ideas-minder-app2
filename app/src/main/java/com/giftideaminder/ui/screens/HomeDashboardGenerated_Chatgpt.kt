//package com.giftideaminder.ui.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.tooling.preview.Preview
//
//// Sample data class for events
//data class GiftEvent(val title: String, val subtitle: String)
//
//// Pastel color definitions
//private val BackgroundColor = Color(0xFFFFEBEE)    // light pink
//private val CardYellow     = Color(0xFFFFF9C4)    // pastel yellow
//private val CardBrown      = Color(0xFF6D4C41)    // brown
//private val CardPurple     = Color(0xFFE1BEE7)    // pastel purple
//private val CardOlive      = Color(0xFFCDDC39)    // pastel green
//private val NavBackground  = Color(0xFFDCEDC8)    // light green
//private val FabColor       = Color(0xFFFFAB91)    // pastel peach
//
//@Composable
//fun HomeScreen(name: String) {
//    val stats = listOf(
//        Triple("Upcoming Gifts", "5 in the next week", CardYellow),
//        Triple("Pending Purchases", "3 awaiting action", CardBrown),
//        Triple("Gift Sent This Month", "12 gifts", CardPurple),
//        Triple("Missed Events", "2 events missed", CardOlive)
//    )
//    val upcomingEvents = listOf(
//        GiftEvent("Mom's Birthday",    "Send flowers"),
//        GiftEvent("Anniversary Dinner","Book a table"),
//        GiftEvent("Friend's Wedding",  "Buy a gift card")
//    )
//
//    Scaffold(
//        backgroundColor = BackgroundColor,
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { /* TODO: Add new gift */ },
//                backgroundColor = FabColor
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add Gift")
//            }
//        },
//        bottomBar = {
//            BottomNavigation(
//                backgroundColor = NavBackground,
//                elevation = 8.dp,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp)
//            ) {
//                BottomNavigationItem(
//                    icon = { Icon(Icons.Default.Home,   contentDescription = "Home") },
//                    selected = true, onClick = { /*TODO*/ })
//                BottomNavigationItem(
//                    icon = { Icon(Icons.Default.CardGiftcard, contentDescription = "Gifts") },
//                    selected = false, onClick = { /*TODO*/ })
//                BottomNavigationItem(
//                    icon = { Icon(Icons.Default.Event, contentDescription = "Events") },
//                    selected = false, onClick = { /*TODO*/ })
//                BottomNavigationItem(
//                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
//                    selected = false, onClick = { /*TODO*/ })
//            }
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(16.dp)
//        ) {
//            // Greeting
//            Text(
//                text = "HELLO, ${name.uppercase()}!",
//                fontSize = 28.sp,
//                fontWeight = FontWeight.Light,
//                color = Color(0xFF5D4037)  // dark brown
//            )
//            Spacer(Modifier.height(24.dp))
//
//            // Stats grid (2x2)
//            for (row in stats.chunked(2)) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    row.forEach { (title, subtitle, bg) ->
//                        StatCard(title, subtitle, bg, Modifier.weight(1f))
//                    }
//                }
//                Spacer(Modifier.height(16.dp))
//            }
//
//            // Section title
//            Text(
//                text = "Upcoming Gift Events",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Medium,
//                color = Color(0xFF5D4037)
//            )
//            Spacer(Modifier.height(8.dp))
//
//            // Event list
//            LazyColumn(
//                modifier = Modifier.fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(upcomingEvents) { event ->
//                    EventCard(event)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun StatCard(
//    title: String,
//    subtitle: String,
//    background: Color,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        backgroundColor = background,
//        shape = MaterialTheme.shapes.medium,
//        elevation = 4.dp,
//        modifier = modifier
//            .height(100.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(12.dp),
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF3E2723))
//            Text(subtitle, fontSize = 14.sp, color = Color(0xFF3E2723))
//        }
//    }
//}
//
//@Composable
//private fun EventCard(event: GiftEvent) {
//    Card(
//        backgroundColor = Color.White,
//        shape = MaterialTheme.shapes.medium,
//        elevation = 2.dp,
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(56.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column {
//                Text(event.title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF5D4037))
//                Text(event.subtitle, fontSize = 14.sp, color = Color(0xFF6D4C41))
//            }
//            IconButton(onClick = { /* TODO: event clicked */ }) {
//                Icon(Icons.Default.ChevronRight, contentDescription = "Open Event")
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    HomeScreen(name = "Krissy")
//}
