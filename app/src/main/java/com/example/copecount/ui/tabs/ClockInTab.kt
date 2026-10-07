package com.example.copecount.ui.tabs

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.copecount.ui.CopeCountViewModel
import com.example.copecount.ui.theme.*
import java.time.LocalDate

@Composable
fun ClockInTab(viewModel: CopeCountViewModel) {
    val isClockedIn = viewModel.loggedDates.contains(LocalDate.now())
    
    // Animation for the pulse effect
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isClockedIn) "Clocked In for Today" else "Tap to Clock In",
            fontSize = 24.sp,
            color = Cream,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(60.dp))
        
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(240.dp)
                .scale(if (isClockedIn) 1f else pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (isClockedIn) 
                            listOf(Gold.copy(alpha = 0.4f), Color.Transparent)
                        else 
                            listOf(RichPurple.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    viewModel.toggleClockIn()
                }
        ) {
            // Inner Core
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = if (isClockedIn)
                                listOf(Gold, Color(0xFFB45309))
                            else
                                listOf(RichPurple, Color(0xFF4C1D95))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Clock Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}
