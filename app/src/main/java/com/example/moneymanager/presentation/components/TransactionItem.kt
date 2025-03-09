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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.moneymanager.R
import com.example.moneymanager.data.local.entity.TransactionEntity
import com.example.moneymanager.data.local.entity.TransactionType
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

// 全局狀態，用於跟踪當前正在滑動的項目ID
private val CurrentlySwipedItemId = mutableStateOf<Long?>(null)

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onDelete: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier,
    showSwipeToDelete: Boolean = true
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
            coroutineScope.launch {
                offsetX = 0f
            }
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
        if (showSwipeToDelete && isDeleteVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .height(72.dp)
                    .width(deleteButtonWidth)
                    .background(Color.Red, shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                    .clickable {
                        try {
                            context.vibrate()
                        } catch (e: Exception) {
                            // 忽略震動錯誤
                        }
                        onDelete(transaction)
                        offsetX = 0f
                        CurrentlySwipedItemId.value = null
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = Color.White
                )
            }
        }
        
        // 交易卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = animatedOffsetX.dp)
                .then(
                    if (showSwipeToDelete) {
                        Modifier.draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                if (isCurrentlySwipedItem || CurrentlySwipedItemId.value == null) {
                                    if (CurrentlySwipedItemId.value == null && delta < 0) {
                                        CurrentlySwipedItemId.value = transaction.id
                                    }
                                    
                                    val slowedDelta = delta * 0.5f
                                    
                                    if (slowedDelta < 0) {
                                        val newOffset = (offsetX + slowedDelta).coerceIn(-deleteButtonWidthPx, 0f)
                                        offsetX = newOffset
                                    } else if (offsetX < 0) {
                                        val newOffset = (offsetX + slowedDelta).coerceAtMost(0f)
                                        offsetX = newOffset
                                    }
                                }
                            },
                            onDragStopped = {
                                if (offsetX > -deleteButtonWidthPx / 2) {
                                    coroutineScope.launch {
                                        offsetX = 0f
                                        if (CurrentlySwipedItemId.value == transaction.id) {
                                            CurrentlySwipedItemId.value = null
                                        }
                                    }
                                } else {
                                    coroutineScope.launch {
                                        offsetX = -deleteButtonWidthPx
                                        try {
                                            context.vibrate()
                                        } catch (e: Exception) {
                                            // 忽略震動錯誤
                                        }
                                    }
                                }
                            }
                        )
                    } else {
                        Modifier
                    }
                )
                .clickable {
                    if (offsetX == 0f) {
                        expanded = !expanded
                    } else {
                        // 如果卡片已經有滑動偏移，則回彈到 0
                        coroutineScope.launch {
                            offsetX = 0f
                            CurrentlySwipedItemId.value = null
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
                            color = if (transaction.getTransactionType() == TransactionType.EXPENSE) 
                                Color(0xFFfa5252) else Color(0xFF40c057)
                        )
                        
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) 
                                stringResource(R.string.collapse) else stringResource(R.string.expand),
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
                                text = stringResource(R.string.note),
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
                                text = stringResource(R.string.no_note),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        
                        // 類別
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = stringResource(R.string.category, transaction.category),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        
                        // 如果有圖片，可以在這裡顯示
                        transaction.imageUri?.let { _ ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.attached_image),
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

private fun formatAmount(amount: Double, type: TransactionType): String {
    return when (type) {
        TransactionType.EXPENSE -> "-¥ ${String.format("%.2f", amount)}"
        TransactionType.INCOME -> "+¥ ${String.format("%.2f", amount)}"
        else -> "¥ ${String.format("%.2f", amount)}"
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