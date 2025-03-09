package com.example.moneymanager.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.data.local.dao.TransactionDao
import com.example.moneymanager.data.local.entity.TransactionEntity
import com.example.moneymanager.data.local.entity.TransactionType
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
class TransactionsViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {

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
            // 在實際應用中，這裡應該從 transactionDao 獲取數據
            // 目前仍使用模擬數據
            val todayTransactions = listOf(
                TransactionEntity(
                    id = 1,
                    title = "午餐",
                    amount = 45.0,
                    date = Date(),
                    category = "餐飲",
                    type = TransactionType.EXPENSE.name
                ),
                TransactionEntity(
                    id = 2,
                    title = "咖啡",
                    amount = 28.0,
                    date = Date(),
                    category = "咖啡",
                    type = TransactionType.EXPENSE.name
                )
            )
            
            val yesterdayTransactions = listOf(
                TransactionEntity(
                    id = 3,
                    title = "超市購物",
                    amount = 156.5,
                    date = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000), // 昨天
                    category = "購物",
                    type = TransactionType.EXPENSE.name
                ),
                TransactionEntity(
                    id = 4,
                    title = "薪資",
                    amount = 8500.0,
                    date = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000), // 昨天
                    category = "收入",
                    type = TransactionType.INCOME.name
                )
            )
            
            _uiState.value = TransactionsUiState(
                todayTransactions = todayTransactions,
                yesterdayTransactions = yesterdayTransactions
            )
            
            // 下面是從數據庫獲取數據的示例代碼，目前被注釋掉
            /*
            transactionDao.getAllTransactions().collectLatest { entities ->
                // 這裡需要根據日期將交易分為今天和昨天
                val calendar = Calendar.getInstance()
                val today = calendar.get(Calendar.DAY_OF_YEAR)
                val todayEntities = mutableListOf<TransactionEntity>()
                val yesterdayEntities = mutableListOf<TransactionEntity>()
                
                entities.forEach { entity ->
                    calendar.time = entity.date
                    val entityDay = calendar.get(Calendar.DAY_OF_YEAR)
                    
                    if (entityDay == today) {
                        todayEntities.add(entity)
                    } else if (entityDay == today - 1) {
                        yesterdayEntities.add(entity)
                    }
                }
                
                _uiState.value = TransactionsUiState(
                    todayTransactions = todayEntities,
                    yesterdayTransactions = yesterdayEntities
                )
            }
            */
        }
    }
    
    fun filterTransactions(filter: String) {
        viewModelScope.launch {
            // 實際應用中，這裡會根據過濾條件從數據庫中獲取交易
            // 這裡只是簡單模擬
            val currentState = _uiState.value
            
            when (filter) {
                "收入" -> {
                    val filteredToday = currentState.todayTransactions.filter { it.type == TransactionType.INCOME.name }
                    val filteredYesterday = currentState.yesterdayTransactions.filter { it.type == TransactionType.INCOME.name }
                    _uiState.value = currentState.copy(
                        todayTransactions = filteredToday,
                        yesterdayTransactions = filteredYesterday
                    )
                }
                "支出" -> {
                    val filteredToday = currentState.todayTransactions.filter { it.type == TransactionType.EXPENSE.name }
                    val filteredYesterday = currentState.yesterdayTransactions.filter { it.type == TransactionType.EXPENSE.name }
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
    val todayTransactions: List<TransactionEntity> = emptyList(),
    val yesterdayTransactions: List<TransactionEntity> = emptyList()
) 