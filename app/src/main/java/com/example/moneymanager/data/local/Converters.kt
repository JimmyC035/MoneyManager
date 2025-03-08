package com.example.moneymanager.data.local

import androidx.room.TypeConverter
import com.example.moneymanager.presentation.screens.loan.LoanType
import com.example.moneymanager.presentation.screens.loan.RepaymentItem
import com.example.moneymanager.presentation.screens.loan.RepaymentMethod
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()
    
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