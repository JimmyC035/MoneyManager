package com.example.moneymanager.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneymanager.R
import com.example.moneymanager.util.BudgetMock
import java.text.NumberFormat
import java.util.*

@Composable
fun BudgetCard(budgets: List<BudgetMock>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.monthly_budget),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            budgets.forEach { budget ->
                BudgetItem(budget = budget)
                if (budget != budgets.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun BudgetItem(budget: BudgetMock) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = budget.category,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "¥ ${formatNumber(budget.spent)} / ¥ ${formatNumber(budget.limit)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = (budget.spent / budget.limit).toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = when {
                budget.spent >= budget.limit -> MaterialTheme.colorScheme.error
                budget.spent >= budget.limit * 0.8 -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.primary
            }
        )
    }
}

private fun formatNumber(number: Double): String {
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
} 