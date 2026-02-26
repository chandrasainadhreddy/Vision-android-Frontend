package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme

@Composable
fun FilterTestsScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToTest: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onClose: () -> Unit = {},
    onApplyFilters: (dateRange: String, testType: String) -> Unit = { _, _ -> }
) {
    var selectedDateRange by remember { mutableStateOf("All Time") }
    var selectedTestType by remember { mutableStateOf("All Types") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "history",
                onNavigateToHome = onNavigateToHome,
                onNavigateToTest = onNavigateToTest,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToSettings = onNavigateToSettings
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Tests",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Date Range Section
            FilterSectionHeader(icon = Icons.Default.CalendarToday, title = "Date Range")
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChipCustom(
                    text = "All Time",
                    isSelected = selectedDateRange == "All Time",
                    onClick = { selectedDateRange = "All Time" },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                FilterChipCustom(
                    text = "Today",
                    isSelected = selectedDateRange == "Today",
                    onClick = { selectedDateRange = "Today" },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChipCustom(
                    text = "This Week",
                    isSelected = selectedDateRange == "This Week",
                    onClick = { selectedDateRange = "This Week" },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                FilterChipCustom(
                    text = "This Month",
                    isSelected = selectedDateRange == "This Month",
                    onClick = { selectedDateRange = "This Month" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Test Type Section
            FilterSectionHeader(icon = Icons.Default.Timeline, title = "Test Type")
            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChipFullWidth(
                    text = "All Types",
                    isSelected = selectedTestType == "All Types",
                    onClick = { selectedTestType = "All Types" }
                )
                FilterChipFullWidth(
                    text = "Quick Screening",
                    isSelected = selectedTestType == "Quick Screening",
                    onClick = { selectedTestType = "Quick Screening" }
                )
                FilterChipFullWidth(
                    text = "Full Assessment",
                    isSelected = selectedTestType == "Full Assessment",
                    onClick = { selectedTestType = "Full Assessment" }
                )
                FilterChipFullWidth(
                    text = "Fixation Test",
                    isSelected = selectedTestType == "Fixation Test",
                    onClick = { selectedTestType = "Fixation Test" }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Apply Button centered below test type
            Button(
                onClick = { onApplyFilters(selectedDateRange, selectedTestType) },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF))
            ) {
                Text(
                    text = "Apply Filters",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FilterSectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF546E7A),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF546E7A)
        )
    }
}

@Composable
fun FilterChipCustom(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) Color(0xFF2962FF) else Color(0xFFCFD8DC)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.Transparent,
            contentColor = if (isSelected) Color(0xFF2962FF) else Color(0xFF546E7A)
        )
    ) {
        Text(text = text, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
fun FilterChipFullWidth(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) Color(0xFF2962FF) else Color(0xFFCFD8DC)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.Transparent,
            contentColor = if (isSelected) Color(0xFF2962FF) else Color(0xFF546E7A)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = text,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterTestsScreenPreview() {
    BinocularvisionTheme {
        FilterTestsScreen()
    }
}
