package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme

@Composable
fun RealTimeTrackingScreen(
    onNavigateNext: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = onSkip) {
                    Text(
                        text = "Skip",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Icon Container
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .background(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                 // Pulse / Chart Icon
                 Icon(
                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                    contentDescription = "Real-time Tracking",
                    tint = Color(0xFF2962FF),
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Real-time Tracking",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D1B2A)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "Perform simple visual exercises while the camera tracks your eye stability and focus accuracy.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color(0xFF546E7A),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Dots Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Inactive Dot 1
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color = Color(0xFFCFD8DC), shape = CircleShape)
                )
                  Spacer(modifier = Modifier.width(8.dp))
                // Active Dot 2 (Pill)
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(8.dp)
                        .background(color = Color(0xFF2962FF), shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Inactive Dot 3
                 Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color = Color(0xFFCFD8DC), shape = CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Next Button
            Button(
                onClick = onNavigateNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2962FF),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RealTimeTrackingScreenPreview() {
    BinocularvisionTheme {
        RealTimeTrackingScreen()
    }
}
