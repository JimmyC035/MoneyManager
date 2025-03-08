package com.example.moneymanager.presentation.model

data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val date: String,
    val category: String,
    val type: TransactionType,
    val description: String = ""
)

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
} 