package com.example.moneymanager.data.local.dao

import androidx.room.*
import com.example.moneymanager.data.local.entity.LoanPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanPlanDao {
    @Query("SELECT * FROM loan_plans")
    fun getAllLoanPlans(): Flow<List<LoanPlanEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanPlan(loanPlan: LoanPlanEntity)
    
    @Delete
    suspend fun deleteLoanPlan(loanPlan: LoanPlanEntity)
    
    @Query("DELETE FROM loan_plans WHERE id = :planId")
    suspend fun deleteLoanPlanById(planId: String)
} 