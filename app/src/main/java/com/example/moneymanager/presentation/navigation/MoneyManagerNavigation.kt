package com.example.moneymanager.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneymanager.presentation.screens.splash.SplashScreen
import com.example.moneymanager.presentation.screens.home.HomeScreen
import com.example.moneymanager.presentation.screens.transactions.TransactionsScreen
import com.example.moneymanager.presentation.screens.statistics.StatisticsScreen
import com.example.moneymanager.presentation.screens.loan.LoanScreen
import com.example.moneymanager.presentation.screens.settings.SettingsScreen

@Composable
fun MoneyManagerNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController = navController)
        }
        
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController = navController)
        }
        
        composable(Screen.Loan.route) {
            LoanScreen(navController = navController)
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
} 