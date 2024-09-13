package com.example.sisvita

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sisvita.ui.theme.SISVITATheme
import androidx.compose.ui.*

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SISVITATheme {
                HomeScreen(
                    onEmotionRecognitionClick = {
                        startActivity(Intent(this, CameraActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(onEmotionRecognitionClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onEmotionRecognitionClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Emotion Recognition")
        }
        // Add more buttons here as needed
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SISVITATheme {
        HomeScreen(onEmotionRecognitionClick = {})
    }
}