package com.example.moneymanager.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.*

object LocaleHelper {
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    
    fun onAttach(context: Context): Context {
        val lang = getPersistedData(context, Locale.getDefault().language)
        return setLocale(context, lang)
    }
    
    fun getLanguage(context: Context): String {
        return getPersistedData(context, Locale.getDefault().language)
    }
    
    fun setLocale(context: Context, language: String): Context {
        persist(context, language)
        
        // 使用 AppCompatDelegate 設置語言
        val localeList = LocaleListCompat.create(Locale(language))
        AppCompatDelegate.setApplicationLocales(localeList)
        
        return updateResources(context, language)
    }
    
    private fun getPersistedData(context: Context, defaultLanguage: String): String {
        val preferences = context.getSharedPreferences("language_settings", Context.MODE_PRIVATE)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage) ?: defaultLanguage
    }
    
    private fun persist(context: Context, language: String) {
        val preferences = context.getSharedPreferences("language_settings", Context.MODE_PRIVATE)
        preferences.edit().putString(SELECTED_LANGUAGE, language).apply()
    }
    
    @Suppress("DEPRECATION")
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration)
        } else {
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            context
        }
    }
    
    fun isCurrentLanguageEnglish(context: Context): Boolean {
        return getLanguage(context) == "en"
    }
} 