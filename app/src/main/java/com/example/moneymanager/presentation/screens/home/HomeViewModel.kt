package com.example.moneymanager.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.presentation.model.Transaction
import com.example.moneymanager.presentation.model.TransactionType
import com.example.moneymanager.util.BudgetMock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    val currentTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date())
        }

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            // 模擬數據加載
            val recentTransactions = listOf(
                Transaction(
                    id = "1",
                    title = "午餐",
                    amount = 45.0,
                    date = "今天, 12:30",
                    category = "餐飲",
                    type = TransactionType.EXPENSE
                ),
                Transaction(
                    id = "3",
                    title = "超市購物",
                    amount = 156.5,
                    date = "昨天, 18:45",
                    category = "購物",
                    type = TransactionType.EXPENSE
                )
            )
            
            val budgets = listOf(
                BudgetMock("餐飲", 1500.0, 850.0),
                BudgetMock("購物", 2000.0, 1200.0),
                BudgetMock("交通", 800.0, 350.0)
            )
            
            _uiState.value = HomeUiState(
                totalBalance = 25680.42,
                monthlyIncome = 8500.0,
                monthlyExpense = 3245.75,
                recentTransactions = recentTransactions,
                budgets = budgets
            )
        }
    }
}

data class HomeUiState(
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val budgets: List<BudgetMock> = emptyList()
) 