package com.example.moneymanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moneymanager.data.local.dao.TransactionDao
import com.example.moneymanager.data.local.dao.LoanPlanDao
import com.example.moneymanager.data.local.entity.TransactionEntity
import com.example.moneymanager.data.local.entity.LoanPlanEntity
import com.example.moneymanager.data.local.entity.UriConverters
import com.example.moneymanager.data.local.entity.LoanConverters
import com.example.moneymanager.util.DateConverter

@Database(
    entities = [
        TransactionEntity::class,
        LoanPlanEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, UriConverters::class, LoanConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun loanPlanDao(): LoanPlanDao
    
    companion object {
        const val DATABASE_NAME = "money_manager_db"
    }
} 