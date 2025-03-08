package com.example.moneymanager.data.local

import android.net.Uri
import androidx.room.TypeConverter
import com.example.moneymanager.presentation.screens.loan.LoanType
import com.example.moneymanager.presentation.screens.loan.RepaymentItem
import com.example.moneymanager.presentation.screens.loan.RepaymentMethod
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()
    
    // Transaction Converters
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromString(value: String?): Uri? {
        return value?.let { Uri.parse(it) }
    }
    
    @TypeConverter
    fun uriToString(uri: Uri?): String? {
        return uri?.toString()
    }
    
    // Loan Plan Converters
    @TypeConverter
    fun fromLoanType(value: LoanType): String = value.name
    
    @TypeConverter
    fun toLoanType(value: String): LoanType = LoanType.valueOf(value)
    
    @TypeConverter
    fun fromRepaymentMethod(value: RepaymentMethod): String = value.name
    
    @TypeConverter
    fun toRepaymentMethod(value: String): RepaymentMethod = RepaymentMethod.valueOf(value)
    
    @TypeConverter
    fun fromRepaymentSchedule(value: List<RepaymentItem>): String = gson.toJson(value)
    
    @TypeConverter
    fun toRepaymentSchedule(value: String): List<RepaymentItem> {
        val listType = object : TypeToken<List<RepaymentItem>>() {}.type
        return gson.fromJson(value, listType)
    }
} 