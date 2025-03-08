package com.example.moneymanager.data.local.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.moneymanager.data.local.Converters
import java.util.Date

@Entity(tableName = "transactions")
@TypeConverters(Converters::class)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val date: Date,
    val note: String,
    val imageUri: String? = null,
    val createdAt: Date = Date(),
    val type: String = "EXPENSE" // 添加 type 字段，默認為 EXPENSE
) 