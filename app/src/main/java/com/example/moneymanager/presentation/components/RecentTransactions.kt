package com.example.moneymanager.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneymanager.R
import com.example.moneymanager.data.local.entity.TransactionEntity

fun LazyListScope.recentTransactionsSection(
    transactions: List<TransactionEntity>,
    onDeleteTransaction: (TransactionEntity) -> Unit
) {
    item {
        // 最近交易標題
        Text(
            text = stringResource(R.string.recent_transactions),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
    }
    
    // 最近交易列表
    items(transactions) { transaction ->
        TransactionItem(
            transaction = transaction,
            onDelete = onDeleteTransaction
        )
    }
} 