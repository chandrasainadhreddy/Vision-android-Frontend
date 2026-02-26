package com.SIMATS.binocularvision.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme

data class FAQItem(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToContact: () -> Unit = {}
) {
    val faqs = remember {
        listOf(
            FAQItem(
                question = "What is binocular vision assessment?",
                answer = "Binocular vision assessment evaluates how well your eyes work together as a team. It tests eye coordination, tracking ability, and focus stability to detect potential vision issues."
            ),
            FAQItem(
                question = "How accurate is this AI assessment?",
                answer = "This is a student research project designed for educational purposes. While it uses advanced algorithms, it should not replace professional medical diagnosis. Always consult an eye care professional for accurate diagnosis."
            ),
            FAQItem(
                question = "How long does a test take?",
                answer = "A quick screening takes about 2-3 minutes, while a full assessment takes 5-7 minutes. Make sure you're in a well-lit area and can hold your phone steady."
            ),
            FAQItem(
                question = "Do I need to remove my glasses?",
                answer = "You can perform the test with or without glasses. However, for best results, we recommend testing without glasses if you're comfortable doing so."
            ),
            FAQItem(
                question = "What do the test results mean?",
                answer = "Results are categorized as Normal (healthy coordination), Mild (minor issues detected), or Needs Attention (consult a professional). Each result includes specific recommendations."
            ),
            FAQItem(
                question = "Is my data stored or shared?",
                answer = "All processing happens locally on your device. No images or videos are uploaded to any server. Test results are stored only on your device and are not shared."
            ),
            FAQItem(
                question = "Can I retake a test?",
                answer = "Yes! You can retake tests as many times as you want. We recommend spacing tests at least a few hours apart for accurate tracking of changes."
            ),
            FAQItem(
                question = "What should I do if I get an error?",
                answer = "Common issues include poor lighting or camera access denied. Check your camera permissions, ensure good lighting, and restart the app if needed."
            )
        )
    }

    var openIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Help & FAQ",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Find answers to common questions about the binocular vision assessment app.",
                    color = Color(0xFF475569), // text-slate-600
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            itemsIndexed(faqs) { index, faq ->
                FAQCard(
                    faq = faq,
                    isOpen = openIndex == index,
                    onClick = {
                        openIndex = if (openIndex == index) null else index
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                
                // Still need help section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)), // bg-blue-50
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE)) // border-blue-100
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Still need help?",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF), // text-blue-800
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Contact our support team for additional assistance.",
                            color = Color(0xFF1D4ED8), // text-blue-700
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Contact Support →",
                            color = Color(0xFF2563EB), // text-blue-600
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onNavigateToContact() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FAQCard(
    faq: FAQItem,
    isOpen: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B), // text-slate-800
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp
                )
                Icon(
                    imageVector = if (isOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isOpen) Color(0xFF2563EB) else Color(0xFF94A3B8), // text-blue-600 / text-slate-400
                    modifier = Modifier.size(24.dp)
                )
            }
            
            AnimatedVisibility(visible = isOpen) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = faq.answer,
                        color = Color(0xFF475569), // text-slate-600
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    BinocularvisionTheme {
        HelpScreen()
    }
}
