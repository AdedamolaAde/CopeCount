package com.example.copecount.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SiwesCountdownScreen(
    daysRemaining: Int,
    totalDays: Int,
    onClockOut: (Int) -> Unit
) {
    var hasClockedOutToday by remember { mutableStateOf(false) }

    val daysClockedOut = totalDays - daysRemaining
    val weeksCompleted = daysClockedOut / 3

    val progress = if (totalDays > 0) daysRemaining.toFloat() / totalDays.toFloat() else 0f

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(250.dp),
                strokeWidth = 16.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(250.dp),
                strokeWidth = 16.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$daysRemaining", fontSize = 64.sp, fontWeight = FontWeight.Bold)
                Text(text = "Days Left", fontSize = 20.sp)
                Text(text = "of $totalDays", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Week $weeksCompleted completed",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Checkbox(
                checked = hasClockedOutToday,
                onCheckedChange = { isChecked ->
                    hasClockedOutToday = isChecked
                    if (isChecked && daysRemaining > 0) {
                        onClockOut(daysRemaining - 1)
                    } else if (!isChecked && daysRemaining < totalDays) {
                        onClockOut(daysRemaining + 1)
                    }
                }
            )
            Text(text = "Clock out for the day", fontSize = 18.sp)
        }
    }
}
