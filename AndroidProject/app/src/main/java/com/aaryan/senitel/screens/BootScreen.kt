package com.aaryan.senitel.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BootScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "SENITEL",
            color = Color.White,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "[ OK ] UI Engine",
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "[ OK ] Secure Storage",
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "[ OK ] Preferences",
            color = Color.White,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Initializing...",
            color = Color.Gray,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace
        )

    }
}