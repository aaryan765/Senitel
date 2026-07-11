package com.aaryan.senitel.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun NavigationPanel() {

    Column(
        modifier = Modifier
            .width(150.dp)
            .border(1.dp, Color.White)
            .padding(8.dp),

        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        MenuItem("OPERATIONS")
        MenuItem("DEVICES")
        MenuItem("REPORTS")
        MenuItem("TOOLS")
        MenuItem("ASSISTANT")
        MenuItem("SETTINGS")

    }

}

@Composable
fun MenuItem(title: String) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White)
            .padding(vertical = 14.dp),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = title,
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )

    }

}

