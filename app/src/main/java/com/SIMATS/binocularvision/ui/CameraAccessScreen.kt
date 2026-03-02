package com.SIMATS.binocularvision.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 60.dp),
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
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = "Camera",
                    tint = Color(0xFF2962FF),
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Title
            Text(
                text = "Camera Access Needed",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "To assess your binocular vision, this app needs access to your front-facing camera.",
                fontSize = 16.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Info Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF8FAFC)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "How we use it:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    BulletPoint(text = "Tracking eye movements")
                    BulletPoint(text = "Detecting pupil position")
                    BulletPoint(text = "Measuring gaze stability")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Allow Access Button
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2962FF)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Allow Camera Access",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
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
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Text(
            text = "•",
            fontSize = 22.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color(0xFF64748B)
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
