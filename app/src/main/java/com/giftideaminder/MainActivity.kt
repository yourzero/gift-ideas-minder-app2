package com.giftideaminder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.giftideaminder.ui.navigation.AppScaffold
import com.giftideaminder.ui.navigation.Navigation
import com.giftideaminder.ui.screens.HomeDashboardScreen
import com.giftideaminder.ui.theme.GiftIdeaMinderTheme
//import com.giftideaminder.ui.screens.HomeDashboardGenerated
import com.giftideaminder.viewmodel.GiftViewModel
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

                val currentUserNameState =
                    viewModel.currentUserName.collectAsState(initial = "Guest")


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Navigation(viewModel, sharedText)
//                    HomeDashboardScreen(
//                        name = currentUserNameState.value,
//                        navController = navController
//                    )

                    AppScaffold(navController,
                        onFabClick = { navController.navigate("addGift") }
                    );

                    Navigation(
                        viewModel = viewModel,
                        sharedText = sharedText
                    )

                }
            }
        }
    }
} 