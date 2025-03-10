package com.example.moneymanager

import android.app.Application
import android.content.Context
import com.example.moneymanager.data.local.PreferencesDataStore
import com.example.moneymanager.presentation.theme.ThemeManager
import com.example.moneymanager.util.LocaleHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MoneyManagerApplication : Application() {
    @Inject
    lateinit var preferencesDataStore: PreferencesDataStore
    
    override fun onCreate() {
        super.onCreate()
        LocaleHelper.onAttach(this)
        // 初始化主題管理器，使用系統主題作為默認值
        val isSystemInDarkTheme = resources.configuration.uiMode and 
            android.content.res.Configuration.UI_MODE_NIGHT_MASK == 
            android.content.res.Configuration.UI_MODE_NIGHT_YES
            
        ThemeManager.initialize(preferencesDataStore, isSystemInDarkTheme)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }
}