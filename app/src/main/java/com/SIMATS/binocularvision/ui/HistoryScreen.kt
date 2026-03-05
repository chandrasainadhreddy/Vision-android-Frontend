package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Timeline
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.binocularvision.ui.viewmodels.TestViewModel
import com.SIMATS.binocularvision.ui.viewmodels.AuthViewModel

data class HistoryItem(
    val title: String,
    val date: String,
    val score: String,
    val status: String,
    val statusColor: Color,
    val statusTextColor: Color
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToTest: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    selectedDateRange: String = "All Time",
    selectedTestType: String = "All Types",
    viewModel: TestViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val userId = authViewModel.userProfile.value?.id
    val historyItems by viewModel.filteredHistory
    
    LaunchedEffect(userId) {
        userId?.let { viewModel.fetchHistory(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "History",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onFilterClick) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = Color.Black
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
                currentRoute = "history",
                onNavigateToHome = onNavigateToHome,
                onNavigateToTest = onNavigateToTest,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToSettings = onNavigateToSettings
            )
        },
        containerColor = Color(0xFFF5F5F7)
    ) { paddingValues ->
        if (historyItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No history found", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Recent Assessments",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF90A4AE),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(historyItems) { remoteItem ->
                    val dbClassification = remoteItem.classification?.lowercase() ?: "pending"
                    val percentageValue = remoteItem.percentage ?: 0.0
                    
                    val (statusLabel, statusColor, statusTextColor) = when (dbClassification) {
                        "normal" -> Triple("Normal Vision", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                        "mild issue" -> Triple("Mild Issue", Color(0xFFFFF8E1), Color(0xFFF9A825))
                        "pending" -> Triple("Pending", Color(0xFFECEFF1), Color(0xFF546E7A))
                        else -> Triple(dbClassification.replaceFirstChar { it.uppercase() }, Color(0xFFFFEBEE), Color(0xFFD32F2F))
                    }
                    
                    val title = when(remoteItem.testType.lowercase()) {
                        "ran" -> "Fixation Test"
                        "vrg" -> "Quick Screening"
                        "pur" -> "Full Assessment"
                        "routine" -> "Routine Check"
                        else -> remoteItem.testType
                    }

                    HistoryCard(
                        item = HistoryItem(
                            title = title,
                            date = remoteItem.date,
                            score = String.format("%.1f%%", percentageValue),
                            status = statusLabel,
                            statusColor = statusColor,
                            statusTextColor = statusTextColor
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryCard(
    item: HistoryItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = Color(0xFF2962FF),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0D1B2A)
                    )
                    
                    Surface(
                        color = item.statusColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = item.status,
                            color = item.statusTextColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF90A4AE),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.date,
                            color = Color(0xFF90A4AE),
                            fontSize = 13.sp
                        )
                    }
                    
                    Text(
                        text = item.score,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color(0xFF0D1B2A)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    BinocularvisionTheme {
        HistoryScreen()
    }
}
