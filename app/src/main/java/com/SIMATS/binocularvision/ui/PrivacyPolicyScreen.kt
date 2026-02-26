package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Privacy Policy",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC) // bg-slate-50
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Shield Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFF6FF)), // bg-blue-50
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF2563EB), // text-blue-600
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Last updated: January 20, 2026",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B), // text-slate-500
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    PrivacySection(
                        title = "1. Introduction",
                        content = "This Privacy Policy explains how the Binocular Vision Assessment app (\"we\", \"our\", or \"the app\") handles your information. This is a student research project designed for educational purposes."
                    )

                    PrivacySection(
                        title = "2. Data Collection",
                        content = "Camera Access: The app uses your device's front camera to track eye movements during assessments. All processing happens locally on your device.\n\nTest Results: Assessment results are stored locally on your device only. No data is transmitted to external servers."
                    )

                    PrivacySection(
                        title = "3. Data Usage",
                        content = "We use the collected data solely to:\n• Perform binocular vision assessments\n• Display your test history and results\n• Track progress over time"
                    )

                    PrivacySection(
                        title = "4. Data Storage",
                        content = "All data is stored locally on your device. No images, videos, or personal information are uploaded to any cloud service or external server. You can delete all data by uninstalling the app."
                    )

                    PrivacySection(
                        title = "5. Third-Party Services",
                        content = "This app does not use any third-party analytics, advertising, or tracking services. Your data remains entirely on your device."
                    )

                    PrivacySection(
                        title = "6. User Rights",
                        content = "You have the right to:\n• Access your stored test results\n• Delete your data at any time\n• Deny camera permissions (though this will limit app functionality)"
                    )

                    PrivacySection(
                        title = "7. Security",
                        content = "Since all processing is local, your data security depends on your device's security measures. We recommend using device lock screens and keeping your device software updated."
                    )

                    PrivacySection(
                        title = "8. Children's Privacy",
                        content = "This app is suitable for all ages. If used by children under 13, parental consent is recommended."
                    )

                    PrivacySection(
                        title = "9. Medical Disclaimer",
                        content = "This app is not a medical device and should not be used for medical diagnosis. Results are for educational and informational purposes only. Always consult a qualified eye care professional for medical advice."
                    )

                    PrivacySection(
                        title = "10. Changes to This Policy",
                        content = "As this is a student project, this privacy policy may be updated. Any changes will be reflected in the app with an updated \"Last modified\" date."
                    )

                    PrivacySection(
                        title = "11. Contact",
                        content = "For questions about this privacy policy or data handling, please contact the development team through the app's feedback feature."
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PrivacySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A), // text-slate-900
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            fontSize = 14.sp,
            color = Color(0xFF334155), // text-slate-700
            lineHeight = 22.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyScreenPreview() {
    BinocularvisionTheme {
        PrivacyPolicyScreen()
    }
}
