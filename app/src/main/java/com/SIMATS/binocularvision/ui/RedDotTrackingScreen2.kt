package com.SIMATS.binocularvision.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme
import kotlinx.coroutines.delay

@Composable
fun RedDotTrackingScreen2(
    onTestComplete: () -> Unit = {},
    onClose: () -> Unit = {},
    viewModel: com.SIMATS.binocularvision.ui.viewmodels.TestViewModel? = null
) {
    var step by remember { mutableIntStateOf(0) }

    LaunchedEffect(step) {
        if (step < 5) {
            viewModel?.addSample(step.toLong(), 0.5f, 0.5f, 0.48f, 0.5f, 0.52f, 0.5f)
            delay(1500)
            step++
        } else {
            onTestComplete()
        }
    }

    // Animation for the red dot ping effect
    val infiniteTransition = rememberInfiniteTransition(label = "ping")
    val pingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val pingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1A232E) // Dark blue/slate background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Close Button
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .padding(24.dp)
                    .statusBarsPadding()
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Header Text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Calibration",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Follow the red dot with your eyes",
                    color = Color(0xFF90A4AE), // Lighter slate for contrast
                    fontSize = 14.sp
                )
            }

            // Moving Dot
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
            ) {
                val alignment = when (step) {
                    0 -> Alignment.Center
                    1 -> Alignment.TopStart
                    2 -> Alignment.TopEnd
                    3 -> Alignment.BottomEnd
                    4 -> Alignment.BottomStart
                    else -> Alignment.Center
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(alignment),
                    contentAlignment = Alignment.Center
                ) {
                    // Ping effect
                    Box(
                        modifier = Modifier
                            .size(32.dp * pingScale)
                            .background(Color(0xFFEF4444).copy(alpha = pingAlpha), CircleShape)
                    )
                    
                    // Core Red Dot
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFEF4444), CircleShape)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RedDotTrackingScreen2Preview() {
    BinocularvisionTheme {
        RedDotTrackingScreen2()
    }
}
