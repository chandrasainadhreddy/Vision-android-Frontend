package com.SIMATS.binocularvision.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var testReminders by remember { mutableStateOf(true) }
    var resultsReady by remember { mutableStateOf(true) }
    var weeklySummary by remember { mutableStateOf(false) }
    var healthTips by remember { mutableStateOf(true) }
    var appUpdates by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Notifications", 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF1E293B)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
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
                .padding(24.dp)
        ) {
            Text(
                text = "Manage how you receive notifications and updates from the app.",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            NotificationSettingRow(
                title = "Test Reminders",
                description = "Get reminded to take regular vision assessments",
                checked = testReminders,
                onCheckedChange = { testReminders = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF1F5F9))

            NotificationSettingRow(
                title = "Results Ready",
                description = "Notification when your test results are processed",
                checked = resultsReady,
                onCheckedChange = { resultsReady = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF1F5F9))

            NotificationSettingRow(
                title = "Weekly Summary",
                description = "Receive a weekly summary of your vision health",
                checked = weeklySummary,
                onCheckedChange = { weeklySummary = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF1F5F9))

            NotificationSettingRow(
                title = "Health Tips",
                description = "Get tips for maintaining healthy vision",
                checked = healthTips,
                onCheckedChange = { healthTips = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF1F5F9))

            NotificationSettingRow(
                title = "App Updates",
                description = "Learn about new features and improvements",
                checked = appUpdates,
                onCheckedChange = { appUpdates = it }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Notification Permissions Info Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFEFF6FF) // Light blue background
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Notification Permissions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E40AF)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Make sure notifications are enabled in your device settings to receive alerts.",
                        fontSize = 14.sp,
                        color = Color(0xFF3B82F6),
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.clickable {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                            context.startActivity(intent)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Open Device Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF2563EB)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationSettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                lineHeight = 20.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF2563EB),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE2E8F0),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    BinocularvisionTheme {
        NotificationsScreen(onNavigateBack = {})
    }
}
