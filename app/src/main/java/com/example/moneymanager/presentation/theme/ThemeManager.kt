package com.example.moneymanager.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.moneymanager.data.local.PreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 主題管理器，用於管理應用程式的主題設置
 */
object ThemeManager {
    private var preferencesDataStore: PreferencesDataStore? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    
    val isDarkTheme = mutableStateOf(false)
    
    fun initialize(dataStore: PreferencesDataStore, isSystemInDarkTheme: Boolean) {
        preferencesDataStore = dataStore
        scope.launch {
            val savedTheme = dataStore.isDarkTheme.first()
            isDarkTheme.value = savedTheme ?: isSystemInDarkTheme
        }
    }
    
    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
        scope.launch {
            preferencesDataStore?.setDarkTheme(isDarkTheme.value)
        }
    }
    
    // 設置主題
    fun setDarkTheme(isDark: Boolean) {
        isDarkTheme.value = isDark
        scope.launch {
            preferencesDataStore?.setDarkTheme(isDark)
        }
    }
}

// 創建一個 CompositionLocal 以便在整個應用程式中訪問主題設置
val LocalThemeManager = staticCompositionLocalOf<MutableState<Boolean>> { 
    mutableStateOf(false) 
} 