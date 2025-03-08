package com.example.moneymanager.presentation.screens.expense

import android.graphics.Rect
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.moneymanager.presentation.theme.Primary

@Composable
fun ImageEditScreen(
    viewModel: AddExpenseViewModel,
    onFinish: () -> Unit
) {
    Log.d("ImageEditScreen", "圖片編輯界面開始渲染")
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val density = LocalDensity.current
    
    // 確保在 UI 狀態變化時重新組合
    LaunchedEffect(uiState.isInImageEditMode) {
        Log.d("ImageEditScreen", "LaunchedEffect: isInImageEditMode = ${uiState.isInImageEditMode}")
    }
    
    // 當前選擇模式
    var currentSelectionType by remember { mutableStateOf(SelectionType.AMOUNT) }
    
    Log.d("ImageEditScreen", "圖片 URI: ${uiState.imageUri}")
    Log.d("ImageEditScreen", "文字塊數量: ${uiState.textBlocks.size}")
    
    // 使用 Surface 作為根元素，確保全屏顯示
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 圖片
            uiState.imageUri?.let { uri ->
                Log.d("ImageEditScreen", "顯示圖片: $uri")
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "收據圖片",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } ?: run {
                Log.e("ImageEditScreen", "圖片 URI 為空")
                // 顯示錯誤信息
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "無法載入圖片",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
            
            // 繪製識別出的文字框和選擇狀態
            if (uiState.textBlocks.isNotEmpty()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                Log.d("ImageEditScreen", "點擊位置: $offset")
                                // 檢測點擊位置是否在文字框內
                                val index = uiState.textBlocks.indexOfFirst { block ->
                                    val rect = block.boundingBox
                                    val left = with(density) { rect.left.toDp().toPx() }
                                    val top = with(density) { rect.top.toDp().toPx() }
                                    val right = with(density) { rect.right.toDp().toPx() }
                                    val bottom = with(density) { rect.bottom.toDp().toPx() }
                                    
                                    val isInside = offset.x in left..right && offset.y in top..bottom
                                    if (isInside) {
                                        Log.d("ImageEditScreen", "點擊文字: ${block.text}")
                                    }
                                    isInside
                                }
                                
                                if (index != -1) {
                                    Log.d("ImageEditScreen", "選擇文字塊: $index, 類型: $currentSelectionType")
                                    viewModel.selectTextBlock(index, currentSelectionType)
                                }
                            }
                        }
                ) {
                    uiState.textBlocks.forEachIndexed { index, block ->
                        val rect = block.boundingBox
                        val left = with(density) { rect.left.toDp().toPx() }
                        val top = with(density) { rect.top.toDp().toPx() }
                        val width = with(density) { (rect.right - rect.left).toDp().toPx() }
                        val height = with(density) { (rect.bottom - rect.top).toDp().toPx() }
                        
                        // 根據選擇類型繪製不同顏色的框
                        val color = when (block.selectionType) {
                            SelectionType.AMOUNT -> Color.Yellow
                            SelectionType.NOTE -> Color.Green
                            else -> Color.White
                        }
                        
                        // 繪製文字框
                        drawRect(
                            color = color,
                            topLeft = Offset(left, top),
                            size = Size(width, height),
                            style = if (block.isSelected) Stroke(width = 4f) else Stroke(width = 2f)
                        )
                        
                        // 如果被選中，填充半透明顏色
                        if (block.isSelected) {
                            drawRect(
                                color = color.copy(alpha = 0.3f),
                                topLeft = Offset(left, top),
                                size = Size(width, height)
                            )
                        }
                    }
                }
            } else {
                Log.e("ImageEditScreen", "沒有識別到文字塊")
                // 顯示提示信息
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "沒有識別到文字",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
            
            // 頂部工具欄
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 關閉按鈕
                IconButton(
                    onClick = {
                        Log.d("ImageEditScreen", "點擊關閉按鈕")
                        viewModel.exitImageEditMode()
                        onFinish()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "關閉",
                        tint = Color.White
                    )
                }
                
                // 完成按鈕
                IconButton(
                    onClick = {
                        Log.d("ImageEditScreen", "點擊完成按鈕")
                        viewModel.finishEditing()
                        onFinish()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "完成",
                        tint = Color.White
                    )
                }
            }
            
            // 底部工具欄
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "選擇文字",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "點擊圖片上的文字進行選擇",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 選擇模式按鈕
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 金額按鈕
                    Button(
                        onClick = { 
                            Log.d("ImageEditScreen", "切換到金額模式")
                            currentSelectionType = SelectionType.AMOUNT 
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentSelectionType == SelectionType.AMOUNT) 
                                Color.Yellow else Color.DarkGray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "金額",
                            color = if (currentSelectionType == SelectionType.AMOUNT) 
                                Color.Black else Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 備註按鈕
                    Button(
                        onClick = { 
                            Log.d("ImageEditScreen", "切換到備註模式")
                            currentSelectionType = SelectionType.NOTE 
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentSelectionType == SelectionType.NOTE) 
                                Color.Green else Color.DarkGray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "備註",
                            color = if (currentSelectionType == SelectionType.NOTE) 
                                Color.Black else Color.White
                        )
                    }
                }
                
                // 已選擇的內容
                if (uiState.amount.isNotEmpty() || uiState.note.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.DarkGray
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "已選擇內容",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            if (uiState.amount.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "金額: ",
                                        color = Color.Yellow,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = uiState.amount,
                                        color = Color.White
                                    )
                                }
                            }
                            
                            if (uiState.note.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "備註: ",
                                        color = Color.Green,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = uiState.note,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 