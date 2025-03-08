package com.example.moneymanager.di

import android.content.Context
import androidx.room.Room
import com.example.moneymanager.data.local.MoneyManagerDatabase
import com.example.moneymanager.data.local.dao.LoanPlanDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MoneyManagerDatabase {
        return Room.databaseBuilder(
            context,
            MoneyManagerDatabase::class.java,
            MoneyManagerDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideLoanPlanDao(database: MoneyManagerDatabase): LoanPlanDao {
        return database.loanPlanDao()
    }
} 