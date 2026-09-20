package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.unit.dp
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringPurple

/**
 * Optimized Cybernetic Ambient Particle Canvas:
 * High-performance, battery-friendly ambient background using drawWithCache,
 * batched point drawing (drawPoints instead of 200+ drawCircle calls),
 * and cached radial gradients to achieve 120fps with minimal GPU power draw.
 */
@Composable
fun CyberneticBackground(
  modifier: Modifier = Modifier,
  accentColor: Color = TuringCyan,
  lowPowerMode: Boolean = true // Default to true (Optimized Mode) to save CPU/GPU resources
) {
  val (scanlineOffset, pulseGlow) = if (!lowPowerMode) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_space")
    val scanline by infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 12000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
      ),
      label = "scanlines"
    )
    val glow by infiniteTransition.animateFloat(
      initialValue = 0.18f,
      targetValue = 0.28f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 6000, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "glow_pulse"
    )
    Pair(scanline, glow)
  } else {
    Pair(0f, 0.22f)
  }

  Canvas(
    modifier = modifier
      .fillMaxSize()
      .drawWithCache {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Precompute static grid points for batch drawing (PointMode.Points)
        val spacing = 64.dp.toPx()
        val cols = (canvasWidth / spacing).toInt()
        val rows = (canvasHeight / spacing).toInt()
        val points = ArrayList<Offset>((cols + 1) * (rows + 1))

        for (c in 0..cols) {
          val x = c * spacing
          for (r in 0..rows) {
            points.add(Offset(x, r * spacing))
          }
        }

        // Cached radial gradients
        val backgroundGradient = Brush.radialGradient(
          colors = listOf(
            Color(0xFF0F172A).copy(alpha = 0.75f),
            TuringObsidian,
            Color(0xFF020408)
          ),
          center = Offset(canvasWidth * 0.5f, canvasHeight * 0.25f),
          radius = canvasWidth * 1.2f
        )

        val aurora1Center = Offset(canvasWidth * 0.85f, canvasHeight * 0.15f)
        val aurora1Radius = canvasWidth * 0.7f
        val aurora2Center = Offset(canvasWidth * 0.15f, canvasHeight * 0.85f)
        val aurora2Radius = canvasWidth * 0.8f

        onDrawBehind {
          // 1. Radial dark space background
          drawRect(brush = backgroundGradient)

          // 2. Subtle Aurora Orbs (single draw pass with cached coordinates)
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(accentColor.copy(alpha = pulseGlow * 0.35f), Color.Transparent),
              center = aurora1Center,
              radius = aurora1Radius
            )
          )

          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(TuringPurple.copy(alpha = pulseGlow * 0.30f), Color.Transparent),
              center = aurora2Center,
              radius = aurora2Radius
            )
          )

          // 3. Batched single-call hardware point drawing (zero per-dot object creation)
          drawPoints(
            points = points,
            pointMode = PointMode.Points,
            color = accentColor.copy(alpha = 0.05f),
            strokeWidth = 2.dp.toPx()
          )

          // 4. Subtle phosphor scanline sweep only if not in low power mode
          if (!lowPowerMode) {
            val scanY = scanlineOffset * canvasHeight
            drawLine(
              brush = Brush.horizontalGradient(
                colors = listOf(
                  Color.Transparent,
                  accentColor.copy(alpha = 0.10f),
                  accentColor.copy(alpha = 0.25f),
                  accentColor.copy(alpha = 0.10f),
                  Color.Transparent
                )
              ),
              start = Offset(0f, scanY),
              end = Offset(canvasWidth, scanY),
              strokeWidth = 1.5.dp.toPx()
            )
          }
        }
      }
  ) {
    // Canvas draw logic handled efficiently in drawWithCache
  }
}

