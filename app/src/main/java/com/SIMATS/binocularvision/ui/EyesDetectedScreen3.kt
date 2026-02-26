package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
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
fun EyesDetectedScreen3(
    onAutoContinue: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    // Automatically continue after 2 seconds
    LaunchedEffect(Unit) {
        delay(2000)
        onAutoContinue()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A232E)) // Dark navy background
    ) {
        // Close Button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .padding(24.dp)
                .statusBarsPadding()
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Main Detection Frame
        Box(
            modifier = Modifier
                .size(width = 280.dp, height = 380.dp)
                .align(Alignment.Center)
                .background(Color.Transparent, shape = RoundedCornerShape(48.dp))
                .padding(2.dp)
        ) {
            // Frame Border
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(48.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, Color(0xFF22C55E).copy(alpha = 0.8f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Center Eye/Target Icon (Double Oval)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(width = 48.dp, height = 32.dp)
                                .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
                                .padding(1.dp)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.Transparent,
                                border = BorderStroke(1.dp, Color(0xFF22C55E))
                            ) {}
                        }
                        
                        Spacer(modifier = Modifier.width(-12.dp)) // Overlap

                        Box(
                            modifier = Modifier
                                .size(width = 48.dp, height = 32.dp)
                                .background(Color.Transparent, shape = RoundedCornerShape(16.dp))
                                .padding(1.dp)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(16.dp),
                                color = Color.Transparent,
                                border = BorderStroke(1.dp, Color(0xFF22C55E))
                            ) {}
                        }
                    }
                }
            }
        }

        // Bottom Success Indicator (Read-only since it's automatic)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .height(56.dp)
                .width(200.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF22C55E)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Eyes Detected",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EyesDetectedScreen3Preview() {
    BinocularvisionTheme {
        EyesDetectedScreen3()
    }
}
