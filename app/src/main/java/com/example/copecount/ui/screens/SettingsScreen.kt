package com.example.copecount.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.copecount.models.CountdownProfile
import com.example.copecount.ui.CopeCountViewModel
import com.example.copecount.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(viewModel: CopeCountViewModel) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var editingProfile by remember { mutableStateOf<CountdownProfile?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOLED)
            .padding(16.dp)
    ) {
        Text(
            "MANAGE COUNTDOWNS",
            fontSize = 12.sp,
            color = DoveGrey,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 20.dp),
            fontWeight = FontWeight.Bold
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.profiles) { profile ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { viewModel.switchProfile(profile.id) },
                    shape = RoundedCornerShape(24.dp),
                    color = if (viewModel.activeProfileId == profile.id) Color.Black.copy(alpha = 0.4f) else Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        if (viewModel.activeProfileId == profile.id) Gold.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(profile.name, color = Cream, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${profile.startDateStr} to ${profile.endDateStr}", fontSize = 12.sp, color = DoveGrey)
                            if (profile.isNotificationEnabled && profile.reminderDateTimeStr != null) {
                                val dt = LocalDateTime.parse(profile.reminderDateTimeStr)
                                val fmt = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
                                Text("🔔 REMINDER: ${dt.format(fmt)}", fontSize = 10.sp, color = Gold, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        
                        Row {
                            IconButton(onClick = {
                                if (profile.isNotificationEnabled) {
                                    viewModel.cancelReminder(context)
                                } else {
                                    DatePickerDialog(context, { _, y, m, d ->
                                        val pickedDate = LocalDate.of(y, m + 1, d)
                                        TimePickerDialog(context, { _, h, min ->
                                            viewModel.scheduleReminder(context, pickedDate.atTime(h, min))
                                        }, 9, 0, true).show()
                                    }, LocalDate.now().year, LocalDate.now().monthValue - 1, LocalDate.now().dayOfMonth).show()
                                }
                            }) {
                                Icon(
                                    if (profile.isNotificationEnabled) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                    contentDescription = "Reminder",
                                    tint = if (profile.isNotificationEnabled) Gold else Color.White.copy(alpha = 0.6f)
                                )
                            }
                            IconButton(onClick = { 
                                editingProfile = profile
                                showDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = RichPurple)
                            }
                            IconButton(onClick = { viewModel.deleteProfile(profile.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentPink)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { 
                editingProfile = null
                showDialog = true 
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RichPurple),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Text("CREATE NEW COUNTDOWN", color = Color.White, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        }
    }

    if (showDialog) {
        CountdownFormDialog(
            initialProfile = editingProfile,
            onDismiss = { showDialog = false },
            onConfirm = { id, name, start, end, days ->
                if (id == null) {
                    viewModel.addProfile(name, start, end, days)
                } else {
                    viewModel.updateProfile(id, name, start, end, days)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun CountdownFormDialog(
    initialProfile: CountdownProfile?,
    onDismiss: () -> Unit,
    onConfirm: (String?, String, LocalDate, LocalDate, Set<DayOfWeek>) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(initialProfile?.name ?: "") }
    var startDate by remember { mutableStateOf(initialProfile?.startDateStr?.let { LocalDate.parse(it) } ?: LocalDate.now()) }
    var endDate by remember { mutableStateOf(initialProfile?.endDateStr?.let { LocalDate.parse(it) } ?: LocalDate.now().plusMonths(3)) }
    val selectedDays = remember { 
        mutableStateListOf<DayOfWeek>().apply { 
            val days = initialProfile?.workDaysInts?.mapNotNull { 
                try { DayOfWeek.of(it) } catch (e: Exception) { null } 
            } ?: listOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY)
            addAll(days) 
        } 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialProfile == null) "NEW COUNTDOWN" else "EDIT COUNTDOWN", color = Cream, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Cream, 
                        unfocusedTextColor = Cream,
                        focusedBorderColor = RichPurple,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(modifier = Modifier.fillMaxWidth().clickable {
                    DatePickerDialog(context, { _, y, m, d -> startDate = LocalDate.of(y, m + 1, d) }, startDate.year, startDate.monthValue - 1, startDate.dayOfMonth).show()
                }, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("START DATE", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                        Text(startDate.toString(), color = Cream, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Edit, contentDescription = null, tint = RichPurple, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth().clickable {
                    DatePickerDialog(context, { _, y, m, d -> endDate = LocalDate.of(y, m + 1, d) }, endDate.year, endDate.monthValue - 1, endDate.dayOfMonth).show()
                }, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("END DATE", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                        Text(endDate.toString(), color = Cream, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Edit, contentDescription = null, tint = RichPurple, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text("WORK DAYS", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DayOfWeek.values().forEach { day ->
                        val isSelected = selectedDays.contains(day)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Checkbox(
                                checked = isSelected, 
                                onCheckedChange = { if (it) selectedDays.add(day) else selectedDays.remove(day) },
                                colors = CheckboxDefaults.colors(checkedColor = RichPurple, uncheckedColor = Color.White.copy(alpha = 0.2f))
                            )
                            Text(day.name.take(1), fontSize = 10.sp, color = if (isSelected) RichPurple else Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(initialProfile?.id, name, startDate, endDate, selectedDays.toSet()) },
                colors = ButtonDefaults.buttonColors(containerColor = RichPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CONFIRM", color = Color.White, fontWeight = FontWeight.ExtraBold)
            }
        },
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(28.dp)
    )
}
