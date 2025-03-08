package com.example.moneymanager.presentation.screens.loan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.presentation.components.BottomNavBar
import com.example.moneymanager.presentation.components.BubbleBackground
import com.example.moneymanager.presentation.navigation.Screen
import com.example.moneymanager.presentation.theme.Primary
import com.example.moneymanager.presentation.theme.ThemeManager
import kotlin.math.pow
import kotlin.math.roundToInt
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.rememberDatePickerState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.Instant

@Composable
fun LoanScreen(
    navController: NavController,
    viewModel: LoanViewModel = hiltViewModel()
) {
    val navBackStackEntry = navController.currentBackStackEntry
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Loan.route
    
    // 獲取當前主題
    val isDarkTheme = ThemeManager.isDarkTheme.value
    
    // 獲取當前主題的顏色
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1C1E) else Color(0xFFF8F9FA)
    val cardColor = if (isDarkTheme) Color(0xFF2D3033) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val secondaryTextColor = if (isDarkTheme) Color.LightGray else Color.DarkGray
    
    // 收集 UI 狀態
    val uiState by viewModel.uiState.collectAsState()
    
    // 保存對話框狀態
    if (uiState.showSaveDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideSaveDialog() },
            title = { Text("保存貸款計劃") },
            text = {
                OutlinedTextField(
                    value = uiState.saveLoanName,
                    onValueChange = { viewModel.setSaveLoanName(it) },
                    label = { Text("計劃名稱") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.saveLoanPlan() }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideSaveDialog() }
                ) {
                    Text("取消")
                }
            }
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 頂部標題和按鈕
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "貸款計算器",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                
                // 重置按鈕
                IconButton(onClick = { viewModel.resetCalculator() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "重置",
                        tint = Primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 輸入區域
            BubbleBackground(
                modifier = Modifier.fillMaxWidth(),
                bubbleColor = cardColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // 貸款類型選擇
                    Text(
                        text = "貸款類型",
                        fontSize = 14.sp,
                        color = secondaryTextColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    var expanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedLoanType.displayName,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = if (isDarkTheme) Color.DarkGray else Color.LightGray,
                                focusedBorderColor = Primary,
                                unfocusedTextColor = textColor,
                                focusedTextColor = textColor
                            ),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            LoanType.values().forEach { loanType ->
                                DropdownMenuItem(
                                    text = { Text(loanType.displayName) },
                                    onClick = {
                                        viewModel.setLoanType(loanType)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 貸款金額
                    Text(
                        text = "貸款金額 (新臺幣${uiState.loanAmount.roundToInt()}萬元)",
                        fontSize = 14.sp,
                        color = secondaryTextColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // 貸款金額滑動條
                    Slider(
                        value = uiState.loanAmount,
                        onValueChange = { viewModel.setLoanAmount(it) },
                        valueRange = 20f..uiState.selectedLoanType.maxAmount,
                        steps = (uiState.selectedLoanType.maxAmount - 20f).toInt(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = if (isDarkTheme) Color.DarkGray else Color.LightGray
                        )
                    )
                    
                    // 顯示貸款金額範圍
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "20萬",
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )
                        
                        Text(
                            text = "${uiState.selectedLoanType.maxAmount.roundToInt()}萬",
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 貸款期限
                    Text(
                        text = "貸款期限 (${uiState.loanTerm.roundToInt()}年)",
                        fontSize = 14.sp,
                        color = secondaryTextColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // 貸款期限滑動條
                    Slider(
                        value = uiState.loanTerm,
                        onValueChange = { viewModel.setLoanTerm(it) },
                        valueRange = 1f..uiState.selectedLoanType.maxYears,
                        steps = (uiState.selectedLoanType.maxYears - 1f).toInt(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = if (isDarkTheme) Color.DarkGray else Color.LightGray
                        )
                    )
                    
                    // 顯示貸款期限範圍
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "1年",
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )
                        
                        Text(
                            text = "${uiState.selectedLoanType.maxYears.roundToInt()}年",
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 年利率
                    Text(
                        text = "年利率 (${String.format("%.1f", uiState.interestRate)}%)",
                        fontSize = 14.sp,
                        color = secondaryTextColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // 年利率滑動條
                    Slider(
                        value = uiState.interestRate,
                        onValueChange = { viewModel.setInterestRate(it) },
                        valueRange = 1f..10f,
                        steps = 90,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = if (isDarkTheme) Color.DarkGray else Color.LightGray
                        )
                    )
                    
                    // 顯示年利率範圍
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "1%",
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )
                        
                        Text(
                            text = "10%",
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 還款方式
                    Text(
                        text = "還款方式",
                        fontSize = 14.sp,
                        color = secondaryTextColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    var repaymentMethodExpanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = repaymentMethodExpanded,
                        onExpandedChange = { repaymentMethodExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.repaymentMethod.displayName,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = if (isDarkTheme) Color.DarkGray else Color.LightGray,
                                focusedBorderColor = Primary,
                                unfocusedTextColor = textColor,
                                focusedTextColor = textColor
                            ),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = repaymentMethodExpanded)
                            }
                        )
                        
                        ExposedDropdownMenu(
                            expanded = repaymentMethodExpanded,
                            onDismissRequest = { repaymentMethodExpanded = false }
                        ) {
                            RepaymentMethod.values().forEach { method ->
                                DropdownMenuItem(
                                    text = { Text(method.displayName) },
                                    onClick = {
                                        viewModel.setRepaymentMethod(method)
                                        repaymentMethodExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 起始時間
                    Text(
                        text = "起始時間",
                        fontSize = 14.sp,
                        color = secondaryTextColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    var showDatePicker by remember { mutableStateOf(false) }
                    val formatter = remember { DateTimeFormatter.ofPattern("yyyy/MM/dd") }
                    val initialDate = remember(uiState.startDate) {
                        try {
                            LocalDate.parse(uiState.startDate, formatter)
                                .atStartOfDay(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()
                        } catch (e: Exception) {
                            System.currentTimeMillis()
                        }
                    }
                    
                    OutlinedTextField(
                        value = uiState.startDate,
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = if (isDarkTheme) Color.DarkGray else Color.LightGray,
                            focusedBorderColor = Primary,
                            unfocusedTextColor = textColor,
                            focusedTextColor = textColor
                        ),
                        placeholder = { Text("選擇日期") },
                        singleLine = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "選擇日期",
                                tint = if (isDarkTheme) Color.LightGray else Color.DarkGray
                            )
                        }
                    )
                    
                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = initialDate
                        )
                        
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            val selectedDate = Instant
                                                .ofEpochMilli(millis)
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate()
                                                .format(formatter)
                                            viewModel.setStartDate(selectedDate)
                                        }
                                        showDatePicker = false
                                    }
                                ) {
                                    Text("確認")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showDatePicker = false }
                                ) {
                                    Text("取消")
                                }
                            }
                        ) {
                            DatePicker(
                                state = datePickerState,
                                colors = DatePickerDefaults.colors(
                                    containerColor = cardColor,
                                    titleContentColor = textColor,
                                    headlineContentColor = textColor,
                                    weekdayContentColor = secondaryTextColor,
                                    subheadContentColor = textColor,
                                    yearContentColor = textColor,
                                    currentYearContentColor = Primary,
                                    selectedYearContentColor = Color.White,
                                    selectedYearContainerColor = Primary,
                                    dayContentColor = textColor,
                                    selectedDayContentColor = Color.White,
                                    selectedDayContainerColor = Primary,
                                    todayContentColor = Primary,
                                    todayDateBorderColor = Primary
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 剩餘期數
                    Text(
                        text = "剩餘期數 (${uiState.remainingPeriods}期)",
                        fontSize = 14.sp,
                        color = secondaryTextColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // 剩餘期數滑動條
                    Slider(
                        value = uiState.remainingPeriods.toFloat(),
                        onValueChange = { viewModel.setRemainingPeriods(it.toInt()) },
                        valueRange = 0f..(uiState.loanTerm * 12),
                        steps = (uiState.loanTerm * 12).toInt(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = if (isDarkTheme) Color.DarkGray else Color.LightGray
                        )
                    )
                    
                    // 顯示剩餘期數範圍
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "0期",
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )
                        
                        Text(
                            text = "${(uiState.loanTerm * 12).toInt()}期",
                            fontSize = 12.sp,
                            color = secondaryTextColor
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 計算按鈕
            Button(
                onClick = { 
                    if (!uiState.showResults) {
                        viewModel.calculateLoan()
                    } else {
                        viewModel.showSaveDialog()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Text(
                    text = if (!uiState.showResults) "計算結果" else "儲存貸款",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 結果顯示區域
            if (uiState.showResults) {
                BubbleBackground(
                    modifier = Modifier.fillMaxWidth(),
                    bubbleColor = cardColor
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // 月供金額
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "月供金額",
                                fontSize = 14.sp,
                                color = secondaryTextColor
                            )
                            
                            Text(
                                text = "$ ${String.format("%.2f", uiState.monthlyPayment)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                        
                        Divider(color = if (isDarkTheme) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f))
                        
                        // 總還款額
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "總還款額",
                                fontSize = 14.sp,
                                color = secondaryTextColor
                            )
                            
                            Text(
                                text = "$ ${String.format("%.2f", uiState.totalRepayment)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                        
                        Divider(color = if (isDarkTheme) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f))
                        
                        // 總利息
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "總利息",
                                fontSize = 14.sp,
                                color = secondaryTextColor
                            )
                            
                            Text(
                                text = "$ ${String.format("%.2f", uiState.totalInterest)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        }
                        
                        Divider(color = if (isDarkTheme) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f))
                        
                        // 利息比例
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "利息比例",
                                fontSize = 14.sp,
                                color = secondaryTextColor
                            )
                            
                            Text(
                                text = "${String.format("%.2f", uiState.interestRatio)}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }
            
            // 已保存的貸款計劃
            if (uiState.savedLoanPlans.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "已保存的貸款計劃",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                // 總月供金額
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "總月供金額",
                            fontSize = 14.sp,
                            color = secondaryTextColor
                        )
                        
                        Text(
                            text = "$ ${String.format("%.2f", viewModel.calculateTotalMonthlyPayment())}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 已保存的貸款計劃列表
                uiState.savedLoanPlans.forEach { plan ->
                    var expanded by remember { mutableStateOf(false) }
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { expanded = !expanded },
                        colors = CardDefaults.cardColors(
                            containerColor = cardColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = plan.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    
                                    Text(
                                        text = "${plan.loanType.displayName} - ${plan.loanAmount}萬",
                                        fontSize = 14.sp,
                                        color = secondaryTextColor
                                    )
                                    
                                    Text(
                                        text = "月供：$ ${String.format("%.2f", plan.monthlyPayment)}",
                                        fontSize = 14.sp,
                                        color = Primary
                                    )

                                    // 添加起始時間和剩餘期數
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "起始：${plan.startDate}",
                                            fontSize = 12.sp,
                                            color = secondaryTextColor
                                        )
                                        
                                        Text(
                                            text = "剩餘：${plan.remainingPeriods}期",
                                            fontSize = 12.sp,
                                            color = if (plan.remainingPeriods > 0) Primary else Color.Red
                                        )
                                    }
                                }
                                
                                Row {
                                    IconButton(
                                        onClick = { expanded = !expanded }
                                    ) {
                                        Icon(
                                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = if (expanded) "收起" else "展開",
                                            tint = textColor
                                        )
                                    }
                                    
                                    IconButton(
                                        onClick = { viewModel.deleteSavedLoanPlan(plan.id) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "刪除",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                            
                            AnimatedVisibility(
                                visible = expanded,
                                enter = expandVertically(),
                                exit = shrinkVertically()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    // 表頭
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "期數",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = textColor,
                                            modifier = Modifier.weight(1f)
                                        )
                                        
                                        Text(
                                            text = "月供",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = textColor,
                                            modifier = Modifier.weight(1f)
                                        )
                                        
                                        Text(
                                            text = "本金",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = textColor,
                                            modifier = Modifier.weight(1f)
                                        )
                                        
                                        Text(
                                            text = "利息",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = textColor,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    
                                    Divider(color = if (isDarkTheme) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f))
                                    
                                    // 還款計劃詳細內容
                                    // 從當前期數開始顯示12期
                                    val currentPeriod = plan.totalPeriods - plan.remainingPeriods + 1
                                    plan.repaymentSchedule
                                        .filter { it.period >= currentPeriod && it.period < currentPeriod + 12 }
                                        .forEach { item ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "${item.period}",
                                                    fontSize = 14.sp,
                                                    color = textColor,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                
                                                Text(
                                                    text = "$ ${String.format("%.2f", item.payment)}",
                                                    fontSize = 14.sp,
                                                    color = textColor,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                
                                                Text(
                                                    text = "$ ${String.format("%.2f", item.principal)}",
                                                    fontSize = 14.sp,
                                                    color = textColor,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                
                                                Text(
                                                    text = "$ ${String.format("%.2f", item.interest)}",
                                                    fontSize = 14.sp,
                                                    color = textColor,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            
                                            if (item.period < currentPeriod + 11) {
                                                Divider(color = if (isDarkTheme) Color.DarkGray.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.3f))
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            }
            
            // 底部空間，確保內容不被底部導航欄遮擋
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        // 底部導航欄
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavBar(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    }
} 