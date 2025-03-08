package com.example.moneymanager.presentation.screens.home

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
        loadTransactions()
        loadTotalAmount()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            transactionRepository.allTransactions.collect { transactions ->
                // 取最近的 5 筆交易
                val recentTransactions = transactions.sortedByDescending { it.date }.take(5)
                _uiState.update { it.copy(
                    recentTransactions = recentTransactions,
                    isLoading = false
                ) }
            }
        }
    }
    
    private fun loadTotalAmount() {
        viewModelScope.launch {
            transactionRepository.totalAmount.collect { totalAmount ->
                _uiState.update { it.copy(
                    totalAmount = totalAmount ?: 0.0
                ) }
            }
        }
    }
}

data class HomeUiState(
    val recentTransactions: List<Transaction> = emptyList(),
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = true
) 