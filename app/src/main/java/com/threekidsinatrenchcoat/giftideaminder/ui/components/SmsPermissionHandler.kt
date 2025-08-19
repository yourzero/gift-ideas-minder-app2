package com.threekidsinatrenchcoat.giftideaminder.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

@Composable
fun SmsPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            if (isGranted) {
                onPermissionGranted()
            } else {
                showPermissionDialog = true
                onPermissionDenied()
            }
        }
    )

    if (hasPermission) {
        content()
    } else {
        SmsPermissionRequestCard(
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.READ_SMS)
            }
        )
    }

    if (showPermissionDialog) {
        SmsPermissionDeniedDialog(
            onDismiss = { showPermissionDialog = false },
            onRetry = {
                showPermissionDialog = false
                permissionLauncher.launch(Manifest.permission.READ_SMS)
            }
        )
    }
}

@Composable
private fun SmsPermissionRequestCard(
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Filled.Message,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "SMS Analysis for Gift Ideas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                "Analyze your text conversations to discover gift ideas and preferences mentioned by your contacts.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        "Your messages stay private and are only processed locally",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    "• Find mentioned interests and hobbies\n" +
                    "• Discover items they want or need\n" +
                    "• Identify special dates and occasions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 32.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Allow SMS Access")
                }
            }
        }
    }
}

@Composable
private fun SmsPermissionDeniedDialog(
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("SMS Permission Required")
        },
        text = {
            Text(
                "To analyze your conversations for gift ideas, this app needs permission to read your SMS messages. " +
                "Your messages are processed locally and never shared."
            )
        },
        confirmButton = {
            Button(onClick = onRetry) {
                Text("Retry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Skip")
            }
        }
    )
}