package com.example.moneymanager.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.data.local.dao.TransactionDao
import com.example.moneymanager.data.local.entity.TransactionEntity
import com.example.moneymanager.data.local.entity.TransactionType
import com.example.moneymanager.data.repository.TransactionRepository
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
    private val transactionRepository: TransactionRepository
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
            transactionRepository.allTransactions.collectLatest { entities ->
                // 根據日期將交易分為今天和昨天
                val calendar = Calendar.getInstance()
                val today = calendar.get(Calendar.DAY_OF_YEAR)
                val todayEntities = mutableListOf<TransactionEntity>()
                val yesterdayEntities = mutableListOf<TransactionEntity>()
                
                // 確保按日期和創建時間排序
                val sortedEntities = entities.sortedWith(
                    compareByDescending<TransactionEntity> { it.date }
                        .thenByDescending { it.createdAt }
                )
                
                sortedEntities.forEach { entity ->
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
        }
    }
    
    fun filterTransactions(filter: String) {
        viewModelScope.launch {
            when (filter) {
                "收入" -> {
                    transactionRepository.getTransactionsByType(TransactionType.INCOME.name).collectLatest { entities ->
                        // 根據日期將交易分為今天和昨天
                        val calendar = Calendar.getInstance()
                        val today = calendar.get(Calendar.DAY_OF_YEAR)
                        val todayEntities = mutableListOf<TransactionEntity>()
                        val yesterdayEntities = mutableListOf<TransactionEntity>()
                        
                        // 確保按日期和創建時間排序
                        val sortedEntities = entities.sortedWith(
                            compareByDescending<TransactionEntity> { it.date }
                                .thenByDescending { it.createdAt }
                        )
                        
                        sortedEntities.forEach { entity ->
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
                }
                "支出" -> {
                    transactionRepository.getTransactionsByType(TransactionType.EXPENSE.name).collectLatest { entities ->
                        // 根據日期將交易分為今天和昨天
                        val calendar = Calendar.getInstance()
                        val today = calendar.get(Calendar.DAY_OF_YEAR)
                        val todayEntities = mutableListOf<TransactionEntity>()
                        val yesterdayEntities = mutableListOf<TransactionEntity>()
                        
                        // 確保按日期和創建時間排序
                        val sortedEntities = entities.sortedWith(
                            compareByDescending<TransactionEntity> { it.date }
                                .thenByDescending { it.createdAt }
                        )
                        
                        sortedEntities.forEach { entity ->
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
                }
                "餐飲", "購物", "交通", "娛樂", "醫療", "住宿", "其他" -> {
                    // 這裡需要添加按類別過濾的功能
                    // 目前暫時使用內存過濾
                    transactionRepository.allTransactions.collectLatest { entities ->
                        // 根據日期將交易分為今天和昨天
                        val calendar = Calendar.getInstance()
                        val today = calendar.get(Calendar.DAY_OF_YEAR)
                        val todayEntities = mutableListOf<TransactionEntity>()
                        val yesterdayEntities = mutableListOf<TransactionEntity>()
                        
                        // 先按類別過濾，然後按日期排序
                        val filteredEntities = entities.filter { it.category == filter }
                            .sortedWith(compareByDescending<TransactionEntity> { it.date }
                                .thenByDescending { it.createdAt })
                        
                        filteredEntities.forEach { entity ->
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
                }
                else -> {
                    // 重新加載所有交易
                    loadTransactions()
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

data class TransactionsUiState(
    val todayTransactions: List<TransactionEntity> = emptyList(),
    val yesterdayTransactions: List<TransactionEntity> = emptyList()
) 