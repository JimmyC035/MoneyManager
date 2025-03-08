package com.example.moneymanager

import android.app.Application
import com.example.moneymanager.data.local.PreferencesDataStore
import com.example.moneymanager.presentation.theme.ThemeManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MoneyManagerApp : Application() {
    @Inject
    lateinit var preferencesDataStore: PreferencesDataStore
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化主題管理器，使用系統主題作為默認值
        val isSystemInDarkTheme = resources.configuration.uiMode and 
            android.content.res.Configuration.UI_MODE_NIGHT_MASK == 
            android.content.res.Configuration.UI_MODE_NIGHT_YES
            
        ThemeManager.initialize(preferencesDataStore, isSystemInDarkTheme)
    }
}