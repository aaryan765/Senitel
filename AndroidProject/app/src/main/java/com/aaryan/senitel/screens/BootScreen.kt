package com.aaryan.senitel.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun BootScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Text(
            text = "SENITEL",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Initializing...",
            color = Color.Gray,
            fontSize = 16.sp
        )

    }

}