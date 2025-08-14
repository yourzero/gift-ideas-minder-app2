package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import com.threekidsinatrenchcoat.giftideaminder.utils.SoundUtils

@Composable
fun LoadingMessages(
    personNames: List<String> = emptyList(),
    showDebugPrompt: Boolean = false,
    aiPrompt: String = "",
    onLoadingComplete: () -> Unit = {}
) {
    var currentMessages by remember { mutableStateOf(emptyList<String>()) }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    
    val pseudoWorkMessages = remember(personNames) {
        buildList {
            // Real work messages with person names
            if (personNames.isNotEmpty()) {
                add("Searching for gift ideas for: ${personNames.joinToString(", ")}")
                personNames.forEach { name ->
                    add("Analyzing preferences for $name...")
                    add("Finding personalized gifts for $name...")
                }
            }
            
            // Pseudo-work messages
            addAll(listOf(
                "Connecting to AI service...",
                "Initializing gift recommendation engine...",
                "Scanning trending gift categories...",
                "Cross-referencing price databases...",
                "Evaluating gift compatibility scores...",
                "Processing relationship context...",
                "Analyzing seasonal preferences...",
                "Filtering by budget constraints...",
                "Checking inventory availability...",
                "Calculating surprise factor ratings...",
                "Optimizing for maximum delight...",
                "Finalizing personalized recommendations..."
            ))
        }
    }
    
    val debugMessages = remember(aiPrompt) {
        if (aiPrompt.isNotEmpty()) {
            aiPrompt.split("\n").filter { it.isNotBlank() }
        } else {
            listOf("No AI prompt available for debugging")
        }
    }
    
    val messagesToShow = if (showDebugPrompt) debugMessages else pseudoWorkMessages
    
    LaunchedEffect(messagesToShow) {
        currentMessages = emptyList()
        
        messagesToShow.forEachIndexed { index, message ->
            delay(if (showDebugPrompt) 200 else 800) // Faster for debug mode
            currentMessages = currentMessages + message
            
            // Auto-scroll to bottom
            listState.animateScrollToItem(currentMessages.size - 1)
        }
        
        // Wait a bit then complete with sound
        delay(500)
        SoundUtils.playSuccessSound(context)
        onLoadingComplete()
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (showDebugPrompt) "AI Prompt Debug Mode" else "Generating Suggestions...",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(currentMessages.size) { index ->
                    Text(
                        text = currentMessages[index],
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}