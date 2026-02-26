package com.SIMATS.binocularvision.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme
import kotlinx.coroutines.delay

@Composable
fun SavingDataScreen(
    onComplete: () -> Unit = {},
    viewModel: com.SIMATS.binocularvision.ui.viewmodels.TestViewModel? = null
) {
    val state = viewModel?.testState?.value
    var progress by remember { mutableFloatStateOf(0f) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        android.util.Log.d("SavingDataScreen", "Screen launched, calling submitData()")
        viewModel?.submitData()
        // Simple progress animation for visual feedback
        while (progress < 0.9f) {
            delay(50)
            progress += 0.02f
        }
    }

    LaunchedEffect(state) {
        android.util.Log.d("SavingDataScreen", "State changed to: $state")
        when (state) {
            is com.SIMATS.binocularvision.ui.viewmodels.TestState.Analyzing,
            is com.SIMATS.binocularvision.ui.viewmodels.TestState.Finished -> {
                progress = 1f
                delay(500)
                android.util.Log.d("SavingDataScreen", "Navigating to next screen")
                onComplete()
            }
            is com.SIMATS.binocularvision.ui.viewmodels.TestState.Error -> {
                android.util.Log.e("SavingDataScreen", "Error state: ${state.message}")
                errorMessage = state.message
            }
            else -> {
                android.util.Log.d("SavingDataScreen", "Other state: $state")
            }
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon Stack
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Circle
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE3F2FD), CircleShape)
                )
                
                // Database Icon
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = Color(0xFF2962FF),
                    modifier = Modifier.size(48.dp)
                )
                
                // Checkmark Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF22C55E), CircleShape)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Text content
            Text(
                text = "Saving Data",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Saving your results...",
                fontSize = 16.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .width(240.dp)
                    .height(8.dp),
                color = Color(0xFF2962FF),
                trackColor = Color(0xFFF1F5F9),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { 
                    errorMessage = null
                    progress = 0f
                    viewModel?.submitData() 
                }) {
                    Text("Retry")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavingDataScreenPreview() {
    BinocularvisionTheme {
        SavingDataScreen()
    }
}
