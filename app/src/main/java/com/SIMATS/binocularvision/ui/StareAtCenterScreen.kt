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
import androidx.compose.ui.geometry.Offset
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

@Composable
fun StareAtCenterScreen(
    onTestComplete: () -> Unit = {},
    onClose: () -> Unit = {},
    viewModel: TestViewModel? = null,
    userId: String = "1",
    testType: String = "Fixation Test"
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
    val jumpInterval = 1500L
    var isTestRunning by remember { mutableStateOf(false) }
    var startTime by remember { mutableLongStateOf(0L) }

    // Face detection for eye data
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
        startTime = System.currentTimeMillis()
    }

    // Timer and Progress management
    LaunchedEffect(isTestRunning) {
        if (isTestRunning) {
            while (System.currentTimeMillis() - startTime < totalTestDuration) {
                delay(500)
                progress = ((System.currentTimeMillis() - startTime).toFloat() / totalTestDuration)
                
                // If the state moved to Analyzing/Finished elsewhere, stop
                if (viewModel?.testState?.value is com.SIMATS.binocularvision.ui.viewmodels.TestState.Finished) {
                    onTestComplete()
                    return@LaunchedEffect
                }
            }
            // 2 minutes is up!
            isTestRunning = false
            onTestComplete()
        }
    }

    // Random Dot Movement
    LaunchedEffect(screenWidth, screenHeight, isTestRunning) {
        if (screenWidth > 0 && screenHeight > 0 && isTestRunning) {
            while (isTestRunning) {
                val padding = with(density) { 60.dp.toPx() }
                dotPosition = Offset(
                    x = Random.nextFloat() * (screenWidth - 2 * padding) + padding,
                    y = Random.nextFloat() * (screenHeight - 2 * padding) + padding
                )
                delay(jumpInterval)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                screenWidth = coordinates.size.width
                screenHeight = coordinates.size.height
            }
            .background(Color.White)
    ) {
        // 1. Camera Preview (Hidden or small overlay)
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = CameraPreview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    var sampleCount = 0L
                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null && isTestRunning) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            faceDetector.process(image)
                                .addOnSuccessListener { faces ->
                                    if (faces.isNotEmpty()) {
                                        val face = faces[0]
                                        val leftEye = face.getLandmark(com.google.mlkit.vision.face.FaceLandmark.LEFT_EYE)?.position
                                        val rightEye = face.getLandmark(com.google.mlkit.vision.face.FaceLandmark.RIGHT_EYE)?.position
                                        
                                        if (leftEye != null && rightEye != null) {
                                            // Stream sample to backend
                                            viewModel?.addSample(
                                                n = sampleCount++,
                                                x = (leftEye.x + rightEye.x) / 2f, // Simulated gaze x
                                                y = (leftEye.y + rightEye.y) / 2f, // Simulated gaze y
                                                lx = leftEye.x,
                                                ly = leftEye.y,
                                                rx = rightEye.x,
                                                ry = rightEye.y
                                            )
                                        }
                                    }
                                }
                                .addOnCompleteListener { imageProxy.close() }
                        } else {
                            imageProxy.close()
                        }
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, imageAnalysis)
                    } catch (e: Exception) {
                        Log.e("StareAtCenter", "Camera binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.size(1.dp) // Keep it alive but essentially invisible/hidden
        )

        // 2. UI Elements
        // Close Button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .padding(24.dp)
                .statusBarsPadding()
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
                .align(Alignment.TopStart)
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF475569))
        }

        // Progress Bar & Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .padding(horizontal = 80.dp)
                .statusBarsPadding(),
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
            val secondsLeft = (120 - (progress * 120)).toInt()
            Text(text = "${secondsLeft}s remaining", fontSize = 12.sp, color = Color(0xFF94A3B8))
        }

        // The Moving Dot
        if (dotPosition.x != 0f || dotPosition.y != 0f) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            dotPosition.x.toInt() - with(density) { 15.dp.toPx() }.toInt(),
                            dotPosition.y.toInt() - with(density) { 15.dp.toPx() }.toInt()
                        )
                    }
                    .size(30.dp)
                    .background(Color(0xFF2563EB), CircleShape)
                    .clip(CircleShape)
            )
        }
        
        // Calibration / Instruction Text
        Text(
            text = "Keep your gaze on the blue dot",
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
            fontSize = 16.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Medium
        )
    }
}
