package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.binocularvision.ui.viewmodels.AuthViewModel
import com.SIMATS.binocularvision.ui.viewmodels.TestViewModel
import com.SIMATS.binocularvision.ui.viewmodels.TestState
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentResultScreen2(
    onBackToHome: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: TestViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val state = viewModel.testState.value
    val result = (state as? TestState.Finished)?.result
    val userId = authViewModel.userProfile.value?.id
    
    // TRUST THE DATABASE: Use values from backend
    val rawClassification = result?.classification?.lowercase() ?: "normal"
    val stability = result?.stability ?: 0.0
    val tracking = result?.tracking ?: 0.0
    val accuracy = result?.accuracy ?: 0.0
    val reaction = result?.reaction ?: 0.0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Assessment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color(0xFFF8FAFC)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 48.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val (label, statusColor, statusBgColor, description, icon) = when (rawClassification) {
                        "normal" -> Quintuple("Normal Vision", Color(0xFF10B981), Color(0xFFECFDF5), "Your binocular coordination is healthy.", Icons.Default.Check)
                        "mild issue" -> Quintuple("Mild Issue", Color(0xFFF59E0B), Color(0xFFFFFBEB), "Minor eye coordination mismatch detected.", Icons.Default.Info)
                        else -> Quintuple("Needs Attention", Color(0xFFEF4444), Color(0xFFFEF2F2), "Significant binocular coordination issue detected.", Icons.Default.Warning)
                    }

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(statusBgColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = label,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = description,
                        fontSize = 15.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Key Metrics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetricCard(
                        title = "Stability",
                        value = String.format("%.0f%%", stability),
                        rating = when {
                            stability >= 95 -> "Excellent"
                            stability >= 85 -> "Average"
                            else -> "Low"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Tracking",
                        value = String.format("%.0f%%", tracking),
                        rating = when {
                            tracking >= 95 -> "Good"
                            tracking >= 85 -> "Average"
                            else -> "Fair"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MetricCard(
                        title = "Accuracy",
                        value = String.format("%.0f%%", accuracy),
                        rating = when {
                            accuracy >= 95 -> "High"
                            accuracy >= 85 -> "Average"
                            else -> "Low"
                        },
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Reaction",
                        value = String.format("%.1f", reaction).take(3),
                        unit = "s",
                        rating = when {
                            stability >= 95 -> "Average"
                            stability >= 85 -> "Fair"
                            else -> "Slow"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (result?.testId != null && userId != null) {
                OutlinedButton(
                    onClick = {
                        viewModel.deleteTest(result.testId, userId)
                        onBackToHome()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                ) {
                    Text(
                        text = "Discard & Delete",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = onBackToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text(
                    text = "Back to Home",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
