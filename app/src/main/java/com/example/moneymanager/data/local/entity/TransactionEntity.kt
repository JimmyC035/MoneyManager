package com.example.moneymanager.data.local.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "transactions")
@TypeConverters(UriConverters::class)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val type: String, // "INCOME", "EXPENSE", "TRANSFER"
    val date: Date,
    val note: String = "",
    val imageUri: String? = null,
    val createdAt: Date = Date()
) {
    // 用於 UI 顯示的輔助屬性和方法
    
    // 獲取格式化的日期字符串
    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }
    
    // 獲取格式化的創建時間字符串
    fun getFormattedCreatedTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeFormat.format(createdAt)
    }
    
    // 獲取完整的格式化日期和時間
    fun getFullFormattedDateTime(): String {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateTimeFormat.format(createdAt)
    }
    
    // 獲取交易類型的枚舉值
    fun getTransactionType(): TransactionType {
        return TransactionType.valueOf(type)
    }
    
    // 獲取描述（與 note 相同）
    val description: String
        get() = note
}

// 將 TransactionType 移到這裡，使其可以在整個應用中使用
enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

class UriConverters {
    @TypeConverter
    fun fromString(value: String?): Uri? {
        return value?.let { Uri.parse(it) }
    }
    
    @TypeConverter
    fun uriToString(uri: Uri?): String? {
        return uri?.toString()
    }
} 