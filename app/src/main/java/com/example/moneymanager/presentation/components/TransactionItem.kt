package com.example.moneymanager.presentation.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
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
    // 使用 Animatable 控制偏移量
    val offsetXAnim = remember { Animatable(0f) }
    val deleteButtonWidth = 80.dp
    val deleteButtonWidthPx = with(density) { deleteButtonWidth.toPx() }
    // 當偏移超過 icon 寬度一半時顯示刪除按鈕
    val isDeleteVisible = offsetXAnim.value < -deleteButtonWidthPx / 2

    // 判斷此卡片是否為目前滑動的那一個
    val isCurrentlySwipedItem = CurrentlySwipedItemId.value == transaction.id

    // 當全局狀態改變（例如其他卡片被觸碰或滑動）時，若本卡片非目前滑動項目且偏移不為 0，則動畫回彈至 0
    LaunchedEffect(CurrentlySwipedItemId.value) {
        if (!isCurrentlySwipedItem && offsetXAnim.value != 0f) {
            offsetXAnim.animateTo(0f, animationSpec = tween(durationMillis = 200))
        }
    }

    Box(
        modifier = modifier.fillMaxWidth())
    {
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
                        coroutineScope.launch {
                            offsetXAnim.animateTo(0f, tween(200))
                        }
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
                .offset(x = offsetXAnim.value.dp)
                .then(
                    if (showSwipeToDelete) {
                        Modifier.draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                if (isCurrentlySwipedItem || CurrentlySwipedItemId.value == null) {
                                    // 當首次向左滑動時設定當前項目
                                    if (CurrentlySwipedItemId.value == null && delta < 0) {
                                        CurrentlySwipedItemId.value = transaction.id
                                    }

                                    val slowedDelta = delta * 0.5f

                                    if (slowedDelta < 0) {
                                        // 限制向左拖動的最大距離不超過 deleteButtonWidthPx
                                        val newOffset = (offsetXAnim.value + slowedDelta)
                                            .coerceIn(-deleteButtonWidthPx, 0f)
                                        coroutineScope.launch {
                                            offsetXAnim.snapTo(newOffset)
                                        }
                                    } else if (offsetXAnim.value < 0) {
                                        // 向右回彈，但不超過 0
                                        val newOffset = (offsetXAnim.value + slowedDelta)
                                            .coerceAtMost(0f)
                                        coroutineScope.launch {
                                            offsetXAnim.snapTo(newOffset)
                                        }
                                    }
                                }
                            },
                            onDragStopped = {
                                if (offsetXAnim.value > -deleteButtonWidthPx / 2) {
                                    coroutineScope.launch {
                                        offsetXAnim.animateTo(0f, tween(200))
                                        if (CurrentlySwipedItemId.value == transaction.id) {
                                            CurrentlySwipedItemId.value = null
                                        }
                                    }
                                } else {
                                    coroutineScope.launch {
                                        offsetXAnim.animateTo(-deleteButtonWidthPx, tween(200))
                                        try {
                                            context.vibrate()
                                        } catch (e: Exception) {
                                            // 忽略震動錯誤
                                        }
                                    }
                                }
                            }
                        )
                    } else Modifier
                )
                .clickable {
                    if (offsetXAnim.value == 0f) {
                        expanded = !expanded
                    } else {
                        // 當卡片有偏移時，點擊先回彈到 0
                        coroutineScope.launch {
                            offsetXAnim.animateTo(0f, tween(200))
                            CurrentlySwipedItemId.value = null
                        }
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                // 主要內容
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 交易類別圖示
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

                    // 交易金額與展開箭頭
                    Column(horizontalAlignment = Alignment.End) {
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

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.category, transaction.category),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        transaction.imageUri?.let { _ ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.attached_image),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
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
        TransactionType.EXPENSE -> "-$ ${String.format(Locale.TAIWAN,"%.2f", amount)}"
        TransactionType.INCOME -> "+$ ${String.format(Locale.TAIWAN,"%.2f", amount)}"
        else -> "$ ${String.format(Locale.TAIWAN,"%.2f", amount)}"
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