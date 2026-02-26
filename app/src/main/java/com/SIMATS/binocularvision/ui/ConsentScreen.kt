package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentScreen(
    onNavigateNext: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    var isChecked by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F7)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Consent & Disclaimer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D1B2A)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF546E7A)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState), // Make content scrollable
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Shield Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = Color(0xFFE3F2FD), // Light Blue
                            shape = RoundedCornerShape(40.dp) // Circle/Rounded
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Security",
                        tint = Color(0xFF2962FF),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Research Disclaimer Title
                Text(
                    text = "Research Disclaimer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D1B2A),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Combined Disclaimer Text
                Text(
                    text = buildAnnotatedString {
                        append("This application is a student engineering project designed for educational and research purposes only.\n\n")
                        
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF37474F))) {
                            append("Not a Medical Device: ")
                        }
                        append("The results provided by this app are not a medical diagnosis. Do not use this app as a substitute for professional medical advice, diagnosis, or treatment.\n\n")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF37474F))) {
                            append("Data Privacy: ")
                        }
                        append("We process camera data locally on your device to analyze eye movements. No images or video are uploaded to any server.\n\n")
                        
                        append("By continuing, you acknowledge that you understand these limitations and agree to participate in this assessment.")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF546E7A), // Blue Gray
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Checkbox Container
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFFF0F4F8), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                        .clickable { isChecked = !isChecked },
                    verticalAlignment = Alignment.Top // Align top for multi-line text
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF2962FF),
                            uncheckedColor = Color(0xFF90A4AE)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I have read and agree to the Terms of Service and Privacy Policy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF37474F),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 12.dp) // Align visually with checkbox
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Continue Button
                Button(
                    onClick = onNavigateNext,
                    enabled = isChecked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2962FF),
                        disabledContainerColor = Color(0xFFB0BEC5),
                        contentColor = Color.White,
                        disabledContentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (isChecked) 4.dp else 0.dp
                    )
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp)) // Bottom padding
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConsentScreenPreview() {
    BinocularvisionTheme {
        ConsentScreen()
    }
}
