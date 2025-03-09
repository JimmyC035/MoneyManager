package com.example.moneymanager.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.data.local.entity.TransactionEntity
import com.example.moneymanager.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

data class TransactionsUiState(
    val groupedTransactions: Map<LocalDate, List<TransactionEntity>> = emptyMap()
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState = _uiState.asStateFlow()
    
    private var allTransactions = listOf<TransactionEntity>()
    private var currentFilter = "全部"
    
    init {
        viewModelScope.launch {
            repository.allTransactions.collect { transactions ->
                allTransactions = transactions
                updateGroupedTransactions(transactions)
            }
        }
    }
    
    fun filterTransactions(filter: String) {
        currentFilter = filter
        val filteredTransactions = when (filter) {
            "全部" -> allTransactions
            "收入" -> allTransactions.filter { it.type == "INCOME" }
            "支出" -> allTransactions.filter { it.type == "EXPENSE" }
            else -> allTransactions.filter { it.category == filter }
        }
        updateGroupedTransactions(filteredTransactions)
    }
    
    private fun updateGroupedTransactions(transactions: List<TransactionEntity>) {
        val grouped = transactions
            .groupBy { it.date.toLocalDate() }
            .toSortedMap(compareByDescending { it })
        
        _uiState.update { it.copy(groupedTransactions = grouped) }
    }
    
    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
    
    // Extension function to convert Date to LocalDate
    private fun Date.toLocalDate(): LocalDate {
        return this.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}