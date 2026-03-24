package com.SIMATS.binocularvision.ui

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.camera.core.Preview as CameraPreview
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme
import com.SIMATS.binocularvision.ui.viewmodels.TestViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay
import java.util.concurrent.Executors
import android.util.Log
import kotlin.random.Random
import kotlin.math.*
import androidx.compose.ui.geometry.Offset

@Composable
fun StareAtCenterScreen3(
    onTestComplete: () -> Unit = {},
    onClose: () -> Unit = {},
    viewModel: TestViewModel? = null,
    userId: String = "1",
    testType: String = "Full Assessment"
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var progress by remember { mutableFloatStateOf(0f) }
    var dotPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var screenWidth by remember { mutableIntStateOf(0) }
    var screenHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    // Test duration: 2 minutes (120 seconds)
    val totalTestDuration = 120_000L
    var isTestRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableLongStateOf(0L) }
    var isEyeDetected by remember { mutableStateOf(true) }

    // Face detection
    val faceDetector = remember {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .build()
        )
    }

    // Start test backend session
    LaunchedEffect(Unit) {
        viewModel?.startTest(userId, testType)
        isTestRunning = true
    }

    // Timer and Progress management
    LaunchedEffect(isTestRunning, isEyeDetected) {
        if (isTestRunning && isEyeDetected) {
            val tickInterval = 500L
            while (elapsedTime < totalTestDuration && isEyeDetected) {
                delay(tickInterval)
                if (isEyeDetected) {
                    elapsedTime += tickInterval
                    progress = (elapsedTime.toFloat() / totalTestDuration).coerceAtMost(1f)
                }
                
                if (viewModel?.testState?.value is com.SIMATS.binocularvision.ui.viewmodels.TestState.Finished) {
                    onTestComplete()
                    return@LaunchedEffect
                }
            }
            if (elapsedTime >= totalTestDuration) {
                isTestRunning = false
                onTestComplete()
            }
        }
    }

    // Sinusoidal movement (kept from original)
    LaunchedEffect(screenWidth, screenHeight, isTestRunning) {
        if (screenWidth > 0 && screenHeight > 0 && isTestRunning) {
            val padding = with(density) { 60.dp.toPx() }
            val availableWidth = screenWidth - 2 * padding
            val availableHeight = screenHeight - 2 * padding
            val centerX = screenWidth / 2f
            val centerY = screenHeight / 2f
            val amplitudeX = availableWidth / 2.5f
            val amplitudeY = availableHeight / 3f
            val frequencyX = 0.2f
            val frequencyY = 0.15f
            
            while (isTestRunning && isEyeDetected) {
                val timeSeconds = elapsedTime / 1000f
                dotPosition = Offset(
                    x = centerX + amplitudeX * sin(2.0 * PI * frequencyX * timeSeconds).toFloat(),
                    y = centerY + amplitudeY * cos(2.0 * PI * frequencyY * timeSeconds).toFloat()
                )
                delay(16)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().onGloballyPositioned { coordinates ->
        screenWidth = coordinates.size.width
        screenHeight = coordinates.size.height
    }.background(Color.White)) {
        // Camera Preview for eye tracking
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = CameraPreview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                    val imageAnalysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

                    var sampleCount = 0L
                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null && isTestRunning) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            faceDetector.process(image).addOnSuccessListener { faces ->
                                if (faces.isNotEmpty()) {
                                    val face = faces[0]
                                    val leftEye = face.getLandmark(com.google.mlkit.vision.face.FaceLandmark.LEFT_EYE)?.position
                                    val rightEye = face.getLandmark(com.google.mlkit.vision.face.FaceLandmark.RIGHT_EYE)?.position
                                    if (leftEye != null && rightEye != null) {
                                        isEyeDetected = true
                                        viewModel?.addSample(
                                            n = sampleCount++,
                                            x = (leftEye.x + rightEye.x) / 2f,
                                            y = (leftEye.y + rightEye.y) / 2f,
                                            lx = leftEye.x, ly = leftEye.y, rx = rightEye.x, ry = rightEye.y
                                        )
                                    } else {
                                        isEyeDetected = false
                                    }
                                } else {
                                    isEyeDetected = false
                                }
                            }.addOnFailureListener {
                                isEyeDetected = false
                            }.addOnCompleteListener { imageProxy.close() }
                        } else { imageProxy.close() }
                    }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, imageAnalysis)
                    } catch (e: Exception) { Log.e("StareAtCenter3", "Camera binding failed", e) }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.size(1.dp)
        )

        // Close Button
        IconButton(
            onClick = onClose,
            modifier = Modifier.padding(24.dp).statusBarsPadding().size(40.dp).clip(CircleShape).background(Color(0xFFF1F5F9)).align(Alignment.TopStart)
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF475569))
        }

        // Progress Bar & Header
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp).padding(horizontal = 80.dp).statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = testType, color = Color(0xFF64748B), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = Color(0xFF2563EB),
                trackColor = Color(0xFFF1F5F9),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(4.dp))
            val secondsLeft = ((totalTestDuration - elapsedTime) / 1000).toInt()
            Text(text = "${secondsLeft}s remaining", fontSize = 12.sp, color = Color(0xFF94A3B8))
        }

        // Error Overlay for missing eyes
        if (!isEyeDetected && isTestRunning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Eyes not detecting",
                        color = Color.Red,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Test paused. Please align your face.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // The Moving Dot
        if (dotPosition.x != 0f || dotPosition.y != 0f) {
            Box(
                modifier = Modifier.offset {
                    IntOffset(
                        dotPosition.x.toInt() - with(density) { 15.dp.toPx() }.toInt(),
                        dotPosition.y.toInt() - with(density) { 15.dp.toPx() }.toInt()
                    )
                }.size(30.dp).background(Color(0xFF2563EB), CircleShape).clip(CircleShape)
            )
        }
        
        // Instruction
        Text(
            text = "Follow the smoothly moving dot",
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
            fontSize = 16.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Medium
        )
    }
}
