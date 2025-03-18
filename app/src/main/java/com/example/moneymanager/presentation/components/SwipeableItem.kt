package com.example.moneymanager.presentation.components

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.moneymanager.R
import kotlinx.coroutines.launch


private val CurrentlySwipedItemId = mutableStateOf<Long?>(null)

@Composable
fun SwipeableItem(
    modifier: Modifier = Modifier,
    id: Long,
    showSwipeToDelete: Boolean = true,
    deleteButtonWidth: Dp = 80.dp,
    onDelete: () -> Unit,
    onItemClick: ()-> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    // 控制水平偏移（單位：像素）
    val offsetXAnim = remember { Animatable(0f) }
    val deleteButtonWidthPx = with(density) { deleteButtonWidth.toPx() }
    // 用來儲存內容高度（單位：px）
    var contentHeightPx by remember { mutableIntStateOf(0) }

    val isCurrentlySwipedItem = (CurrentlySwipedItemId.value == id)

    LaunchedEffect(CurrentlySwipedItemId.value) {
        if (!isCurrentlySwipedItem && offsetXAnim.value != 0f) {
            offsetXAnim.animateTo(0f, tween(durationMillis = 200))
        }
    }


    Box(modifier = modifier.fillMaxWidth()) {
        // 背景刪除 Icon，依據內容高度自動調整高度
        if (showSwipeToDelete && offsetXAnim.value != 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .height(with(density) { contentHeightPx.toDp() })
                    .width(deleteButtonWidth)
                    .background(Color.Red, shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                    .clickable {
                        try {
                            context.vibrate()
                        } catch (e: Exception) { }
                        onDelete()
                        coroutineScope.launch {
                            offsetXAnim.animateTo(0f, tween(200))
                        }
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
        // 前景內容：先測量高度，再應用 offset 及 draggable 手勢
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    contentHeightPx = coordinates.size.height
                }
                .offset { IntOffset(offsetXAnim.value.toInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val slowedDelta = delta * 0.5f
                        if (!isCurrentlySwipedItem && delta < 0) {
                            CurrentlySwipedItemId.value = id
                        }
                        if (slowedDelta < 0) {
                            // 限制向左滑動最大值為 deleteButtonWidthPx
                            val newOffset = (offsetXAnim.value + slowedDelta)
                                .coerceIn(-deleteButtonWidthPx, 0f)
                            coroutineScope.launch { offsetXAnim.snapTo(newOffset) }
                        } else if (offsetXAnim.value < 0) {
                            val newOffset = (offsetXAnim.value + slowedDelta)
                                .coerceAtMost(0f)
                            coroutineScope.launch { offsetXAnim.snapTo(newOffset) }
                        }
                    },
                    onDragStopped = {
                        if (offsetXAnim.value > -deleteButtonWidthPx / 2) {
                            coroutineScope.launch {
                                offsetXAnim.animateTo(0f, tween(200))
                            }
                            if (CurrentlySwipedItemId.value == id) {
                                CurrentlySwipedItemId.value = null
                            }
                        } else {
                            coroutineScope.launch {
                                offsetXAnim.animateTo(-deleteButtonWidthPx, tween(200))
                                try {
                                    context.vibrate()
                                } catch (e: Exception) { }
                            }
                        }
                    }
                ).clickable {
                    if (!isCurrentlySwipedItem) {
                        CurrentlySwipedItemId.value = null
                    }
                    if (offsetXAnim.value != 0f) {
                        coroutineScope.launch {
                            offsetXAnim.animateTo(0f, tween(200))
                            CurrentlySwipedItemId.value = null
                        }
                    } else {
                        onItemClick()
                    }
                }
        ) {
            content()
        }
    }
}
