package com.example.moneymanager.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.data.local.entity.Transaction
import com.example.moneymanager.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
            transactionRepository.allTransactions.collect { transactions ->
                _uiState.update { it.copy(
                    transactions = transactions,
                    isLoading = false
                ) }
            }
        }
    }
    
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }
    
    fun filterTransactions(filter: String) {
        viewModelScope.launch {
            // 實際應用中，這裡會根據過濾條件從數據庫中獲取交易
            val currentState = _uiState.value
            
            when (filter) {
                "收入" -> {
                    // 暫時不實現收入過濾
                    loadTransactions()
                }
                "支出" -> {
                    // 暫時不實現支出過濾
                    loadTransactions()
                }
                "餐飲" -> {
                    val filteredTransactions = currentState.transactions.filter { it.category == "餐飲" }
                    _uiState.value = currentState.copy(
                        transactions = filteredTransactions
                    )
                }
                "購物" -> {
                    val filteredTransactions = currentState.transactions.filter { it.category == "購物" }
                    _uiState.value = currentState.copy(
                        transactions = filteredTransactions
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
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true
) 