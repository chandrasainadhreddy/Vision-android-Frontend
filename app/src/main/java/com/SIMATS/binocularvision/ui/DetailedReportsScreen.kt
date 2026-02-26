package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme

@Composable
fun DetailedReportsScreen(onNavigateNext: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F7)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Spacer(modifier = Modifier.weight(0.6f)) // Push content down similar to prev screens

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
                 // Document / Report Icon
                 Icon(
                    imageVector = Icons.Default.List, // Using List for better preview compatibility
                    contentDescription = "Detailed Reports",
                    tint = Color(0xFF2962FF),
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Detailed Reports",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black // Changed to Black as requested
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "Get comprehensive reports on your binocular vision health that you can share with your optometrist.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Black, // Changed to Black as requested
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
                // Inactive Dot 2
                Box(
                    modifier = Modifier
                         .size(8.dp)
                        .background(color = Color(0xFFCFD8DC), shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Active Dot 3 (Pill)
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(8.dp)
                        .background(color = Color(0xFF2962FF), shape = CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Get Started Button (Using same style as next button but different text)
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
                    text = "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailedReportsScreenPreview() {
    BinocularvisionTheme {
        DetailedReportsScreen()
    }
}
