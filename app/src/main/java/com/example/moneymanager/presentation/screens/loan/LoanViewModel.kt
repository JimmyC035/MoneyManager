package com.example.moneymanager.presentation.screens.loan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.pow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.example.moneymanager.data.local.dao.LoanPlanDao
import com.example.moneymanager.data.local.entity.LoanPlanEntity

/**
 * 貸款類型
 */
enum class LoanType(val displayName: String, val maxAmount: Float, val maxYears: Float) {
    PERSONAL("信貸", 500f, 9f),  // 信貸：最高500萬，最長9年
    MORTGAGE("房貸", 1000f, 40f), // 房貸：最高1000萬，最長40年
    CAR("車貸", 300f, 7f)        // 車貸：最高300萬，最長7年
}

/**
 * 還款方式
 */
enum class RepaymentMethod(val displayName: String) {
    EQUAL_INSTALLMENT("等額本息"), // 等額本息
    EQUAL_PRINCIPAL("等額本金")    // 等額本金
}

/**
 * 還款計劃項目
 */
data class RepaymentItem(
    val period: Int,
    val payment: Double,
    val principal: Double,
    val interest: Double,
    val remainingPrincipal: Double
)

/**
 * 已保存的貸款計劃
 */
data class SavedLoanPlan(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val loanType: LoanType,
    val loanAmount: Float,
    val loanTerm: Float,
    val interestRate: Float,
    val repaymentMethod: RepaymentMethod,
    val monthlyPayment: Double,
    val totalRepayment: Double,
    val totalInterest: Double,
    val interestRatio: Double,
    val repaymentSchedule: List<RepaymentItem>,
    val startDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
    val totalPeriods: Int = (loanTerm * 12).toInt(),
    val remainingPeriods: Int = calculateRemainingPeriods(startDate, totalPeriods)
) {
    companion object {
        fun calculateRemainingPeriods(startDate: String, totalPeriods: Int): Int {
            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
            val start = LocalDate.parse(startDate, formatter)
            val now = LocalDate.now()
            val monthsPassed = ChronoUnit.MONTHS.between(start, now).toInt()
            return (totalPeriods - monthsPassed).coerceAtLeast(0)
        }
    }
}

/**
 * 貸款頁面的 UI 狀態
 */
data class LoanUiState(
    val selectedLoanType: LoanType = LoanType.PERSONAL,
    val loanAmount: Float = 100f, // 單位：萬元
    val loanTerm: Float = 5f, // 單位：年
    val interestRate: Float = 4.5f, // 單位：%
    val repaymentMethod: RepaymentMethod = RepaymentMethod.EQUAL_INSTALLMENT,
    val startDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
    val remainingPeriods: Int = (loanTerm * 12).toInt(),
    val showResults: Boolean = false,
    val monthlyPayment: Double = 0.0,
    val totalRepayment: Double = 0.0,
    val totalInterest: Double = 0.0,
    val interestRatio: Double = 0.0,
    val repaymentSchedule: List<RepaymentItem> = emptyList(),
    val savedLoanPlans: List<SavedLoanPlan> = emptyList(),
    val showSaveDialog: Boolean = false,
    val saveLoanName: String = ""
)

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val loanPlanDao: LoanPlanDao
) : ViewModel() {
    
    // UI 狀態
    private val _uiState = MutableStateFlow(LoanUiState())
    val uiState: StateFlow<LoanUiState> = _uiState.asStateFlow()
    
    init {
        // 從資料庫載入已保存的貸款計劃
        viewModelScope.launch {
            loanPlanDao.getAllLoanPlans()
                .collect { loanPlans ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            savedLoanPlans = loanPlans.map { it.toSavedLoanPlan() }
                        )
                    }
                }
        }
    }
    
    /**
     * 設置貸款類型
     */
    fun setLoanType(loanType: LoanType) {
        _uiState.update { currentState ->
            // 根據貸款類型調整貸款金額和期限的上限
            val newLoanAmount = when {
                currentState.loanAmount > loanType.maxAmount -> loanType.maxAmount
                else -> currentState.loanAmount
            }
            
            val newLoanTerm = when {
                currentState.loanTerm > loanType.maxYears -> loanType.maxYears
                else -> currentState.loanTerm
            }
            
            currentState.copy(
                selectedLoanType = loanType,
                loanAmount = newLoanAmount,
                loanTerm = newLoanTerm,
                showResults = false
            )
        }
    }
    
    /**
     * 設置貸款金額
     */
    fun setLoanAmount(amount: Float) {
        _uiState.update { currentState ->
            currentState.copy(
                loanAmount = amount,
                showResults = false
            )
        }
    }
    
    /**
     * 設置貸款期限
     */
    fun setLoanTerm(term: Float) {
        _uiState.update { currentState ->
            currentState.copy(
                loanTerm = term,
                showResults = false
            )
        }
    }
    
    /**
     * 設置年利率
     */
    fun setInterestRate(rate: Float) {
        _uiState.update { currentState ->
            currentState.copy(
                interestRate = rate,
                showResults = false
            )
        }
    }
    
    /**
     * 設置還款方式
     */
    fun setRepaymentMethod(method: RepaymentMethod) {
        _uiState.update { currentState ->
            currentState.copy(
                repaymentMethod = method,
                showResults = false
            )
        }
    }
    
    /**
     * 重置貸款計算器
     */
    fun resetCalculator() {
        _uiState.update { currentState ->
            currentState.copy(
                loanAmount = 100f,
                loanTerm = 5f,
                interestRate = 4.5f,
                repaymentMethod = RepaymentMethod.EQUAL_INSTALLMENT,
                showResults = false
            )
        }
    }
    
    /**
     * 計算貸款
     */
    fun calculateLoan() {
        val currentState = _uiState.value
        val principal = currentState.loanAmount * 10000.0 // 轉換為元
        val years = currentState.loanTerm.toDouble()
        val rate = currentState.interestRate.toDouble()
        
        if (principal > 0 && years > 0 && rate > 0) {
            val monthlyRate = rate / 100 / 12
            val totalMonths = years * 12
            
            var monthlyPayment = 0.0
            var totalRepayment = 0.0
            var totalInterest = 0.0
            
            if (currentState.repaymentMethod == RepaymentMethod.EQUAL_INSTALLMENT) {
                // 等額本息計算公式
                monthlyPayment = principal * monthlyRate * (1 + monthlyRate).pow(totalMonths) / 
                                ((1 + monthlyRate).pow(totalMonths) - 1)
                totalRepayment = monthlyPayment * totalMonths
                totalInterest = totalRepayment - principal
            } else {
                // 等額本金計算
                // 每月本金 = 貸款本金 / 還款月數
                val monthlyPrincipal = principal / totalMonths
                
                // 計算總利息和總還款額
                var remainingPrincipal = principal
                var totalPayment = 0.0
                
                for (i in 1..totalMonths.toInt()) {
                    val monthlyInterest = remainingPrincipal * monthlyRate
                    totalInterest += monthlyInterest
                    totalPayment += (monthlyPrincipal + monthlyInterest)
                    remainingPrincipal -= monthlyPrincipal
                }
                
                // 第一個月的還款額作為參考
                monthlyPayment = monthlyPrincipal + (principal * monthlyRate)
                totalRepayment = totalPayment
            }
            
            val interestRatio = (totalInterest / principal) * 100
            
            // 生成還款計劃
            val schedule = generateRepaymentSchedule(
                principal, 
                monthlyRate, 
                totalMonths.toInt(), 
                currentState.repaymentMethod
            )
            
            _uiState.update { it.copy(
                monthlyPayment = monthlyPayment,
                totalRepayment = totalRepayment,
                totalInterest = totalInterest,
                interestRatio = interestRatio,
                repaymentSchedule = schedule,
                showResults = true
            ) }
        }
    }
    
    /**
     * 生成還款計劃
     */
    private fun generateRepaymentSchedule(
        principal: Double, 
        monthlyRate: Double, 
        totalMonths: Int,
        repaymentMethod: RepaymentMethod
    ): List<RepaymentItem> {
        val schedule = mutableListOf<RepaymentItem>()
        var remainingPrincipal = principal
        
        if (repaymentMethod == RepaymentMethod.EQUAL_INSTALLMENT) {
            // 等額本息
            val monthlyPayment = principal * monthlyRate * (1 + monthlyRate).pow(totalMonths) / 
                                ((1 + monthlyRate).pow(totalMonths) - 1)
            
            for (period in 1..totalMonths) {
                val interest = remainingPrincipal * monthlyRate
                val principalPaid = monthlyPayment - interest
                remainingPrincipal -= principalPaid
                
                schedule.add(
                    RepaymentItem(
                        period = period,
                        payment = monthlyPayment,
                        principal = principalPaid,
                        interest = interest,
                        remainingPrincipal = remainingPrincipal
                    )
                )
                
                // 為了性能考慮，只顯示前12個月的還款計劃
                if (period >= 12) break
            }
        } else {
            // 等額本金
            val monthlyPrincipal = principal / totalMonths
            
            for (period in 1..totalMonths) {
                val interest = remainingPrincipal * monthlyRate
                val payment = monthlyPrincipal + interest
                remainingPrincipal -= monthlyPrincipal
                
                schedule.add(
                    RepaymentItem(
                        period = period,
                        payment = payment,
                        principal = monthlyPrincipal,
                        interest = interest,
                        remainingPrincipal = remainingPrincipal
                    )
                )
                
                // 為了性能考慮，只顯示前12個月的還款計劃
                if (period >= 12) break
            }
        }
        
        return schedule
    }
    
    /**
     * 顯示保存對話框
     */
    fun showSaveDialog() {
        _uiState.update { it.copy(showSaveDialog = true) }
    }
    
    /**
     * 隱藏保存對話框
     */
    fun hideSaveDialog() {
        _uiState.update { it.copy(showSaveDialog = false, saveLoanName = "") }
    }
    
    /**
     * 設置保存貸款計劃的名稱
     */
    fun setSaveLoanName(name: String) {
        _uiState.update { it.copy(saveLoanName = name) }
    }
    
    /**
     * 設置起始時間
     */
    fun setStartDate(date: String) {
        _uiState.update { currentState ->
            currentState.copy(
                startDate = date,
                showResults = false
            )
        }
    }
    
    /**
     * 設置剩餘期數
     */
    fun setRemainingPeriods(periods: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                remainingPeriods = periods.coerceIn(0, (currentState.loanTerm * 12).toInt()),
                showResults = false
            )
        }
    }
    
    /**
     * 保存貸款計劃
     */
    fun saveLoanPlan() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val newPlan = SavedLoanPlan(
                name = currentState.saveLoanName,
                loanType = currentState.selectedLoanType,
                loanAmount = currentState.loanAmount,
                loanTerm = currentState.loanTerm,
                interestRate = currentState.interestRate,
                repaymentMethod = currentState.repaymentMethod,
                monthlyPayment = currentState.monthlyPayment,
                totalRepayment = currentState.totalRepayment,
                totalInterest = currentState.totalInterest,
                interestRatio = currentState.interestRatio,
                repaymentSchedule = currentState.repaymentSchedule,
                startDate = currentState.startDate,
                totalPeriods = (currentState.loanTerm * 12).toInt(),
                remainingPeriods = currentState.remainingPeriods
            )
            
            // 保存到資料庫
            loanPlanDao.insertLoanPlan(newPlan.toLoanPlanEntity())
            
            _uiState.update { it.copy(
                showSaveDialog = false,
                saveLoanName = ""
            ) }
        }
    }
    
    /**
     * 刪除已保存的貸款計劃
     */
    fun deleteSavedLoanPlan(planId: String) {
        viewModelScope.launch {
            loanPlanDao.deleteLoanPlanById(planId)
        }
    }
    
    /**
     * 計算每月總還款額
     */
    fun calculateTotalMonthlyPayment(): Double {
        return _uiState.value.savedLoanPlans.sumOf { it.monthlyPayment }
    }
}

// 擴展函數用於轉換 Entity 和 Domain 模型
private fun SavedLoanPlan.toLoanPlanEntity() = LoanPlanEntity(
    id = id,
    name = name,
    loanType = loanType,
    loanAmount = loanAmount,
    loanTerm = loanTerm,
    interestRate = interestRate,
    repaymentMethod = repaymentMethod,
    monthlyPayment = monthlyPayment,
    totalRepayment = totalRepayment,
    totalInterest = totalInterest,
    interestRatio = interestRatio,
    repaymentSchedule = repaymentSchedule,
    startDate = startDate,
    totalPeriods = totalPeriods,
    remainingPeriods = remainingPeriods
)

private fun LoanPlanEntity.toSavedLoanPlan() = SavedLoanPlan(
    id = id,
    name = name,
    loanType = loanType,
    loanAmount = loanAmount,
    loanTerm = loanTerm,
    interestRate = interestRate,
    repaymentMethod = repaymentMethod,
    monthlyPayment = monthlyPayment,
    totalRepayment = totalRepayment,
    totalInterest = totalInterest,
    interestRatio = interestRatio,
    repaymentSchedule = repaymentSchedule,
    startDate = startDate,
    totalPeriods = totalPeriods,
    remainingPeriods = remainingPeriods
) 