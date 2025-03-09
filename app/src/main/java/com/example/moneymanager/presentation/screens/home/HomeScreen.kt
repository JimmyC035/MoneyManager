package com.example.moneymanager.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.R
import com.example.moneymanager.presentation.components.AddFloatingActionButton
import com.example.moneymanager.presentation.components.BottomNavBar
import com.example.moneymanager.presentation.components.recentTransactionsSection
import com.example.moneymanager.presentation.navigation.Screen
import com.example.moneymanager.presentation.screens.expense.AddExpenseDialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.res.stringResource
import com.example.moneymanager.data.local.entity.TransactionEntity
import com.example.moneymanager.presentation.components.AssetCard
import com.example.moneymanager.presentation.components.BudgetCard

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onSave = { showAddExpenseDialog = false }
        )
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = { BottomNavBar(navController = navController, currentRoute = Screen.Home.route) },
        floatingActionButton = {
            AddFloatingActionButton(
                onClick = { showAddExpenseDialog = true }
            )
        }
    ) { paddingValues ->
        HomeScreenBody(
            paddingValues = paddingValues,
            uiState = uiState,
            onDeleteTransaction = { viewModel.deleteTransaction(it) }
        )
    }
}

@Composable
private fun HomeScreenBody(
    paddingValues: PaddingValues,
    uiState: HomeUiState,
    onDeleteTransaction: (TransactionEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .windowInsetsPadding(WindowInsets.statusBars),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // 標題
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        
        item {
            // 總資產卡片
            AssetCard(
                totalBalance = uiState.totalBalance,
                monthlyIncome = uiState.monthlyIncome,
                monthlyExpense = uiState.monthlyExpense
            )
        }
        
        // 最近交易區塊
        recentTransactionsSection(
            transactions = uiState.recentTransactions,
            onDeleteTransaction = onDeleteTransaction
        )
        
        item {
            BudgetCard(budgets = uiState.budgets)
        }
        
        // 底部空間
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
} 