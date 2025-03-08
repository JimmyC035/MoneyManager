package com.example.moneymanager.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.presentation.model.Transaction
import com.example.moneymanager.presentation.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()
    
    val currentTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date())
        }

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            // 模擬數據加載
            val todayTransactions = listOf(
                Transaction(
                    id = "1",
                    title = "午餐",
                    amount = 45.0,
                    date = "12:30",
                    category = "餐飲",
                    type = TransactionType.EXPENSE
                ),
                Transaction(
                    id = "2",
                    title = "咖啡",
                    amount = 28.0,
                    date = "09:15",
                    category = "咖啡",
                    type = TransactionType.EXPENSE
                )
            )
            
            val yesterdayTransactions = listOf(
                Transaction(
                    id = "3",
                    title = "超市購物",
                    amount = 156.5,
                    date = "昨天, 18:45",
                    category = "購物",
                    type = TransactionType.EXPENSE
                ),
                Transaction(
                    id = "4",
                    title = "薪資",
                    amount = 8500.0,
                    date = "昨天, 10:00",
                    category = "收入",
                    type = TransactionType.INCOME
                )
            )
            
            _uiState.value = TransactionsUiState(
                todayTransactions = todayTransactions,
                yesterdayTransactions = yesterdayTransactions
            )
        }
    }
    
    fun filterTransactions(filter: String) {
        viewModelScope.launch {
            // 實際應用中，這裡會根據過濾條件從數據庫中獲取交易
            // 這裡只是簡單模擬
            val currentState = _uiState.value
            
            when (filter) {
                "收入" -> {
                    val filteredToday = currentState.todayTransactions.filter { it.type == TransactionType.INCOME }
                    val filteredYesterday = currentState.yesterdayTransactions.filter { it.type == TransactionType.INCOME }
                    _uiState.value = currentState.copy(
                        todayTransactions = filteredToday,
                        yesterdayTransactions = filteredYesterday
                    )
                }
                "支出" -> {
                    val filteredToday = currentState.todayTransactions.filter { it.type == TransactionType.EXPENSE }
                    val filteredYesterday = currentState.yesterdayTransactions.filter { it.type == TransactionType.EXPENSE }
                    _uiState.value = currentState.copy(
                        todayTransactions = filteredToday,
                        yesterdayTransactions = filteredYesterday
                    )
                }
                "餐飲" -> {
                    val filteredToday = currentState.todayTransactions.filter { it.category == "餐飲" }
                    val filteredYesterday = currentState.yesterdayTransactions.filter { it.category == "餐飲" }
                    _uiState.value = currentState.copy(
                        todayTransactions = filteredToday,
                        yesterdayTransactions = filteredYesterday
                    )
                }
                "購物" -> {
                    val filteredToday = currentState.todayTransactions.filter { it.category == "購物" }
                    val filteredYesterday = currentState.yesterdayTransactions.filter { it.category == "購物" }
                    _uiState.value = currentState.copy(
                        todayTransactions = filteredToday,
                        yesterdayTransactions = filteredYesterday
                    )
                }
                else -> {
                    // 重新加載所有交易
                    loadTransactions()
                }
            }
        }
    }
}

data class TransactionsUiState(
    val todayTransactions: List<Transaction> = emptyList(),
    val yesterdayTransactions: List<Transaction> = emptyList()
) 