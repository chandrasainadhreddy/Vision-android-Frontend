
package com.SIMATS.binocularvision.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToTest: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        val homeIcon = if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home
        val testIcon = if (currentRoute == "test") Icons.Filled.Timeline else Icons.Outlined.Timeline
        val historyIcon = if (currentRoute == "history") Icons.Filled.History else Icons.Outlined.History
        val settingsIcon = if (currentRoute == "settings") Icons.Filled.Settings else Icons.Outlined.Settings

        NavigationBarItem(
            icon = { Icon(homeIcon, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = onNavigateToHome,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2962FF),
                selectedTextColor = Color(0xFF2962FF),
                unselectedIconColor = Color(0xFF90A4AE),
                unselectedTextColor = Color(0xFF90A4AE),
                indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            icon = { Icon(testIcon, contentDescription = "Test") },
            label = { Text("Test") },
            selected = currentRoute == "test",
            onClick = onNavigateToTest,
             colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2962FF),
                selectedTextColor = Color(0xFF2962FF),
                unselectedIconColor = Color(0xFF90A4AE),
                unselectedTextColor = Color(0xFF90A4AE),
                indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            icon = { Icon(historyIcon, contentDescription = "History") },
            label = { Text("History") },
            selected = currentRoute == "history",
            onClick = onNavigateToHistory,
             colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2962FF),
                selectedTextColor = Color(0xFF2962FF),
                unselectedIconColor = Color(0xFF90A4AE),
                unselectedTextColor = Color(0xFF90A4AE),
                indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            icon = { Icon(settingsIcon, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentRoute == "settings",
            onClick = onNavigateToSettings,
             colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2962FF),
                selectedTextColor = Color(0xFF2962FF),
                unselectedIconColor = Color(0xFF90A4AE),
                unselectedTextColor = Color(0xFF90A4AE),
                indicatorColor = Color.White
            )
        )
    }
}
