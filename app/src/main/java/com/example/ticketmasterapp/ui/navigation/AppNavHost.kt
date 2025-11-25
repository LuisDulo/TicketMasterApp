package com.example.ticketmasterapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ticketmasterapp.auth.LoginScreen
import com.example.ticketmasterapp.auth.AuthViewModel
import com.example.ticketmasterapp.ui.screens.AddEventScreen
import com.example.ticketmasterapp.ui.screens.EventDetailsScreen
import com.example.ticketmasterapp.ui.screens.HomeScreen
import com.example.ticketmasterapp.viewmodel.EventViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    // Shared ViewModels
    val eventViewModel: EventViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // Track login state
    var isLoggedIn by remember { mutableStateOf(false) }

    // Decide start destination
    val startDestination = if (isLoggedIn) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {

        // LOGIN SCREEN
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    isLoggedIn = true
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // HOME SCREEN
        composable("home") {
            HomeScreen(
                viewModel = eventViewModel,
                navToAddEvent = { navController.navigate("addEvent") },
                navToEventDetails = { eventId ->
                    navController.navigate("eventDetails/$eventId")
                }
            )
        }

        // ADD EVENT SCREEN
        composable("addEvent") {
            AddEventScreen(
                viewModel = eventViewModel,
                onEventAdded = { navController.popBackStack() }
            )
        }

        // EVENT DETAILS SCREEN
        composable("eventDetails/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailsScreen(
                eventId = eventId,
                viewModel = eventViewModel
            )
        }
    }
}
