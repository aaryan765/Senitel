package com.aaryan.senitel.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.aaryan.senitel.models.Host
import com.aaryan.senitel.ui.theme.CtosBlue
import com.aaryan.senitel.ui.theme.CtosSurface
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

    val scanlineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Scanline"
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
            .background(CtosSurface.copy(alpha = 0.3f))
            .border(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension * 0.45f

            drawBackgroundGrid()
            drawCircularGrid(center, radius)
            drawWorldMap(center, radius)
            drawRadarSweep(center, radius, rotation)
            drawHosts(center, radius, hostPositions, rotation)
            drawScanline(scanlineOffset)
            drawVignette()
        }
    }
}

private fun DrawScope.drawBackgroundGrid() {
    val step = 30.dp.toPx()
    val color = Color.White.copy(alpha = 0.02f)
    
    for (x in 0..(size.width / step).toInt()) {
        drawLine(color = color, start = Offset(x * step, 0f), end = Offset(x * step, size.height))
    }
    for (y in 0..(size.height / step).toInt()) {
        drawLine(color = color, start = Offset(0f, y * step), end = Offset(size.width, y * step))
    }
}

private fun DrawScope.drawCircularGrid(center: Offset, radius: Float) {
    val color = Color.White.copy(alpha = 0.1f)
    for (i in 1..4) {
        drawCircle(
            color = color, 
            radius = radius * (i / 4f), 
            center = center, 
            style = Stroke(width = if (i == 4) 2f else 1f)
        )
    }
    
    // Crosshair lines
    drawLine(color = color, start = Offset(center.x - radius, center.y), end = Offset(center.x + radius, center.y))
    drawLine(color = color, start = Offset(center.x, center.y - radius), end = Offset(center.x, center.y + radius))
}

private fun DrawScope.drawRadarSweep(center: Offset, radius: Float, rotation: Float) {
    rotate(rotation, center) {
        drawArc(
            brush = Brush.sweepGradient(
                0.0f to Color.Transparent,
                0.8f to CtosBlue.copy(alpha = 0.2f),
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
            color = CtosBlue.copy(alpha = 0.8f),
            start = center,
            end = Offset(
                center.x + radius * cos(0f),
                center.y + radius * sin(0f)
            ),
            strokeWidth = 2.dp.toPx()
        )
    }
}

private fun DrawScope.drawHosts(
    center: Offset, 
    radius: Float, 
    hostPositions: Map<String, Pair<Float, Float>>,
    currentRotation: Float
) {
    hostPositions.forEach { (_, pos) ->
        val (angleDeg, dist) = pos
        val angleRad = Math.toRadians(angleDeg.toDouble()).toFloat()
        val dotCenter = Offset(
            center.x + (radius * dist * cos(angleRad)),
            center.y + (radius * dist * sin(angleRad))
        )

        // Calculate sweep interaction
        // rotation goes 0-360. angleDeg is 0-360.
        val diff = (currentRotation - angleDeg + 360) % 360
        val proximity = if (diff < 30) (30 - diff) / 30f else 0f
        
        val alpha = 0.2f + (0.8f * proximity)
        val sizeMultiplier = 1f + (0.5f * proximity)

        drawCircle(
            color = CtosBlue.copy(alpha = alpha),
            radius = 3.dp.toPx() * sizeMultiplier,
            center = dotCenter
        )
        
        if (proximity > 0.5f) {
            drawCircle(
                color = CtosBlue.copy(alpha = (proximity - 0.5f) * 2 * 0.3f),
                radius = 3.dp.toPx() + (12.dp.toPx() * (proximity - 0.5f) * 2),
                center = dotCenter,
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

private fun DrawScope.drawWorldMap(center: Offset, radius: Float) {
    val mapPoints = listOf(
        // North America
        Pair(-100, 40), Pair(-90, 45), Pair(-80, 35), Pair(-110, 50), Pair(-120, 55),
        // South America
        Pair(-60, -10), Pair(-55, -20), Pair(-50, -30), Pair(-70, 0),
        // Europe
        Pair(10, 50), Pair(20, 55), Pair(30, 60), Pair(0, 45),
        // Africa
        Pair(20, 10), Pair(25, 0), Pair(30, -10), Pair(15, 20), Pair(10, 5),
        // Asia
        Pair(100, 30), Pair(110, 20), Pair(120, 40), Pair(80, 40), Pair(90, 10), Pair(70, 35),
        // Australia
        Pair(135, -25), Pair(140, -30), Pair(120, -20)
    )
    
    mapPoints.forEach { pt ->
        val x = center.x + (pt.first / 180f) * radius
        val y = center.y - (pt.second / 90f) * radius
        drawRect(
            color = Color.White.copy(alpha = 0.05f),
            topLeft = Offset(x - 1.dp.toPx(), y - 1.dp.toPx()),
            size = Size(2.dp.toPx(), 2.dp.toPx())
        )
    }
}

private fun DrawScope.drawScanline(offset: Float) {
    val y = size.height * offset
    drawLine(
        color = Color.White.copy(alpha = 0.05f),
        start = Offset(0f, y),
        end = Offset(size.width, y),
        strokeWidth = 1.dp.toPx()
    )
}

private fun DrawScope.drawVignette() {
    drawRect(
        brush = Brush.radialGradient(
            0.7f to Color.Transparent,
            1.0f to Color.Black.copy(alpha = 0.5f),
            center = Offset(size.width / 2, size.height / 2),
            radius = size.maxDimension / 1.5f
        ),
        size = size
    )
}
