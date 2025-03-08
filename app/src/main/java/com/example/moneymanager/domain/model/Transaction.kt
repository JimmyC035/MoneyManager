package com.example.moneymanager.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val description: String,
    val date: Date,
    val createdAt: Date = Date()
)

enum class TransactionType {
    INCOME, EXPENSE
} 