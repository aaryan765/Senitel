package com.aaryan.senitel.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Header() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "☰",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 20.sp
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CTOS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "CENTRAL OPERATING SYSTEM",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 8.sp,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = "::",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 20.sp
            )
        }
    }
}
