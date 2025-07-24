package com.giftideaminder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.giftideaminder.data.model.Gift


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 640)@Preview
@Composable
fun DashboardScreen(
    gifts: List<Gift> = emptyList(),
    onSearch: (String) -> Unit = {},
    onAddGift: () -> Unit = {},
    onGiftClick: (Gift) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val filterTags = listOf("Birthday", "Christmas", "Sale")
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gift Idea Minder") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .height(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddGift,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                //Icon(Icons.Default.Add, contentDescription = "Add Gift")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { /* Icon(Icons.Filled.Person, contentDescription = "People") */ },
                    label = { Text("People") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { /* Icon(Icons.Filled.Search, contentDescription = "Suggestions") */ },
                    label = { Text("Suggestions") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { /* Icon(Icons.Filled.Settings, contentDescription = "Settings") */ },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearch(it.text)
                },
                leadingIcon = { },
                placeholder = { Text("Search gifts…") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                filterTags.forEach { tag ->
                    FilterChip(
                        selected = selectedFilter == tag,
                        onClick = {
                            selectedFilter = if (selectedFilter == tag) null else tag
                        },
                        label = { Text(tag) }
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* navigate to OCR capture */ },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    //Icon(
                    //    Icons.Filled.CameraAlt,
                    //    contentDescription = "OCR Capture",
                    //    tint = MaterialTheme.colorScheme.secondary
                    //)
                    Spacer(Modifier.width(8.dp))
                    Text("Capture Gift via OCR")
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(gifts) { gift ->
                    GiftCard(gift = gift, onClick = { onGiftClick(gift) })
                }
            }
        }
    }
}
@Preview
@Composable
fun GiftCard(gift: Gift, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(gift.title, style = MaterialTheme.typography.titleMedium)
            gift.description?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = gift.eventDate?.let { java.text.SimpleDateFormat("MMM d, yyyy").format(java.util.Date(it)) }
                        ?: "No Date",
                    style = MaterialTheme.typography.bodySmall
                )
                gift.personId?.let { id ->
                    Text("Assigned to: $id", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
