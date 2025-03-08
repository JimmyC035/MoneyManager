package com.example.moneymanager.presentation.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.presentation.components.BottomNavBar
import com.example.moneymanager.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "財務分析",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        bottomBar = { BottomNavBar(navController = navController, currentRoute = "statistics_screen") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 收支趨勢標題
            item {
                Text(
                    text = "收支趨勢",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                
                // 最近6個月的收支情況
                Text(
                    text = "最近6個月的收支情況",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            
            // 收支趨勢圖表
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // 圖表標題和選擇器
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "收支趨勢",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            TextButton(onClick = { /* TODO: 時間範圍選擇 */ }) {
                                Text(text = "6個月")
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "選擇時間範圍"
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 收支趨勢圖表
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            // 繪製收支趨勢圖表
                            IncomeExpenseChart(
                                incomeData = uiState.monthlyIncomeData,
                                expenseData = uiState.monthlyExpenseData,
                                months = uiState.months
                            )
                        }
                        
                        // 圖例
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // 收入圖例
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Primary)
                                )
                                Text(
                                    text = "收入",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            
                            // 支出圖例
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Error)
                                )
                                Text(
                                    text = "支出",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // 支出分析標題
            item {
                Text(
                    text = "支出分析",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            // 支出分析圖表
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // 圖表標題和選擇器
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "支出分類",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            TextButton(onClick = { /* TODO: 時間範圍選擇 */ }) {
                                Text(text = "本月")
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "選擇時間範圍"
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 支出分析圖表
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            // 餅圖
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                ExpensePieChart(
                                    data = uiState.expenseCategoryData
                                )
                            }
                            
                            // 分類列表
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                uiState.expenseCategoryData.forEach { (category, percentage, color) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(color)
                                        )
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(start = 8.dp)
                                        ) {
                                            Text(
                                                text = category,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = "${percentage}%",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // 底部空間
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun IncomeExpenseChart(
    incomeData: List<Float>,
    expenseData: List<Float>,
    months: List<String>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 圖表區域
        Box(modifier = Modifier.weight(1f)) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val width = size.width
                val height = size.height
                val barWidth = width / (months.size * 2 + 1)
                val maxValue = (incomeData + expenseData).maxOrNull() ?: 0f
                val barSpacing = barWidth / 2
                
                // 繪製底線
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, height),
                    end = Offset(width, height),
                    strokeWidth = 1.dp.toPx()
                )
                
                // 繪製收入和支出柱狀圖
                for (i in months.indices) {
                    val incomeHeight = if (maxValue > 0) (incomeData[i] / maxValue) * height * 0.8f else 0f
                    val expenseHeight = if (maxValue > 0) (expenseData[i] / maxValue) * height * 0.8f else 0f
                    
                    // 收入柱狀圖
                    drawRect(
                        color = Primary,
                        topLeft = Offset(
                            x = i * (barWidth * 2 + barSpacing) + barSpacing,
                            y = height - incomeHeight
                        ),
                        size = Size(barWidth, incomeHeight)
                    )
                    
                    // 支出柱狀圖
                    drawRect(
                        color = Error,
                        topLeft = Offset(
                            x = i * (barWidth * 2 + barSpacing) + barWidth + barSpacing * 2,
                            y = height - expenseHeight
                        ),
                        size = Size(barWidth, expenseHeight)
                    )
                }
            }
        }
        
        // 月份標籤
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            months.forEach { month ->
                Text(
                    text = month,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun ExpensePieChart(
    data: List<Triple<String, Float, Color>>
) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val radius = size.minDimension / 2 * 0.8f
        val center = Offset(size.width / 2, size.height / 2)
        
        var startAngle = 0f
        val total = data.sumOf { it.second.toDouble() }.toFloat()
        
        // 繪製餅圖
        data.forEach { (_, value, color) ->
            val sweepAngle = 360f * (value / total)
            
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            
            // 繪製邊框
            drawArc(
                color = Color.White,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 2.dp.toPx())
            )
            
            startAngle += sweepAngle
        }
        
        // 繪製中心圓
        drawCircle(
            color = Color.White,
            radius = radius * 0.5f,
            center = center
        )
    }
} 