package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.R
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme

@Composable
fun OnboardingScreen(onNavigateNext: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Top Logos Row - Positioned exactly at the top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // SES Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_ses_logo),
                    contentDescription = "SES Logo",
                    modifier = Modifier.size(85.dp)
                )
                
                // SIMATS Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_simats_logo),
                    contentDescription = "SIMATS Logo",
                    modifier = Modifier.size(85.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // Central Eye Graphic
            Box(
                modifier = Modifier.size(290.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer light blue background circle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFEBF2FF), CircleShape)
                )
                
                // Inner ring outline
                Canvas(modifier = Modifier.size(240.dp)) {
                    drawCircle(
                        color = Color(0xFFD9E6FF),
                        style = Stroke(width = 4.dp.toPx())
                    )
                }

                // Main Eye Icon
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground_content),
                    contentDescription = "Eye Icon",
                    modifier = Modifier.size(115.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = "Smart Vision\nAnalysis",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Color(0xFF0F172A),
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Description
            Text(
                text = "Detect eye coordination issues early using advanced AI technology right from your phone.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF64748B),
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Get Started Button
            Button(
                onClick = onNavigateNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2962FF),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Get Started",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Footer Text
            Text(
                text = "Powered by SIMATS Engineering",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1E293B)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    BinocularvisionTheme {
        OnboardingScreen(onNavigateNext = {})
    }
}
