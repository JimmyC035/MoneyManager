package com.example.moneymanager.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Transactions : Screen("transactions")
    object Statistics : Screen("statistics")
    object Loan : Screen("loan")
    object Settings : Screen("settings")
} 