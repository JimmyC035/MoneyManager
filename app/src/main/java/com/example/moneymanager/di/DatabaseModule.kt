package com.example.moneymanager.di

import android.content.Context
import com.example.moneymanager.data.database.AppDatabase
import com.example.moneymanager.data.local.dao.LoanPlanDao
import com.example.moneymanager.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }
    
    @Provides
    fun provideLoanPlanDao(database: AppDatabase): LoanPlanDao {
        return database.loanPlanDao()
    }
} 