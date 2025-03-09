package com.example.moneymanager.data.repository

import com.example.moneymanager.data.local.dao.TransactionDao
import com.example.moneymanager.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    
    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }
    
    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }
    
    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }
    
    fun getTotalIncome(): Flow<Double?> {
        return transactionDao.getTotalIncome()
    }
    
    fun getTotalExpense(): Flow<Double?> {
        return transactionDao.getTotalExpense()
    }
    
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByType(type)
    }
} 