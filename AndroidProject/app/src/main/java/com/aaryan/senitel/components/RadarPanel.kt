package com.aaryan.senitel.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun RadarPanel() {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, Color.White)
            .padding(12.dp)
    ) {

        val radius = size.minDimension / 2

        drawCircle(
            color = Color.DarkGray,
            radius = radius,
            style = Stroke(2f)
        )

        drawCircle(
            color = Color.DarkGray,
            radius = radius * 0.7f,
            style = Stroke(2f)
        )

        drawCircle(
            color = Color.DarkGray,
            radius = radius * 0.4f,
            style = Stroke(2f)
        )

        drawLine(
            color = Color.DarkGray,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height)
        )

        drawLine(
            color = Color.DarkGray,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2)
        )
    }
}