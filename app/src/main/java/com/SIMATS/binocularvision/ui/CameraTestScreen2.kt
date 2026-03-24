package com.SIMATS.binocularvision.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

@Composable
fun CameraTestScreen2(
    onCancel: () -> Unit = {},
    onEyesDetected: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A232E))
    ) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val faceDetectorOptions = FaceDetectorOptions.Builder()
                            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                            .build()

                        val detector = FaceDetection.getClient(faceDetectorOptions)

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setTargetResolution(Size(1280, 720))
                            .build()

                        var eyesClosedInLastFrame = false
                        var isNavigating = false

                        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val image = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                detector.process(image)
                                    .addOnSuccessListener { faces ->
                                        if (faces.isNotEmpty()) {
                                            val face = faces[0]
                                            val leftProb = face.leftEyeOpenProbability ?: -1f
                                            val rightProb = face.rightEyeOpenProbability ?: -1f
                                            
                                            if (leftProb != -1f && rightProb != -1f) {
                                                if (leftProb < 0.2f && rightProb < 0.2f) {
                                                    eyesClosedInLastFrame = true
                                                    Log.d("CameraTest2", "Eyes Closed Detected")
                                                } else if (eyesClosedInLastFrame && leftProb > 0.7f && rightProb > 0.7f) {
                                                    eyesClosedInLastFrame = false
                                                    if (!isNavigating) {
                                                        isNavigating = true
                                                        Log.d("CameraTest2", "Blink Detected! Navigating...")
                                                        onEyesDetected()
                                                    }
                                                }
                                            }
                                        } else {
                                            eyesClosedInLastFrame = false
                                        }
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }

                        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraTest2", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Camera permission is required",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.4f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(text = "Cancel", fontSize = 14.sp)
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Switch Camera",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .navigationBarsPadding()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0D1218).copy(alpha = 0.8f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Position your face and blink to verify",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Liveness Check: Blink your eyes to continue",
                        color = Color(0xFF90A4AE),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
