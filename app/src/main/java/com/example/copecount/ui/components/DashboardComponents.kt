package com.example.copecount.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.copecount.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GlassBorder, Color.Transparent)
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.Black.copy(alpha = 0.2f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
fun DateHeader(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.clickable { onStartClick() }) {
                Text("START", fontSize = 10.sp, color = DoveGrey, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(startDate.format(formatter), fontSize = 15.sp, color = Cream, fontWeight = FontWeight.Medium)
            }
            
            Box(modifier = Modifier.padding(top = 16.dp)) {
                Text("→", color = Gold, fontSize = 20.sp)
            }

            Column(horizontalAlignment = Alignment.End, modifier = Modifier.clickable { onEndClick() }) {
                Text("END", fontSize = 10.sp, color = DoveGrey, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(endDate.format(formatter), fontSize = 15.sp, color = Cream, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun CountdownDisplay(
    daysRemaining: Int,
    totalDays: Int,
    currentWeek: Int,
    totalWeeks: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("DAYS REMAINING", fontSize = 13.sp, color = DoveGrey, letterSpacing = 3.sp, fontWeight = FontWeight.SemiBold)
        
        Text(
            text = daysRemaining.toString(),
            fontSize = 110.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Cream,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "of $totalDays total · week $currentWeek of $totalWeeks complete",
            fontSize = 14.sp,
            color = RichPurple.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun WeeklyProgressBar(
    currentWeek: Int,
    totalWeeks: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (i in 1..totalWeeks) {
                val isActive = i <= currentWeek
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isActive) RichPurple else SurfaceOLEDVariant)
                )
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("WEEK 1", fontSize = 10.sp, color = DoveGrey, fontWeight = FontWeight.Bold)
            Text("• CURRENT WEEK: $currentWeek", fontSize = 10.sp, color = RichPurple, fontWeight = FontWeight.ExtraBold)
            Text("WEEK $totalWeeks", fontSize = 10.sp, color = DoveGrey, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AttendanceLog(
    attendanceData: Set<LocalDate>,
    startDate: LocalDate,
    endDate: LocalDate,
    workDays: Set<DayOfWeek>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        Text("ATTENDANCE LOG", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f), letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.Black.copy(alpha = 0.2f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with Days of Week
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Spacer(modifier = Modifier.width(40.dp)) // Offset for the Day labels
                    val days = listOf("M", "T", "W", "T", "F", "S", "S")
                    days.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                val monthsInRange = getMonthsInRange(startDate, endDate)
                
                monthsInRange.forEach { monthDate ->
                    MonthGrid(
                        monthDate = monthDate,
                        startDate = startDate,
                        endDate = endDate,
                        attendanceData = attendanceData,
                        workDays = workDays
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${attendanceData.size} days logged", fontSize = 11.sp, color = Cream.copy(alpha = 0.7f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(SurfaceOLEDVariant, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.size(8.dp).background(Gold, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ACTIVE DAYS", fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MonthGrid(
    monthDate: LocalDate,
    startDate: LocalDate,
    endDate: LocalDate,
    attendanceData: Set<LocalDate>,
    workDays: Set<DayOfWeek>
) {
    val firstDayOfMonth = monthDate.withDayOfMonth(1)
    val lastDayOfMonth = monthDate.withDayOfMonth(monthDate.lengthOfMonth())
    val monthName = monthDate.format(DateTimeFormatter.ofPattern("MMMM")).uppercase()
    
    // Calculate leading empty cells (offset to Monday)
    val offset = (firstDayOfMonth.dayOfWeek.value - 1)

    Column {
        Text(
            text = monthName,
            fontSize = 10.sp,
            color = Gold,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val totalDays = monthDate.lengthOfMonth()
        val rows = (totalDays + offset + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth().height(24.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // We don't actually need the side labels if we have the top header, 
                // but let's add a small spacer to align with the Row header
                Spacer(modifier = Modifier.width(40.dp)) 

                for (col in 0 until 7) {
                    val dayIndex = row * 7 + col - offset + 1
                    if (dayIndex in 1..totalDays) {
                        val currentDate = monthDate.withDayOfMonth(dayIndex)
                        val isInRange = !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate)
                        val isWorkDay = workDays.contains(currentDate.dayOfWeek)
                        val isLogged = attendanceData.contains(currentDate)
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when {
                                        !isInRange -> Color.Transparent
                                        isLogged -> Gold
                                        isWorkDay -> SurfaceOLEDVariant.copy(alpha = 0.5f)
                                        else -> Color.White.copy(alpha = 0.05f)
                                    }
                                )
                                .border(
                                    width = 0.5.dp,
                                    color = if (currentDate == LocalDate.now() && isInRange) Gold.copy(alpha = 0.5f) else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    } else {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight())
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

fun getMonthsInRange(start: LocalDate, end: LocalDate): List<LocalDate> {
    val months = mutableListOf<LocalDate>()
    var curr = start.withDayOfMonth(1)
    while (!curr.isAfter(end.withDayOfMonth(1))) {
        months.add(curr)
        curr = curr.plusMonths(1)
    }
    return months
}

@Composable
fun StatsRow(
    logged: Int,
    streak: Int,
    percent: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        StatCard(value = logged.toString(), label = "LOGGED", color = Color.White, modifier = Modifier.weight(1f))
        StatCard(value = streak.toString(), label = "STREAK", color = Gold, modifier = Modifier.weight(1f))
        StatCard(value = "$percent%", label = "COMPLETE", color = RichPurple, modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.2f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, fontSize = 9.sp, color = DoveGrey, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        }
    }
}


