package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.data.model.PriceRecord
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftDetailScreen(
    giftId: Int,
    viewModel: GiftViewModel = hiltViewModel(),
    navController: NavController
) {
    val giftWithHistory = viewModel
        .getGiftWithHistoryById(giftId)
        .collectAsState(initial = null)
        .value
    
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gift Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        if (giftWithHistory == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading gift details...")
            }
        } else {
            val gift = giftWithHistory.gift
            val history: List<PriceRecord> = giftWithHistory.currentPriceHistory

            val personViewModel: PersonViewModel = hiltViewModel()
            val persons = personViewModel.allPersons.collectAsState(initial = emptyList()).value
            val personName = persons.find { it.id == gift.personId }?.name ?: "Unassigned"

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Main Gift Info Card
                    ElevatedCard(
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = gift.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            if (!gift.description.isNullOrBlank()) {
                                Text(
                                    text = gift.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            // Product Link
                            if (!gift.url.isNullOrBlank()) {
                                ElevatedCard(
                                    onClick = {
                                        try {
                                            uriHandler.openUri(gift.url)
                                        } catch (e: Exception) {
                                            // Handle URL opening error silently
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "View Product Page",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Icon(
                                            Icons.Filled.Launch,
                                            contentDescription = "Open link",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                            
                            Text(
                                text = "For: $personName",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                item {
                    // Price & Details Card
                    ElevatedCard {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Price & Details",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Original Price", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        "$${gift.originalPrice ?: "N/A"}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Column {
                                    Text("Current Price", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        gift.currentPrice?.let { "$$it" } ?: "Not fetched",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            if (gift.eventDate != null) {
                                val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    .format(Date(gift.eventDate))
                                Text("Event Date: $date")
                            }
                            
                            OutlinedTextField(
                                value = gift.budget?.toString() ?: "",
                                onValueChange = { newBudget ->
                                    viewModel.updateGift(gift.copy(budget = newBudget.toDoubleOrNull()))
                                },
                                label = { Text("Budget") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = gift.isPurchased,
                                    onCheckedChange = { isPurchased ->
                                        viewModel.updateGift(gift.copy(isPurchased = isPurchased))
                                    }
                                )
                                Text("Purchased", modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
                
                if (history.isNotEmpty()) {
                    item {
                        ElevatedCard {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Price History",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                
                                history.forEach { record ->
                                    val date = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                                        .format(Date(record.timestamp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(date)
                                        Text("$${record.price}")
                                    }
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
                
                item {
                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate("edit_gift/$giftId") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit")
                        }
                        
                        Button(
                            onClick = {
                                viewModel.deleteGift(gift)
                                navController.popBackStack()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete")
                        }
                    }
                }
                
                item {
                    Button(
                        onClick = { viewModel.updatePriceForGift(gift) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Price")
                    }
                }
            }
        }
    }
}

