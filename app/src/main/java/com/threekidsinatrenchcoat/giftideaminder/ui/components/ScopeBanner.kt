package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.threekidsinatrenchcoat.giftideaminder.ui.state.BudgetSummary
import com.threekidsinatrenchcoat.giftideaminder.ui.state.GiftScope

@Composable
fun ScopeBanner(
    scope: GiftScope,
    budget: BudgetSummary?,
    onAdd: () -> Unit,
    onSuggest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when (scope) {
        is GiftScope.Person -> scope.name
        is GiftScope.Event -> scope.name
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            if (budget != null) {
                val progress = if (budget.budget > 0) (budget.spent / budget.budget).toFloat().coerceIn(0f, 1f) else 0f
                LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                Text("$${"%.0f".format(budget.spent)} / $${"%.0f".format(budget.budget)}",
                    style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onAdd) { Text("Add idea") }
                Button(onClick = onSuggest) { Text("AI suggest") }
            }
        }
    }
}
