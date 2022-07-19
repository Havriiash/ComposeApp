package com.dvh.composetest.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dvh.composetest.ui.theme.ComposeTestTheme
import com.dvh.composetest.ui.viewmodels.ConferencesScreenViewModel
import com.dvh.composetest.ui.viewmodels.SharedViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTestTheme {
                val navController = rememberNavController()
                val viewModel: ConferencesScreenViewModel = viewModel()
                val sharedViewModel: SharedViewModel = viewModel()

                NavHost(navController = navController, startDestination = ConferencesScreen.ROUTE) {
                    composable(ConferencesScreen.ROUTE) {
                        ConferencesScreen(navController, viewModel, sharedViewModel)
                    }
                    composable(ConferenceDetailScreen.ROUTE) {
                        ConferenceDetailScreen(sharedViewModel.conference.value)
                    }
                }
            }
        }
    }
}