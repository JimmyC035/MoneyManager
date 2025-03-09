package com.example.moneymanager.util

import android.content.Context
import com.example.moneymanager.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

object DateFormatter {
    fun getFormattedDate(context: Context, date: LocalDate): String {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        
        return when (date) {
            today -> context.getString(R.string.today)
            yesterday -> context.getString(R.string.yesterday)
            else -> {
                if (LocaleHelper.isCurrentLanguageEnglish(context)) {
                    // 英文格式：March 5
                    val month = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    context.getString(R.string.date_format, month, date.dayOfMonth)
                } else {
                    // 中文格式：3月5日
                    context.getString(R.string.date_format, date.monthValue, date.dayOfMonth)
                }
            }
        }
    }
} 