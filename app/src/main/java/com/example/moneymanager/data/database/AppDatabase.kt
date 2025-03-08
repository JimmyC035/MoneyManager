package com.example.moneymanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moneymanager.data.local.Converters
import com.example.moneymanager.data.local.dao.LoanPlanDao
import com.example.moneymanager.data.local.dao.TransactionDao
import com.example.moneymanager.data.local.entity.LoanPlanEntity
import com.example.moneymanager.data.local.entity.Transaction

@Database(
    entities = [
        Transaction::class,
        LoanPlanEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun loanPlanDao(): LoanPlanDao
    
    companion object {
        const val DATABASE_NAME = "money_manager_database"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 