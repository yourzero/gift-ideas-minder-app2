package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppTopBar(title: String) {
    CenterAlignedTopAppBar(
        modifier = Modifier.height(48.dp),
        title = {
            Box(
                modifier = Modifier.height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF546E7A),
            titleContentColor = Color.White
        )
    )
}
