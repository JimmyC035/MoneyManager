package com.example.moneymanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.moneymanager.presentation.screens.loan.LoanType
import com.example.moneymanager.presentation.screens.loan.RepaymentItem
import com.example.moneymanager.presentation.screens.loan.RepaymentMethod
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "loan_plans")
@TypeConverters(LoanConverters::class)
data class LoanPlanEntity(
    @PrimaryKey val id: String,
    val name: String,
    val loanType: LoanType,
    val loanAmount: Float,
    val loanTerm: Float,
    val interestRate: Float,
    val repaymentMethod: RepaymentMethod,
    val monthlyPayment: Double,
    val totalRepayment: Double,
    val totalInterest: Double,
    val interestRatio: Double,
    val repaymentSchedule: List<RepaymentItem>,
    val startDate: String,
    val totalPeriods: Int,
    val remainingPeriods: Int
)

class LoanConverters {
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