package com.aaryan.senitel.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.aaryan.senitel.models.Host
import kotlin.math.*
import kotlin.random.Random

@Composable
fun RadarPanel(hosts: List<Host> = emptyList()) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    // Map host IPs to stable random positions
    val hostPositions = remember(hosts) {
        hosts.associate { host ->
            val angle = Random.nextDouble(0.0, 360.0)
            val dist = Random.nextDouble(0.2, 0.85)
            host.ip to Pair(angle.toFloat(), dist.toFloat())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension * 0.48f

            // Techy grid background
            val step = 20.dp.toPx()
            for (x in 0..(size.width / step).toInt()) {
                drawLine(color = Color.White.copy(alpha = 0.03f), start = Offset(x * step, 0f), end = Offset(x * step, size.height))
            }
            for (y in 0..(size.height / step).toInt()) {
                drawLine(color = Color.White.copy(alpha = 0.03f), start = Offset(0f, y * step), end = Offset(size.width, y * step))
            }

            // Circular grid
            for (i in 1..4) {
                drawCircle(color = Color.White.copy(alpha = 0.15f), radius = radius * (i / 4f), center = center, style = Stroke(width = 1f))
            }

            // World map (Simplified dots)
            drawWorldMap(center, radius)

            // Rotating sweep
            rotate(rotation, center) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to Color.Transparent,
                        0.5f to Color(0xFF33AAFF).copy(alpha = 0.3f),
                        1.0f to Color.Transparent,
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
                
                drawLine(
                    color = Color(0xFF33AAFF),
                    start = center,
                    end = Offset(
                        center.x + radius * cos(0f),
                        center.y + radius * sin(0f)
                    ),
                    strokeWidth = 1.5f
                )
            }

            // Discovered host dots
            hostPositions.forEach { (_, pos) ->
                val (angleDeg, dist) = pos
                val angleRad = Math.toRadians(angleDeg.toDouble()).toFloat()
                val dotCenter = Offset(
                    center.x + (radius * dist * cos(angleRad)),
                    center.y + (radius * dist * sin(angleRad))
                )

                // Calculate pulse based on rotation proximity
                val diff = abs(rotation - angleDeg)
                val proximity = if (diff < 20) (20 - diff) / 20f else 0f
                
                val dotColor = Color(0xFF33AAFF)
                drawCircle(
                    color = dotColor.copy(alpha = 0.8f),
                    radius = 3.dp.toPx(),
                    center = dotCenter
                )
                
                if (proximity > 0) {
                    drawCircle(
                        color = dotColor.copy(alpha = 0.4f * proximity),
                        radius = (3.dp.toPx() + 15.dp.toPx() * proximity),
                        center = dotCenter,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawWorldMap(center: Offset, radius: Float) {
    val mapPoints = listOf(
        // North America
        Pair(-100, 40), Pair(-90, 45), Pair(-80, 35), Pair(-110, 50),
        // South America
        Pair(-60, -10), Pair(-55, -20), Pair(-50, -30),
        // Europe
        Pair(10, 50), Pair(20, 55), Pair(30, 60),
        // Africa
        Pair(20, 10), Pair(25, 0), Pair(30, -10),
        // Asia
        Pair(100, 30), Pair(110, 20), Pair(120, 40), Pair(80, 40), Pair(90, 10),
        // Australia
        Pair(135, -25), Pair(140, -30)
    )
    
    mapPoints.forEach { pt ->
        val x = center.x + (pt.first / 180f) * radius
        val y = center.y - (pt.second / 90f) * radius
        drawCircle(
            color = Color.White.copy(alpha = 0.08f),
            radius = 2f,
            center = Offset(x, y)
        )
    }
}
