package com.example.moneymanager.presentation.screens.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.presentation.theme.Error
import com.example.moneymanager.presentation.theme.Primary
import com.example.moneymanager.presentation.theme.Success
import com.example.moneymanager.presentation.theme.Warning
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatisticsData()
    }

    private fun loadStatisticsData() {
        viewModelScope.launch {
            // 模擬數據加載
            val months = listOf("1月", "2月", "3月", "4月", "5月", "6月")
            val incomeData = listOf(5000f, 5500f, 4800f, 6000f, 5200f, 5800f)
            val expenseData = listOf(3500f, 4200f, 3800f, 4500f, 3900f, 4100f)
            
            val expenseCategoryData = listOf(
                Triple("餐飲", 35f, Error),
                Triple("購物", 25f, Primary),
                Triple("交通", 15f, Success),
                Triple("娛樂", 10f, Warning),
                Triple("其他", 15f, Color(0xFF9775FA))
            )
            
            _uiState.value = StatisticsUiState(
                months = months,
                monthlyIncomeData = incomeData,
                monthlyExpenseData = expenseData,
                expenseCategoryData = expenseCategoryData
            )
        }
    }
}

data class StatisticsUiState(
    val months: List<String> = emptyList(),
    val monthlyIncomeData: List<Float> = emptyList(),
    val monthlyExpenseData: List<Float> = emptyList(),
    val expenseCategoryData: List<Triple<String, Float, Color>> = emptyList()
) 