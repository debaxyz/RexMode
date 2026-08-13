package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkGridLine
import com.example.ui.theme.RexDarkCrimson

@Composable
fun BackgroundGrid(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Solid dark canvas base
        drawRect(color = DarkBackground)

        // Radial subtle background red glow around the middle top
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    RexDarkCrimson.copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = Offset(width * 0.5f, height * 0.35f),
                radius = width * 0.8f
            ),
            center = Offset(width * 0.5f, height * 0.35f),
            radius = width * 0.8f
        )

        // Grid lines (vertical and horizontal spacing 40dp approx)
        val stepPx = 40.dp.toPx()
        
        var x = 0f
        while (x <= width) {
            drawLine(
                color = DarkGridLine.copy(alpha = 0.4f),
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += stepPx
        }

        var y = 0f
        while (y <= height) {
            drawLine(
                color = DarkGridLine.copy(alpha = 0.4f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += stepPx
        }
    }
}
