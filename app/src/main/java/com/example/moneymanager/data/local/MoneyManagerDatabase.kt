package com.example.moneymanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moneymanager.data.local.dao.LoanPlanDao
import com.example.moneymanager.data.local.entity.LoanPlanEntity
import com.example.moneymanager.data.local.Converters

@Database(
    entities = [
        LoanPlanEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MoneyManagerDatabase : RoomDatabase() {
    abstract fun loanPlanDao(): LoanPlanDao
    
    companion object {
        const val DATABASE_NAME = "money_manager_db"
    }
} 