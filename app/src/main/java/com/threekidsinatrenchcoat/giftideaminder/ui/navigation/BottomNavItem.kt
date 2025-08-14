//package com.threekidsinatrenchcoat.giftideaminder.ui.navigation
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Icon
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.NavigationBarItemDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.NavigationBar
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.unit.dp
//
///**
// * Must be called inside a NavigationBar {} only.
// */
//@Composable
//fun BottomNavItem(
//    icon: ImageVector,
//    route: String,
//    contentDescription: String,
//    currentRoute: String?,
//    onNavigate: (String) -> Unit
//) : NavigationBarItem
//
//{
//    val selected = currentRoute == route
//
//
//    // This MUST be called inside a NavigationBar {}.
//    NavigationBarItem(
//        selected = selected,
//        onClick = { onNavigate(route) },
//        icon = {
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier
//                    .size(56.dp)
//                    .clip(RoundedCornerShape(28.dp))
//                    .background(
//                        color = if (selected) Color.White else Color.Transparent
//                    )
//            ) {
//                Icon(
//                    imageVector = icon,
//                    contentDescription = contentDescription,
//                    tint = if (selected)
//                        MaterialTheme.colorScheme.primary
//                    else
//                        MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//        },
//        colors = NavigationBarItemDefaults.colors(
//            indicatorColor = Color.Transparent, // we draw our own highlight
//            selectedIconColor = Color.Unspecified,
//            unselectedIconColor = Color.Unspecified
//        )
//    )
//}
