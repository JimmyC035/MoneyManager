package com.example.moneymanager.util

import com.example.moneymanager.domain.model.Transaction
import com.example.moneymanager.domain.model.TransactionType
import java.util.Date
import kotlin.random.Random

object MockDataHelper {
    fun getMockTransactions(): List<Transaction> {
        return listOf(
            Transaction(
                amount = 8500.0,
                type = TransactionType.INCOME,
                category = "工資",
                description = "月薪",
                date = Date()
            ),
            Transaction(
                amount = 45.0,
                type = TransactionType.EXPENSE,
                category = "餐飲",
                description = "午餐",
                date = Date()
            ),
            Transaction(
                amount = 156.80,
                type = TransactionType.EXPENSE,
                category = "購物",
                description = "超市購物",
                date = Date()
            )
        )
    }

    fun getMockTotalBalance(): Double = 25680.42

    fun getMockMonthlyIncome(): Double = 8500.0

    fun getMockMonthlyExpense(): Double = 3245.75

    fun getMockBudgets(): List<BudgetMock> = listOf(
        BudgetMock("餐飲", 850.0, 1200.0),
        BudgetMock("購物", 950.0, 1000.0),
        BudgetMock("娛樂", 650.0, 500.0)
    )

    fun getMockExpensePercentages(): List<ExpensePercentageMock> = listOf(
        ExpensePercentageMock("餐飲", 30),
        ExpensePercentageMock("購物", 15),
        ExpensePercentageMock("交通", 10),
        ExpensePercentageMock("娛樂", 15),
        ExpensePercentageMock("住房", 15),
        ExpensePercentageMock("其他", 15)
    )

    fun getMockMonthlyStats(): List<MonthlyStatsMock> = listOf(
        MonthlyStatsMock("1月", 8000.0, 4000.0),
        MonthlyStatsMock("2月", 10000.0, 6000.0),
        MonthlyStatsMock("3月", 7000.0, 5000.0),
        MonthlyStatsMock("4月", 9000.0, 4500.0),
        MonthlyStatsMock("5月", 8500.0, 3500.0),
        MonthlyStatsMock("6月", 9500.0, 3000.0)
    )
}

data class BudgetMock(
    val category: String,
    val spent: Double,
    val limit: Double
)

data class ExpensePercentageMock(
    val category: String,
    val percentage: Int
)

data class MonthlyStatsMock(
    val month: String,
    val income: Double,
    val expense: Double
) 