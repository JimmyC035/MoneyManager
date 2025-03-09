package com.example.moneymanager.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.data.local.dao.TransactionDao
import com.example.moneymanager.data.local.entity.TransactionEntity
import com.example.moneymanager.data.local.entity.TransactionType
import com.example.moneymanager.data.repository.TransactionRepository
import com.example.moneymanager.util.BudgetMock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

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
            // 模擬預算數據
            val budgets = listOf(
                BudgetMock("餐飲", 1500.0, 850.0),
                BudgetMock("購物", 2000.0, 1200.0),
                BudgetMock("交通", 800.0, 350.0)
            )
            
            // 從數據庫獲取收入總額
            transactionRepository.getTotalIncome().collectLatest { income ->
                // 從數據庫獲取支出總額
                transactionRepository.getTotalExpense().collectLatest { expense ->
                    // 從數據庫獲取最近的交易
                    transactionRepository.allTransactions.collectLatest { transactions ->
                        _uiState.value = HomeUiState(
                            totalBalance = (income ?: 0.0) - (expense ?: 0.0),
                            monthlyIncome = income ?: 0.0,
                            monthlyExpense = expense ?: 0.0,
                            recentTransactions = transactions.take(5),
                            budgets = budgets
                        )
                    }
                }
            }
        }
    }
    
    // 刪除交易
    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
            // 刪除後數據會自動更新，因為我們使用了 Flow
        }
    }
}

data class HomeUiState(
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val budgets: List<BudgetMock> = emptyList()
) 