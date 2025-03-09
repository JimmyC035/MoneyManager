package com.example.moneymanager.presentation.screens.expense

import android.app.DatePickerDialog
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.moneymanager.data.local.entity.TransactionType
import com.example.moneymanager.presentation.theme.Primary
import com.example.moneymanager.presentation.theme.ThemeManager
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isDarkTheme = ThemeManager.isDarkTheme.value
    
    // 獲取當前主題的顏色
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1C1E) else Color(0xFFF8F9FA)
    val cardColor = if (isDarkTheme) Color(0xFF2D3033) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val secondaryTextColor = if (isDarkTheme) Color.LightGray else Color.DarkGray
    
    // 收集 UI 狀態
    val uiState by viewModel.uiState.collectAsState()
    
    // 相機啟動器
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        Log.d("AddExpenseDialog", "相機結果: $success")
        if (success) {
            Log.d("AddExpenseDialog", "相機拍照成功，開始處理圖片")
            // 使用 viewModelScope 確保在主線程中處理圖片
            viewModel.processImage(context)
            Log.d("AddExpenseDialog", "圖片處理請求已發送")
        } else {
            Log.d("AddExpenseDialog", "相機拍照失敗或取消")
        }
    }
    
    // 權限請求啟動器
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("AddExpenseDialog", "權限結果: $isGranted")
        if (isGranted) {
            viewModel.startCamera(context, cameraLauncher)
        } else {
            viewModel.handlePermissionDenied()
        }
    }
    
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(key1 = true) {
        visible = true
    }
    
    // 監聽 isInImageEditMode 狀態變化
    LaunchedEffect(uiState.isInImageEditMode) {
        Log.d("AddExpenseDialog", "isInImageEditMode 變更: ${uiState.isInImageEditMode}")
    }
    
    // 監聽 shouldLaunchCamera 標誌
    LaunchedEffect(key1 = uiState.shouldLaunchCamera) {
        Log.d("AddExpenseDialog", "shouldLaunchCamera 變更: ${uiState.shouldLaunchCamera}")
        if (uiState.shouldLaunchCamera && uiState.imageUri != null) {
            Log.d("AddExpenseDialog", "啟動相機: ${uiState.imageUri}")
            cameraLauncher.launch(uiState.imageUri)
        }
    }
    
    // 顯示權限拒絕對話框
    if (uiState.showPermissionDeniedDialog) {
        PermissionDeniedDialog(
            onDismiss = { viewModel.dismissPermissionDeniedDialog() },
            onOpenSettings = { 
                viewModel.openAppSettings(context)
                viewModel.dismissPermissionDeniedDialog()
            }
        )
    }
    
    // 顯示圖片編輯界面
    if (uiState.isInImageEditMode) {
        Log.d("AddExpenseDialog", "顯示圖片編輯界面: isInImageEditMode = ${uiState.isInImageEditMode}")
        
        // 直接使用 ImageEditScreen 作為根 Composable
        ImageEditScreen(
            viewModel = viewModel,
            onFinish = {
                Log.d("AddExpenseDialog", "圖片編輯完成")
                viewModel.exitImageEditMode()
            }
        )
        
        // 不顯示對話框
        return
    } else {
        Log.d("AddExpenseDialog", "不顯示圖片編輯界面: isInImageEditMode = ${uiState.isInImageEditMode}")
    }
    
    Dialog(
        onDismissRequest = {
            visible = false
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 頂部欄
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (uiState.transactionType == TransactionType.EXPENSE) "新增支出" else "新增收入",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        
                        IconButton(
                            onClick = {
                                visible = false
                                onDismiss()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "關閉",
                                tint = textColor
                            )
                        }
                    }
                    
                    // 交易類型切換
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TabRow(
                            selectedTabIndex = if (uiState.transactionType == TransactionType.EXPENSE) 0 else 1,
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .clip(RoundedCornerShape(16.dp)),
                            indicator = { tabPositions ->
                                Box {}
                            },
                            divider = { }
                        ) {
                            // 支出標籤
                            Tab(
                                selected = uiState.transactionType == TransactionType.EXPENSE,
                                onClick = { viewModel.setTransactionType(TransactionType.EXPENSE) },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (uiState.transactionType == TransactionType.EXPENSE)
                                            Primary else Color.Transparent
                                    )
                            ) {
                                Text(
                                    text = "支出",
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                                    color = if (uiState.transactionType == TransactionType.EXPENSE) 
                                        Color.White else textColor
                                )
                            }
                            
                            // 收入標籤
                            Tab(
                                selected = uiState.transactionType == TransactionType.INCOME,
                                onClick = { viewModel.setTransactionType(TransactionType.INCOME) },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (uiState.transactionType == TransactionType.INCOME)
                                            Primary else Color.Transparent
                                    )
                            ) {
                                Text(
                                    text = "收入",
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                                    color = if (uiState.transactionType == TransactionType.INCOME) 
                                        Color.White else textColor
                                )
                            }
                        }
                    }
                    
                    // 金額輸入
                    OutlinedTextField(
                        value = uiState.amount,
                        onValueChange = { viewModel.setAmount(it) },
                        label = { Text("金額") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = if (uiState.amountError) Color.Red 
                                else if (isDarkTheme) Color.DarkGray else Color.LightGray,
                            focusedBorderColor = if (uiState.amountError) Color.Red else Primary,
                            unfocusedTextColor = textColor,
                            focusedTextColor = textColor,
                            errorBorderColor = Color.Red,
                            errorTextColor = Color.Red,
                            errorLabelColor = Color.Red
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = uiState.amountError,
                        supportingText = {
                            if (uiState.amountError) {
                                Text(
                                    text = uiState.amountErrorMessage,
                                    color = Color.Red
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 類別選擇
                    ExposedDropdownMenuBox(
                        expanded = uiState.showCategoryDropdown,
                        onExpandedChange = { viewModel.toggleCategoryDropdown() }
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("類別") },
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
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.showCategoryDropdown) }
                        )
                        
                        ExposedDropdownMenu(
                            expanded = uiState.showCategoryDropdown,
                            onDismissRequest = { viewModel.toggleCategoryDropdown() }
                        ) {
                            viewModel.currentCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        viewModel.setCategory(category)
                                        viewModel.toggleCategoryDropdown()
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 日期選擇
                    OutlinedTextField(
                        value = uiState.date,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("日期") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                Log.d("AddExpenseDialog", "點擊日期選擇器")
                                // 使用原生 DatePickerDialog
                                val currentDate = try {
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(uiState.date) ?: Date()
                                } catch (e: Exception) {
                                    Date()
                                }
                                
                                val calendar = Calendar.getInstance().apply {
                                    time = currentDate
                                }
                                
                                val year = calendar.get(Calendar.YEAR)
                                val month = calendar.get(Calendar.MONTH)
                                val day = calendar.get(Calendar.DAY_OF_MONTH)
                                
                                val datePickerDialog = DatePickerDialog(
                                    context,
                                    { _, selectedYear, selectedMonth, selectedDay ->
                                        val selectedCalendar = Calendar.getInstance().apply {
                                            set(selectedYear, selectedMonth, selectedDay)
                                        }
                                        val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            .format(selectedCalendar.time)
                                        viewModel.setDate(selectedDate)
                                    },
                                    year,
                                    month,
                                    day
                                )
                                
                                datePickerDialog.show()
                            },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = if (isDarkTheme) Color.DarkGray else Color.LightGray,
                            focusedBorderColor = Primary,
                            unfocusedTextColor = textColor,
                            focusedTextColor = textColor
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                Log.d("AddExpenseDialog", "點擊日期選擇器圖標")
                                // 使用原生 DatePickerDialog
                                val currentDate = try {
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(uiState.date) ?: Date()
                                } catch (e: Exception) {
                                    Date()
                                }
                                
                                val calendar = Calendar.getInstance().apply {
                                    time = currentDate
                                }
                                
                                val year = calendar.get(Calendar.YEAR)
                                val month = calendar.get(Calendar.MONTH)
                                val day = calendar.get(Calendar.DAY_OF_MONTH)
                                
                                val datePickerDialog = DatePickerDialog(
                                    context,
                                    { _, selectedYear, selectedMonth, selectedDay ->
                                        val selectedCalendar = Calendar.getInstance().apply {
                                            set(selectedYear, selectedMonth, selectedDay)
                                        }
                                        val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            .format(selectedCalendar.time)
                                        viewModel.setDate(selectedDate)
                                    },
                                    year,
                                    month,
                                    day
                                )
                                
                                datePickerDialog.show()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "選擇日期",
                                    tint = Primary
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 備註輸入
                    OutlinedTextField(
                        value = uiState.note,
                        onValueChange = { viewModel.setNote(it) },
                        label = { Text("備註") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = if (isDarkTheme) Color.DarkGray else Color.LightGray,
                            focusedBorderColor = Primary,
                            unfocusedTextColor = textColor,
                            focusedTextColor = textColor
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { 
                                    Log.d("AddExpenseDialog", "點擊相機按鈕")
                                    viewModel.checkCameraPermission(context, permissionLauncher)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "拍照",
                                    tint = Primary
                                )
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 圖片預覽
                    if (uiState.imageUri != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            AsyncImage(
                                model = uiState.imageUri,
                                contentDescription = "收據圖片",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        // 如果有識別的文字，顯示提示
                        if (uiState.fullRecognizedText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (uiState.amount.isNotEmpty() || uiState.note.isNotEmpty())
                                        "已選擇文字作為金額或備註"
                                    else
                                        "已識別文字",
                                    color = Primary,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                
                                TextButton(
                                    onClick = { viewModel.enterImageEditMode() }
                                ) {
                                    Text("重新選擇")
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // 確認按鈕
                    Button(
                        onClick = { 
                            viewModel.saveExpense {
                                // 保存成功時的回調
                                visible = false
                                onSave()
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
                            text = if (uiState.transactionType == TransactionType.EXPENSE) 
                                "確認新增支出" else "確認新增收入",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
} 