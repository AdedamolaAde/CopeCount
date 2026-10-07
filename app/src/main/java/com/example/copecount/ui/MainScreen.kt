package com.example.copecount.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.copecount.ui.components.*
import com.example.copecount.ui.screens.CountdownFormDialog
import com.example.copecount.ui.screens.SettingsScreen
import com.example.copecount.ui.tabs.ClockInTab
import com.example.copecount.ui.theme.*
import java.time.LocalDate

@Composable
fun MainScreen(viewModel: CopeCountViewModel) {
    var selectedTab by remember { mutableStateOf(1) } // Default to Dashboard

    val isAutomaticCountdown = viewModel.workDays.size == 7

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomBottomSwatch(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                isAutomaticCountdown = isAutomaticCountdown
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(BackgroundOLED)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    0 -> if (!isAutomaticCountdown) ClockInTab(viewModel) else DashboardTab(viewModel)
                    1 -> DashboardTab(viewModel)
                    2 -> SettingsScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun CustomBottomSwatch(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isAutomaticCountdown: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .height(64.dp)
                .wrapContentWidth()
                .clip(RoundedCornerShape(32.dp)),
            color = Color.Black.copy(alpha = 0.3f),
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!isAutomaticCountdown) {
                    SwatchItem(
                        selected = selectedTab == 0,
                        icon = Icons.Default.Fingerprint,
                        label = "Clock In",
                        onClick = { onTabSelected(0) }
                    )
                }
                SwatchItem(
                    selected = selectedTab == 1,
                    icon = Icons.Default.GridView,
                    label = "Dashboard",
                    onClick = { onTabSelected(1) }
                )
                SwatchItem(
                    selected = selectedTab == 2,
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    onClick = { onTabSelected(2) }
                )
            }
        }
    }
}

@Composable
fun SwatchItem(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val containerColor = if (selected) RichPurple.copy(alpha = 0.25f) else Color.Transparent
    val contentColor = if (selected) Gold else Color.White

    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(if (label == "Clock In") 28.dp else 22.dp)
            )
            AnimatedVisibility(visible = selected) {
                Text(
                    text = label,
                    color = contentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardTab(viewModel: CopeCountViewModel) {
    val scrollState = rememberScrollState()
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = 16.dp)
    ) {
        DateHeader(
            startDate = viewModel.startDate,
            endDate = viewModel.endDate,
            onStartClick = { showEditDialog = true },
            onEndClick = { showEditDialog = true }
        )

        val totalWeeks = (viewModel.totalDays / viewModel.workDays.size).coerceAtLeast(1)
        val currentWeek = (viewModel.loggedDaysCount / viewModel.workDays.size).coerceIn(1, 100)
        val percentComplete = if (viewModel.totalDays > 0) 
            ((viewModel.loggedDaysCount.toFloat() / viewModel.totalDays.toFloat()) * 100).toInt() 
            else 0

        CountdownDisplay(
            daysRemaining = viewModel.daysRemaining,
            totalDays = viewModel.totalDays,
            currentWeek = currentWeek,
            totalWeeks = totalWeeks
        )

        WeeklyProgressBar(
            currentWeek = currentWeek,
            totalWeeks = totalWeeks
        )

        AttendanceLog(
            attendanceData = viewModel.loggedDates,
            startDate = viewModel.startDate,
            endDate = viewModel.endDate,
            workDays = viewModel.workDays
        )

        StatsRow(
            logged = viewModel.loggedDaysCount,
            streak = viewModel.streakCount,
            percent = percentComplete
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showEditDialog && viewModel.activeProfile != null) {
        CountdownFormDialog(
            initialProfile = viewModel.activeProfile,
            onDismiss = { showEditDialog = false },
            onConfirm = { id, name, start, end, days ->
                if (id != null) {
                    viewModel.updateProfile(id, name, start, end, days)
                }
                showEditDialog = false
            }
        )
    }
}
