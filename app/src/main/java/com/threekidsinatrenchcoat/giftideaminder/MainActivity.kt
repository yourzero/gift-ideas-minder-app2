package com.threekidsinatrenchcoat.giftideaminder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.threekidsinatrenchcoat.giftideaminder.ui.navigation.AppScaffold
//import com.threekidsinatrenchcoat.giftideaminder.ui.navigation.Navigation
import com.threekidsinatrenchcoat.giftideaminder.ui.theme.GiftIdeaMinderTheme
//import com.threekidsinatrenchcoat.giftideaminder.ui.screens.HomeDashboardGenerated
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: GiftViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedText = intent?.getStringExtra(Intent.EXTRA_TEXT)

        setContent {
            GiftIdeaMinderTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScaffold(
                        navController = navController,
                        onFabClick = { navController.navigate("add_gift") }
                    );
                }
            }
        }
    }
} 