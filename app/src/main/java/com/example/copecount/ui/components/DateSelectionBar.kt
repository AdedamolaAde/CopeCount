package com.example.copecount.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateSelectionBar(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartDateSelected: (LocalDate) -> Unit,
    onEndDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    // Helper to launch the native calendar popup
    fun showDatePicker(initialDate: LocalDate, onDatePicked: (LocalDate) -> Unit) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                // Note: Calendar months are 0-indexed, LocalDate is 1-indexed
                onDatePicked(LocalDate.of(year, month + 1, dayOfMonth))
            },
            initialDate.year,
            initialDate.monthValue,
            initialDate.dayOfMonth
        ).show()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Start Date", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            TextButton(onClick = { showDatePicker(startDate, onStartDateSelected) }) {
                Text(text = startDate.format(formatter), fontWeight = FontWeight.Bold)
            }
        }

        Text(text = "to", fontSize = 16.sp)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "End Date", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            TextButton(onClick = { showDatePicker(endDate, onEndDateSelected) }) {
                Text(text = endDate.format(formatter), fontWeight = FontWeight.Bold)
            }
        }
    }
}
