package com.example.moneymanager.data.local.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "transactions")
@TypeConverters(TransactionConverters::class)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val date: Date,
    val note: String,
    val imageUri: String? = null,
    val createdAt: Date = Date()
)

class TransactionConverters {
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
} 