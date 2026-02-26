package com.SIMATS.binocularvision.ui

import android.util.Log
import androidx.camera.core.*
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

@Composable
fun RedDotTrackingScreen(
    onTestComplete: () -> Unit = {},
    onClose: () -> Unit = {},
    viewModel: com.SIMATS.binocularvision.ui.viewmodels.TestViewModel? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var step by remember { mutableIntStateOf(0) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    // Face Detector Configuration
    val options = FaceDetectorOptions.Builder()
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
    val detector = remember { FaceDetection.getClient(options) }

    // Logic to advance steps
    LaunchedEffect(step) {
        if (step < 5) {
            delay(3000) // Give 3 seconds per point to track
            step++
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1A232E)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Hidden Camera View for Processing
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(ctx)
                    
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            processImageProxy(detector, imageProxy, step, viewModel)
                        }

                        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner, cameraSelector, preview, imageAnalysis
                            )
                        } catch (exc: Exception) {
                            Log.e("Camera", "Use case binding failed", exc)
                        }
                    }, androidx.core.content.ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.size(1.dp) // Keep it hidden but active
            )

            // UI Elements (Close Button, Header, Moving Dot)
            IconButton(
                onClick = onClose,
                modifier = Modifier.padding(24.dp).statusBarsPadding().size(40.dp)
                    .clip(CircleShape).background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }

            // Header
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Calibration", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Follow the red dot with your eyes", color = Color(0xFF90A4AE), fontSize = 14.sp)
            }

            // Moving Dot with Ping Animation
            val alignment = when (step) {
                0 -> Alignment.Center
                1 -> Alignment.TopStart
                2 -> Alignment.TopEnd
                3 -> Alignment.BottomEnd
                4 -> Alignment.BottomStart
                else -> Alignment.Center
            }

            if (step < 5) {
                Box(modifier = Modifier.fillMaxSize().padding(60.dp)) {
                    DotWithAnimation(modifier = Modifier.align(alignment))
                }
            }

            if (step >= 5) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.BottomCenter) {
                    Button(
                        onClick = onTestComplete,
                        modifier = Modifier.fillMaxWidth().height(56.dp).navigationBarsPadding(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF))
                    ) {
                        Text("Start Fixation Test", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    detector: com.google.mlkit.vision.face.FaceDetector,
    imageProxy: ImageProxy,
    step: Int,
    viewModel: com.SIMATS.binocularvision.ui.viewmodels.TestViewModel?
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null && step < 5) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        detector.process(image)
            .addOnSuccessListener { faces ->
                for (face in faces) {
                    val leftEye = face.getLandmark(com.google.mlkit.vision.face.FaceLandmark.LEFT_EYE)
                    val rightEye = face.getLandmark(com.google.mlkit.vision.face.FaceLandmark.RIGHT_EYE)
                    
                    if (leftEye != null && rightEye != null) {
                        // Logic similar to your Python script: Capture coordinates
                        viewModel?.addSample(
                            n = step.toLong(),
                            x = face.boundingBox.centerX().toFloat(),
                            y = face.boundingBox.centerY().toFloat(),
                            lx = leftEye.position.x,
                            ly = leftEye.position.y,
                            rx = rightEye.position.x,
                            ry = rightEye.position.y
                        )
                    }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    } else {
        imageProxy.close()
    }
}

@Composable
fun DotWithAnimation(modifier: Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ping")
    val pingScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 2.5f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Restart), label = ""
    )
    Box(modifier = modifier.size(32.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(32.dp * pingScale).background(Color(0xFFEF4444).copy(alpha = 0.3f), CircleShape))
        Box(modifier = Modifier.size(32.dp).background(Color(0xFFEF4444), CircleShape))
    }
}
