package com.example.moneymanager.presentation.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneymanager.presentation.components.BottomNavBar
import com.example.moneymanager.presentation.components.TransactionItem
import com.example.moneymanager.presentation.navigation.Screen
import com.example.moneymanager.presentation.screens.expense.AddExpenseDialog
import com.example.moneymanager.presentation.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    navController: NavController,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTime = viewModel.currentTime
    
    var selectedFilter by remember { mutableStateOf("全部") }
    val filters = listOf("全部", "收入", "支出", "餐飲", "購物")
    
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onSave = { showAddExpenseDialog = false }
        )
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "交易記錄",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: 搜索功能 */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController = navController, currentRoute = Screen.Transactions.route) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddExpenseDialog = true },
                containerColor = Primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加交易",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // 全部交易標題和月份選擇
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "全部交易",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = { /* TODO: 月份選擇 */ }) {
                    Text(
                        text = "本月 ▼",
                        color = Primary
                    )
                }
            }
            
            // 過濾選項
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter),
                edgePadding = 0.dp,
                divider = {},
                indicator = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                filters.forEach { filter ->
                    Tab(
                        selected = selectedFilter == filter,
                        onClick = { 
                            selectedFilter = filter
                            viewModel.filterTransactions(filter)
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (selectedFilter == filter) Primary else Color.LightGray.copy(alpha = 0.2f)
                            )
                    ) {
                        Text(
                            text = filter,
                            color = if (selectedFilter == filter) Color.White else Color.Gray,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
            
            // 今天標籤
            Text(
                text = "今天",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // 交易列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 今天的交易
                items(uiState.todayTransactions) { transaction ->
                    TransactionItem(transaction = transaction)
                }
                
                // 昨天標籤
                item {
                    Text(
                        text = "昨天",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // 昨天的交易
                items(uiState.yesterdayTransactions) { transaction ->
                    TransactionItem(transaction = transaction)
                }
                
                // 底部空間
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
} 