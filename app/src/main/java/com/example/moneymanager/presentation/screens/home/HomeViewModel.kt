package com.example.moneymanager.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.data.local.dao.TransactionDao
import com.example.moneymanager.data.local.entity.TransactionEntity
import com.example.moneymanager.data.local.entity.TransactionType
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
    private val transactionDao: TransactionDao
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
            // 模擬數據加載
            val recentTransactions = listOf(
                TransactionEntity(
                    id = 1,
                    title = "午餐",
                    amount = 45.0,
                    date = Date(),
                    category = "餐飲",
                    type = TransactionType.EXPENSE.name
                ),
                TransactionEntity(
                    id = 3,
                    title = "超市購物",
                    amount = 156.5,
                    date = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000), // 昨天
                    category = "購物",
                    type = TransactionType.EXPENSE.name
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
            
            // 下面是從數據庫獲取數據的示例代碼，目前被注釋掉
            /*
            // 獲取收入總額
            transactionDao.getTotalIncome().collectLatest { income ->
                // 獲取支出總額
                transactionDao.getTotalExpense().collectLatest { expense ->
                    // 獲取最近的交易
                    transactionDao.getAllTransactions().collectLatest { entities ->
                        _uiState.value = HomeUiState(
                            totalBalance = (income ?: 0.0) - (expense ?: 0.0),
                            monthlyIncome = income ?: 0.0,
                            monthlyExpense = expense ?: 0.0,
                            recentTransactions = entities.take(5),
                            budgets = budgets
                        )
                    }
                }
            }
            */
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