package com.example.wls_android.service

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class FullScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FullScreenContent()
        }
    }
}

@Composable
fun FullScreenContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Dies ist eine Vollbildbenachrichtigung", fontSize = 24.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun FullScreenContentPreview() {
    FullScreenContent()
}