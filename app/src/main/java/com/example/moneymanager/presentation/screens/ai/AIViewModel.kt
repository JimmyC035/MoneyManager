package com.example.moneymanager.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AIUiState())
    val uiState: StateFlow<AIUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // 模擬數據加載
            _uiState.value = AIUiState(
                isProcessing = false,
                recognizedText = "",
                recognizedAmount = 0.0,
                recognizedCategory = "",
                recognizedDate = ""
            )
        }
    }
    
    fun processImage(imageUri: String) {
        viewModelScope.launch {
            // 模擬圖像處理
            _uiState.value = _uiState.value.copy(isProcessing = true)
            
            // 模擬延遲
            kotlinx.coroutines.delay(1500)
            
            // 模擬識別結果
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                recognizedText = "午餐 - 麥當勞",
                recognizedAmount = 45.0,
                recognizedCategory = "餐飲",
                recognizedDate = "2023-06-15"
            )
        }
    }
    
    fun processVoice(voiceText: String) {
        viewModelScope.launch {
            // 模擬語音處理
            _uiState.value = _uiState.value.copy(isProcessing = true)
            
            // 模擬延遲
            kotlinx.coroutines.delay(1000)
            
            // 模擬識別結果
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                recognizedText = voiceText,
                recognizedAmount = 28.0,
                recognizedCategory = "咖啡",
                recognizedDate = "2023-06-15"
            )
        }
    }
    
    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(recognizedCategory = category)
    }
    
    fun updateAmount(amount: Double) {
        _uiState.value = _uiState.value.copy(recognizedAmount = amount)
    }
    
    fun updateDate(date: String) {
        _uiState.value = _uiState.value.copy(recognizedDate = date)
    }
    
    fun saveTransaction() {
        viewModelScope.launch {
            // 模擬保存交易
            // 實際應用中，這裡會調用儲存庫保存交易
            
            // 重置狀態
            _uiState.value = AIUiState(
                isProcessing = false,
                recognizedText = "",
                recognizedAmount = 0.0,
                recognizedCategory = "",
                recognizedDate = ""
            )
        }
    }
}

data class AIUiState(
    val isProcessing: Boolean = false,
    val recognizedText: String = "",
    val recognizedAmount: Double = 0.0,
    val recognizedCategory: String = "",
    val recognizedDate: String = ""
) 