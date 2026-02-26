package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTestScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToTest: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onTestSelected: (String) -> Unit = {},
    viewModel: com.SIMATS.binocularvision.ui.viewmodels.TestViewModel? = null,
    authViewModel: com.SIMATS.binocularvision.ui.viewmodels.AuthViewModel? = null
) {
    val userProfile = authViewModel?.userProfile?.value
    
    fun handleTestSelection(testType: String) {
        val mappedType = when (testType) {
            "fixation_test" -> "RAN"         // Random Fixation
            "quick_screening" -> "VRG"       // Vergance (Basic)
            "full_assessment" -> "PUR"       // Pursuit
            else -> "RAN"
        }
        userProfile?.id?.let { userId ->
            viewModel?.startTest(userId, mappedType)
            onTestSelected(testType)
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "New Test",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "test",
                onNavigateToHome = onNavigateToHome,
                onNavigateToTest = onNavigateToTest,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToSettings = onNavigateToSettings
            )
        },
        containerColor = Color(0xFFF5F5F7)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Select a test type to begin your assessment.",
                color = Color(0xFF546E7A),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            TestCard(
                title = "Fixation Test",
                description = "Specific test for gaze stability and focus duration.",
                icon = Icons.Default.Adjust,
                iconContainerColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF2E7D32),
                onClick = { handleTestSelection("fixation_test") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TestCard(
                title = "Quick Screening",
                description = "2-minute rapid assessment of basic eye coordination.",
                icon = Icons.Default.Visibility,
                iconContainerColor = Color(0xFFE3F2FD),
                iconColor = Color(0xFF2962FF),
                onClick = { handleTestSelection("quick_screening") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TestCard(
                title = "Full Assessment",
                description = "Comprehensive analysis including smooth pursuit and saccades.",
                icon = Icons.Default.Timeline,
                iconContainerColor = Color(0xFFF3E5F5),
                iconColor = Color(0xFF8E24AA),
                onClick = { handleTestSelection("full_assessment") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Preparation Tips
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD).copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Preparation Tips",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2962FF),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TipItem(text = "Clean your front camera lens")
                    TipItem(text = "Remove glasses if possible")
                    TipItem(text = "Ensure you are in a well-lit room")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TestCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconContainerColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0D1B2A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF546E7A),
                    lineHeight = 20.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFCFD8DC)
            )
        }
    }
}

@Composable
fun TipItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(Color(0xFF2962FF))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color(0xFF546E7A)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NewTestScreenPreview() {
    BinocularvisionTheme {
        NewTestScreen()
    }
}
