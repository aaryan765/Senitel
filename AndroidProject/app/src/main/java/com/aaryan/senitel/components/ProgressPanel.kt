package com.aaryan.senitel.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressPanel(
    status: String = "READY",
    progress: Float = 0f,
    scanned: Int = 0,
    total: Int = 0,
    elapsedMs: Long = 0
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "SmoothProgress"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (status == "SCANNING") "SCANNING NETWORK..." else status,
            color = Color(0xFF33AAFF),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "192.168.1.0/24",
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SegmentedProgressBar(
                progress = animatedProgress,
                modifier = Modifier.weight(1f).height(12.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun SegmentedProgressBar(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val segmentCount = 40
        val spacing = 2.dp.toPx()
        val segmentWidth = (size.width - (segmentCount - 1) * spacing) / segmentCount
        
        for (i in 0 until segmentCount) {
            val isFilled = i < (progress * segmentCount)
            val color = if (isFilled) Color(0xFF33AAFF) else Color.White.copy(alpha = 0.1f)
            
            drawRoundRect(
                color = color,
                topLeft = Offset(i * (segmentWidth + spacing), 0f),
                size = Size(segmentWidth, size.height),
                cornerRadius = CornerRadius(1.dp.toPx())
            )
        }
    }
}
