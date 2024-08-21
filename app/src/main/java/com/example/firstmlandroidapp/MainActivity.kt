package com.example.firstmlandroidapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

class MainActivity : AppCompatActivity() {
    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnCamera = findViewById<Button>(R.id.btnCamera)
        btnCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, 123)
            } else {
                Toast.makeText(this, "No Camera Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == RESULT_OK) {
            val extras = data?.extras // data receive kra
            val bitmap = extras?.get("data") as? Bitmap
            if (bitmap != null) {
                detectFace(bitmap)
            }
        }
    }

    private fun detectFace(bitmap: Bitmap) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).build()

        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = detector.process(image).addOnSuccessListener { faces ->
            // Task completed successfully
            // ...
            var resultText = ""
            for (face in faces) {

                val bounds = face.boundingBox
                val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                // nose available):
                val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                leftEar?.let {
                    val leftEarPos = leftEar.position
                }

                // If contour detection was enabled:
                val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
                val upperLipBottomContour =
                    face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

                // If classification was enabled:
                if (face.smilingProbability != null) {
                    val smileProb = face.smilingProbability
                }
                if (face.rightEyeOpenProbability != null) {
                    val rightEyeOpenProb = face.rightEyeOpenProbability
                }

                // If face tracking was enabled:
                if (face.trackingId != null) {
                    val id = face.trackingId
                }
                resultText += "$bounds, $rotY, $rotZ, $leftEar, $leftEyeContour, $upperLipBottomContour"

            }
            if (faces.isEmpty()) {
                Toast.makeText(this, "No Face Found", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, resultText, Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            // Task failed with an exception
            // ...
            Toast.makeText(this, "No Face Found", Toast.LENGTH_SHORT).show()
        }
    }
}