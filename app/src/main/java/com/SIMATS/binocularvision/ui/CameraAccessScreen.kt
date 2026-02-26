package com.SIMATS.binocularvision.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme

@Composable
fun CameraAccessScreen(
    onAllowAccess: () -> Unit = {},
    onNotNow: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onAllowAccess()
        } else {
            // If denied, open settings
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F7) // Light gray background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Camera Icon Circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(color = Color(0xFFE3F2FD), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt, // Using default camera icon
                    contentDescription = "Camera",
                    tint = Color(0xFF4285F4), // Blue tint
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Camera Access Needed",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D1B2A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "To assess your binocular vision, this app needs access to your front-facing camera.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF546E7A),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Info Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFF8F9FA), shape = RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "How we use it:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF37474F),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                BulletPoint(text = "Tracking eye movements")
                BulletPoint(text = "Detecting pupil position")
                BulletPoint(text = "Measuring gaze stability")
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Allow Access Button
            Button(
                onClick = {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2962FF)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Allow Camera Access",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Not Now Button
            TextButton(
                onClick = onNotNow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Not Now",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF37474F)
                )
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "•",
            fontSize = 18.sp,
            color = Color(0xFF546E7A),
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF546E7A)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraAccessScreenPreview() {
    BinocularvisionTheme {
        CameraAccessScreen()
    }
}
