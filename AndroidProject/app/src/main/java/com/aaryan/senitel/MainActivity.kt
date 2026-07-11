package com.aaryan.senitel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.aaryan.senitel.screens.BootScreen
import com.aaryan.senitel.ui.theme.SenitelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SenitelTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) {
                    BootScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BootScreenPreview() {
    SenitelTheme {
        BootScreen()
    }
}