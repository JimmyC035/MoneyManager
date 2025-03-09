package com.example.moneymanager.presentation.components

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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.moneymanager.data.local.entity.TransactionEntity
import com.example.moneymanager.data.local.entity.TransactionType
import com.example.moneymanager.presentation.theme.Error
import com.example.moneymanager.presentation.theme.Success
import kotlinx.coroutines.launch

// 全局狀態，用於跟踪當前正在滑動的項目ID
private val CurrentlySwipedItemId = mutableStateOf<Long?>(null)

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDelete: (TransactionEntity) -> Unit = {}
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
        modifier = modifier
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
                        onDelete(transaction)
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
                        
                        // 顯示類別而不是日期
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
                            color = if (transaction.getTransactionType() == TransactionType.EXPENSE) Error else Success
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
        TransactionType.EXPENSE -> Error
        TransactionType.INCOME -> Success
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