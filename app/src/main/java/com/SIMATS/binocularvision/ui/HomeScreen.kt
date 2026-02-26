package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.binocularvision.api.models.HistoryItemRemote
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme
import com.SIMATS.binocularvision.ui.viewmodels.AuthViewModel
import com.SIMATS.binocularvision.ui.viewmodels.HomeViewModel
import com.SIMATS.binocularvision.ui.viewmodels.HomeState
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToTest: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val userProfile by authViewModel.userProfile
    val homeState by homeViewModel.homeState

    LaunchedEffect(userProfile?.id) {
        userProfile?.id?.let { homeViewModel.fetchDashboard(it) }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onNavigateToHome = onNavigateToHome,
                onNavigateToTest = onNavigateToTest,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToSettings = onNavigateToSettings
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        when (val state = homeState) {
            is HomeState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2962FF))
                }
            }
            is HomeState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error: ${state.message}", color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Button(onClick = { userProfile?.id?.let { homeViewModel.fetchDashboard(it) } }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is HomeState.Success -> {
                val dashboard = state.dashboard
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hi, ${userProfile?.name ?: "User"}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D1B2A)
                            )
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE3F2FD))
                                    .clickable { onProfileClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userProfile?.name?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("")?.take(2) ?: "U",
                                    color = Color(0xFF2962FF),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // CTA Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF2962FF), Color(0xFF1565C0))
                                    )
                                )
                                .clickable { onNavigateToTest() }
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Start New Assessment",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Check your vision health",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 13.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Overview Section
                        Text(
                            text = "Overview",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D1B2A)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        val latest = dashboard.latestTest
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OverviewCard(
                                title = "Latest Score",
                                value = if (latest != null) String.format("%.1f%%", latest.percentage) else "N/A",
                                subtitle = latest?.classification ?: "No tests yet",
                                subtitleColor = if (latest?.classification?.lowercase() == "normal") Color(0xFF4CAF50) else Color(0xFFF9A825),
                                icon = Icons.Default.ShowChart,
                                modifier = Modifier.weight(.5f)
                            )
                            OverviewCard(
                                title = "Last Test",
                                value = if (latest != null) {
                                    // Simple logic to show 'Today' or date
                                    "Recent" 
                                } else "--",
                                subtitle = latest?.date ?: "Never",
                                subtitleColor = Color(0xFF90A4AE),
                                icon = Icons.Default.AccessTime,
                                modifier = Modifier.weight(.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Recent Tests Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Tests",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0D1B2A)
                            )
                            Text(
                                text = "View All",
                                color = Color(0xFF2962FF),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { onNavigateToHistory() }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (dashboard.recentTests.isEmpty()) {
                        item {
                            Text(
                                "No recent tests found",
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        items(dashboard.recentTests) { item ->
                            HistoryItemRow(item)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewCard(
    title: String,
    value: String,
    subtitle: String,
    subtitleColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF546E7A),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color(0xFF90A4AE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0D1B2A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = subtitleColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HistoryItemRow(item: HistoryItemRemote) {
    val dbClassification = item.classification?.lowercase() ?: "pending"
    val classification = dbClassification.replaceFirstChar { it.uppercase() }

    val statusColor = when (dbClassification) {
        "normal" -> Color(0xFFE8F5E9)
        "mild issue" -> Color(0xFFFFF8E1)
        "pending" -> Color(0xFFECEFF1)
        else -> Color(0xFFFFEBEE)
    }
    val statusTextColor = when (dbClassification) {
        "normal" -> Color(0xFF2E7D32)
        "mild issue" -> Color(0xFFF9A825)
        "pending" -> Color(0xFF546E7A)
        else -> Color(0xFFD32F2F)
    }

    val title = when(item.testType.lowercase()) {
        "ran" -> "Fixation Test"
        "vrg" -> "Quick Screening"
        "pur" -> "Full Assessment"
        "routine" -> "Routine Check"
        else -> item.testType
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
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
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0D1B2A)
                    )
                    
                    Surface(
                        color = statusColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = classification,
                            color = statusTextColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.date,
                        color = Color(0xFF90A4AE),
                        fontSize = 12.sp
                    )
                    
                    Text(
                        text = String.format("%.1f%%", item.percentage ?: 0.0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0D1B2A)
                    )
                }
            }
        }
    }
}
