package com.example.moneymanager.presentation.screens.home

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.presentation.components.BottomNavBar
import com.example.moneymanager.presentation.components.BubbleBackground
import com.example.moneymanager.data.local.entity.TransactionEntity
import com.example.moneymanager.data.local.entity.TransactionType
import com.example.moneymanager.presentation.screens.expense.AddExpenseDialog
import com.example.moneymanager.presentation.theme.Primary
import com.example.moneymanager.util.BudgetMock
import java.text.NumberFormat
import java.util.*
import com.example.moneymanager.presentation.navigation.Screen
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch

// 全局狀態，用於跟踪當前正在滑動的項目ID
private val CurrentlySwipedItemId = mutableStateOf<Long?>(null)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTime = viewModel.currentTime
    
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onSave = { showAddExpenseDialog = false }
        )
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = { BottomNavBar(navController = navController, currentRoute = Screen.Home.route) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddExpenseDialog = true },
                containerColor = Primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "新增交易紀錄",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .windowInsetsPadding(WindowInsets.statusBars),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // 標題
                Text(
                    text = "智能記賬",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            item {
                // 總資產卡片（帶有氣泡背景）
                BubbleBackground(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "總資產",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Text(
                            text = formatCurrency(uiState.totalBalance),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "本月收入",
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = formatCurrency(uiState.monthlyIncome),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            
                            Column {
                                Text(
                                    text = "本月支出",
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = formatCurrency(uiState.monthlyExpense),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                // 最近交易標題
                Text(
                    text = "最近交易",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            // 最近交易列表
            items(uiState.recentTransactions) { transaction ->
                HomeTransactionItem(transaction = transaction, viewModel = viewModel)
            }
            
            item {
                BudgetCard(budgets = uiState.budgets)
            }
            
            // 底部空間
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun BalanceCard(
    totalBalance: Int,
    monthlyIncome: Int,
    monthlyExpense: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "總資產",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            
            Text(
                text = "¥ $totalBalance",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "本月收入",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "¥ $monthlyIncome",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "本月支出",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "¥ $monthlyExpense",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeTransactionItem(
    transaction: TransactionEntity,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // 滑動刪除相關
    val density = LocalDensity.current
    var offsetX by remember { mutableStateOf(0f) }
    val deleteButtonWidth = 80.dp
    val deleteButtonWidthPx = with(density) { deleteButtonWidth.toPx() }
    val isDeleteVisible = offsetX < -deleteButtonWidthPx / 2
    
    // 檢查當前項目是否是正在滑動的項目
    val isCurrentlySwipedItem = CurrentlySwipedItemId.value == transaction.id
    
    // 如果當前項目不是正在滑動的項目，但offsetX不為0，則重置offsetX
    LaunchedEffect(CurrentlySwipedItemId.value) {
        if (!isCurrentlySwipedItem && offsetX != 0f) {
            offsetX = 0f
        }
    }
    
    // 滑動動畫
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(durationMillis = 200),
        label = "offsetX"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // 刪除按鈕
        if (isDeleteVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .height(72.dp) // 與卡片高度一致
                    .width(deleteButtonWidth)
                    .background(Color.Red, shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                    .clickable {
                        // 震動反饋
                        try {
                            context.vibrate()
                        } catch (e: Exception) {
                            // 忽略震動錯誤
                        }
                        // 刪除交易
                        viewModel.deleteTransaction(transaction)
                        // 重置偏移和當前滑動項目
                        offsetX = 0f
                        CurrentlySwipedItemId.value = null
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "刪除",
                    tint = Color.White
                )
            }
        }
        
        // 交易卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = animatedOffsetX.dp)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        // 只有當前項目是正在滑動的項目，或者沒有項目正在滑動時，才允許滑動
                        if (isCurrentlySwipedItem || CurrentlySwipedItemId.value == null) {
                            // 設置當前滑動項目
                            if (CurrentlySwipedItemId.value == null && delta < 0) {
                                CurrentlySwipedItemId.value = transaction.id
                            }
                            
                            // 減慢滑動速度，使其更加自然
                            val slowedDelta = delta * 0.5f
                            
                            // 只允許向左滑動（負值）且限制最大滑動距離
                            if (slowedDelta < 0) {
                                // 向左滑動，但不超過刪除按鈕寬度
                                val newOffset = (offsetX + slowedDelta).coerceIn(-deleteButtonWidthPx, 0f)
                                offsetX = newOffset
                            } else if (offsetX < 0) {
                                // 向右回彈，但不超過初始位置
                                val newOffset = (offsetX + slowedDelta).coerceAtMost(0f)
                                offsetX = newOffset
                            }
                        }
                    },
                    onDragStopped = {
                        if (offsetX > -deleteButtonWidthPx / 2) {
                            // 如果滑動距離不夠，回彈
                            coroutineScope.launch {
                                offsetX = 0f
                                // 重置當前滑動項目
                                if (CurrentlySwipedItemId.value == transaction.id) {
                                    CurrentlySwipedItemId.value = null
                                }
                            }
                        } else {
                            // 如果滑動距離足夠，顯示刪除按鈕
                            coroutineScope.launch {
                                offsetX = -deleteButtonWidthPx
                                // 震動反饋
                                try {
                                    context.vibrate()
                                } catch (e: Exception) {
                                    // 忽略震動錯誤
                                }
                            }
                        }
                    }
                )
                .clickable { 
                    if (offsetX == 0f) {
                        expanded = !expanded
                    } else {
                        // 如果卡片已經滑動，點擊時先回彈
                        coroutineScope.launch {
                            offsetX = 0f
                            // 重置當前滑動項目
                            if (CurrentlySwipedItemId.value == transaction.id) {
                                CurrentlySwipedItemId.value = null
                            }
                        }
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Column {
                // 主要內容
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 交易類別圖標
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(getCategoryColor(transaction.getTransactionType()).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(transaction.category),
                            contentDescription = transaction.category,
                            tint = getCategoryColor(transaction.getTransactionType()),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // 交易詳情
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = transaction.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // 顯示類別
                        Text(
                            text = transaction.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    
                    // 交易金額
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = formatAmount(transaction.amount, transaction.getTransactionType()),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (transaction.getTransactionType() == TransactionType.EXPENSE) Color(0xFFfa5252) else Color(0xFF40c057)
                        )
                        
                        // 展開/收起箭頭
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "收起" else "展開",
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(rotationState),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                // 展開的詳細內容
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        
                        // 備註
                        if (transaction.note.isNotEmpty()) {
                            Text(
                                text = "備註",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = transaction.note,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        } else {
                            Text(
                                text = "無備註",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        
                        // 類別
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "類別: ${transaction.category}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        
                        // 如果有圖片，可以在這裡顯示
                        transaction.imageUri?.let { uri ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "附加圖片",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // 這裡可以添加圖片顯示
                        }
                    }
                }
            }
        }
    }
}

private fun formatAmount(amount: Double, type: TransactionType): String {
    return when (type) {
        TransactionType.EXPENSE -> "-¥ ${String.format("%.2f", amount)}"
        TransactionType.INCOME -> "+¥ ${String.format("%.2f", amount)}"
        else -> "¥ ${String.format("%.2f", amount)}"
    }
}

private fun getCategoryColor(type: TransactionType): Color {
    return when (type) {
        TransactionType.EXPENSE -> Color(0xFFfa5252)
        TransactionType.INCOME -> Color(0xFF40c057)
        else -> Color.Gray
    }
}

private fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "餐飲", "午餐", "晚餐", "早餐" -> Icons.Default.Favorite
        "購物", "超市" -> Icons.Default.ShoppingCart
        "收入", "薪資", "獎金", "投資", "禮金", "其他收入" -> Icons.Default.Star
        "咖啡" -> Icons.Default.Info
        else -> Icons.Default.ShoppingCart
    }
}

@Composable
private fun BudgetCard(budgets: List<BudgetMock>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "本月預算",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            budgets.forEach { budget ->
                BudgetItem(budget = budget)
                if (budget != budgets.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun BudgetItem(budget: BudgetMock) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = budget.category,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "¥ ${formatNumber(budget.spent)} / ¥ ${formatNumber(budget.limit)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = (budget.spent / budget.limit).toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = when {
                budget.spent >= budget.limit -> Color(0xFFfa5252)
                budget.spent >= budget.limit * 0.8 -> Color(0xFFfcc419)
                else -> MaterialTheme.colorScheme.primary
            },
        )
    }
}

@Composable
@Preview
fun Previews(){
    BalanceCard(
        monthlyExpense = 100,
        monthlyIncome = 200,
        totalBalance = 2000,
    )

}

private fun formatNumber(number: Double): String {
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.TAIWAN)
    return format.format(amount)
}

// 震動功能
fun Context.vibrate() {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    } catch (e: Exception) {
        // 忽略震動錯誤
    }
} 