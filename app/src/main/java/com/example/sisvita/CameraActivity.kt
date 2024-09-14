package com.example.sisvita

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.camera.view.PreviewView
import android.util.Log
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceDetector

class CameraActivity : ComponentActivity() {

    private lateinit var previewView: PreviewView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Log.e("CameraActivity", "Camera permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        previewView = findViewById(R.id.previewView)

        // Solicita permisos de la cámara
        requestCameraPermission()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Configura la vista previa
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Configura el análisis de imágenes (FaceAnalyzer) sin especificar resolución o aspecto
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // Mantiene solo la última imagen
                .setTargetRotation(previewView.display.rotation) // Asegura la rotación correcta
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(this), FaceAnalyzer()) // Usa FaceAnalyzer
                }

            // Selecciona la cámara frontal
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind previous use cases and bind with new use cases
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis // Vincula la vista previa y el análisis de imágenes
                )
            } catch (exc: Exception) {
                Log.e("CameraActivity", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }


    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera()
        }
    }

    private inner class FaceAnalyzer : ImageAnalysis.Analyzer {
        private val detector: FaceDetector by lazy {
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()
            FaceDetection.getClient(options)
        }

        @SuppressLint("SetTextI18n")
        @OptIn(ExperimentalGetImage::class)
        override fun analyze(image: ImageProxy) {
            @Suppress("UnsafeExperimentalUsageError")
            val mediaImage = image.image
            if (mediaImage != null) {
                val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                detector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        var detectedEmotion: String
                        for (face in faces) {
                            val smileProbability = face.smilingProbability ?: 0.0f
                            val leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0.0f
                            val rightEyeOpenProbability = face.rightEyeOpenProbability ?: 0.0f

                            detectedEmotion = when {
                                smileProbability > 0.5 -> "Feliz"
                                smileProbability < 0.2 && leftEyeOpenProbability < 0.5 && rightEyeOpenProbability < 0.5 -> "Triste"
                                else -> "Neutro"
                            }

                            // Actualiza la UI en el hilo principal
                            runOnUiThread {
                                findViewById<TextView>(R.id.emotionTextView).text = "Emoción: $detectedEmotion"
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FaceAnalyzer", "Face detection failed", e)
                    }
                    .addOnCompleteListener {
                        image.close()
                    }
            }
        }
    }
}