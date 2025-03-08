package com.example.moneymanager.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneymanager.presentation.components.BottomNavBar
import com.example.moneymanager.presentation.components.BubbleBackground
import com.example.moneymanager.presentation.navigation.Screen
import com.example.moneymanager.presentation.theme.Primary
import com.example.moneymanager.presentation.theme.ThemeManager

@Composable
fun SettingsScreen(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntry
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Settings.route
    
    // 設置選項狀態
    var selectedCurrency by remember { mutableStateOf("TWD") }
    var isDarkTheme by remember { mutableStateOf(ThemeManager.isDarkTheme.value) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    
    // 監聽主題變化
    LaunchedEffect(ThemeManager.isDarkTheme.value) {
        isDarkTheme = ThemeManager.isDarkTheme.value
    }
    
    // 貨幣選項
    val currencies = listOf("TWD", "USD", "EUR", "JPY", "CNY")
    
    // 獲取當前主題的顏色
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1C1E) else Color(0xFFF8F9FA)
    val cardColor = if (isDarkTheme) Color(0xFF2D3033) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val secondaryTextColor = if (isDarkTheme) Color.LightGray else Color.DarkGray
    
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
            // 頂部標題
            Text(
                text = "設置",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            // 基本設置
            Text(
                text = "基本設置",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = secondaryTextColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            BubbleBackground(
                modifier = Modifier.fillMaxWidth(),
                bubbleColor = cardColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // 貨幣設置
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCurrencyDialog = true }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AttachMoney,
                                contentDescription = "貨幣",
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "貨幣",
                                fontSize = 16.sp,
                                color = textColor
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCurrency,
                                fontSize = 16.sp,
                                color = secondaryTextColor
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "選擇",
                                tint = secondaryTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Divider(color = if (isDarkTheme) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f))
                    
                    // 深色主題
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DarkMode,
                                contentDescription = "深色主題",
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "深色主題",
                                fontSize = 16.sp,
                                color = textColor
                            )
                        }
                        
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { 
                                isDarkTheme = it
                                ThemeManager.setDarkTheme(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Primary,
                                checkedTrackColor = Primary.copy(alpha = 0.5f)
                            )
                        )
                    }
                    
                    Divider(color = if (isDarkTheme) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f))
                    
                    // 通知
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "通知",
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "通知",
                                fontSize = 16.sp,
                                color = textColor
                            )
                        }
                        
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Primary,
                                checkedTrackColor = Primary.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 關於
            Text(
                text = "關於",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = secondaryTextColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            BubbleBackground(
                modifier = Modifier.fillMaxWidth(),
                bubbleColor = cardColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // 版本信息
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "版本",
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "版本",
                                fontSize = 16.sp,
                                color = textColor
                            )
                        }
                        
                        Text(
                            text = "1.0.0",
                            fontSize = 16.sp,
                            color = secondaryTextColor
                        )
                    }
                    
                    Divider(color = if (isDarkTheme) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f))
                    
                    // 隱私政策
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* TODO: 導航到隱私政策頁面 */ }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = "隱私政策",
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "隱私政策",
                                fontSize = 16.sp,
                                color = textColor
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "查看",
                            tint = secondaryTextColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Divider(color = if (isDarkTheme) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f))
                    
                    // 使用條款
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* TODO: 導航到使用條款頁面 */ }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = "使用條款",
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "使用條款",
                                fontSize = 16.sp,
                                color = textColor
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "查看",
                            tint = secondaryTextColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            // 底部空間，確保內容不被底部導航欄遮擋
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        // 貨幣選擇對話框
        if (showCurrencyDialog) {
            AlertDialog(
                onDismissRequest = { showCurrencyDialog = false },
                title = {
                    Text(
                        text = "選擇貨幣",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                text = {
                    Column {
                        currencies.forEach { currency ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCurrency = currency
                                        showCurrencyDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedCurrency == currency,
                                    onClick = {
                                        selectedCurrency = currency
                                        showCurrencyDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Primary
                                    )
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = currency,
                                    fontSize = 16.sp,
                                    color = textColor
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showCurrencyDialog = false }
                    ) {
                        Text(
                            text = "取消",
                            color = Primary
                        )
                    }
                },
                containerColor = cardColor,
                shape = RoundedCornerShape(16.dp)
            )
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