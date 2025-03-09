package com.example.moneymanager.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.moneymanager.presentation.theme.BubbleColor
import com.example.moneymanager.presentation.theme.Primary

@Composable
fun BubbleBackground(
    modifier: Modifier = Modifier,
    bubbleColor: Color = Primary.copy(alpha = 0.8f),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.clip(RoundedCornerShape(16.dp))) {
        // 背景氣泡
        BubbleEffect(
            modifier = Modifier.fillMaxSize(),
            bubbleColor = BubbleColor,
            bubbleAlpha = 0.9f
        )
        
        // 主要內容
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = bubbleColor
            )
        ) {
            content()
        }
    }
}

@Composable
private fun BubbleEffect(
    modifier: Modifier = Modifier,
    bubbleColor: Color = Color(0xFFE1F3FF),
    bubbleAlpha: Float = 1f
) {
    Box(modifier = modifier) {
        // 氣泡1 - 左上大泡泡
        val infiniteTransition1 = rememberInfiniteTransition(label = "bubble1")
        val offsetY1 by infiniteTransition1.animateFloat(
            initialValue = -40f,
            targetValue = -10f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "bubble1-y"
        )
        val offsetX1 by infiniteTransition1.animateFloat(
            initialValue = -40f,
            targetValue = -5f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "bubble1-x"
        )
        
        Box(
            modifier = Modifier
                .size(160.dp)
                .offset(x = offsetX1.dp, y = offsetY1.dp)
                .alpha(bubbleAlpha)
                .clip(CircleShape)
                .background(bubbleColor)
        )
        
        // 氣泡2 - 右下大泡泡
        val infiniteTransition2 = rememberInfiniteTransition(label = "bubble2")
        val offsetY2 by infiniteTransition2.animateFloat(
            initialValue = 180f,
            targetValue = 160f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "bubble2-y"
        )
        val offsetX2 by infiniteTransition2.animateFloat(
            initialValue = 220f,
            targetValue = 200f,
            animationSpec = infiniteRepeatable(
                animation = tween(3500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "bubble2-x"
        )
        
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(x = offsetX2.dp, y = offsetY2.dp)
                .alpha(bubbleAlpha)
                .clip(CircleShape)
                .background(bubbleColor)
        )
        
        // 氣泡3 - 中間浮動泡泡
        val infiniteTransition3 = rememberInfiniteTransition(label = "bubble3")
        val scale by infiniteTransition3.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "bubble3-scale"
        )
        
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(x = 50.dp, y = 100.dp)
                .scale(scale)
                .alpha(bubbleAlpha)
                .clip(CircleShape)
                .background(bubbleColor)
        )
    }
} 