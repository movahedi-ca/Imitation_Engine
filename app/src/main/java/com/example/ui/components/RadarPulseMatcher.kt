package com.example.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidianCard

/**
 * Energy-efficient Radar Pulse Matcher:
 * Automatically freezes transitions when idle (isScanning == false) to conserve battery.
 * Caches static concentric rings and crosshairs using drawWithCache.
 */
@Composable
fun RadarPulseMatcher(
  isScanning: Boolean,
  modifier: Modifier = Modifier
) {
  val (pulseRadiusFraction, pulseAlpha) = if (isScanning) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_scanning")
    val radius by infiniteTransition.animateFloat(
      initialValue = 0.2f,
      targetValue = 1.0f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 2400, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
      ),
      label = "pulse_radius"
    )
    val alpha by infiniteTransition.animateFloat(
      initialValue = 0.7f,
      targetValue = 0.0f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 2400, easing = FastOutLinearInEasing),
        repeatMode = RepeatMode.Restart
      ),
      label = "pulse_alpha"
    )
    Pair(radius, alpha)
  } else {
    Pair(0.4f, 0.0f)
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier.size(240.dp)
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .drawWithCache {
          val centerOffset = Offset(size.width / 2, size.height / 2)
          val maxRadius = size.minDimension / 2
          val ringColor1 = Color(0xFF1E293B)
          val ringColor2 = Color(0xFF24324D)
          val crosshairColor = Color(0x2200E5FF)
          val stroke1Px = 1.dp.toPx()
          val stroke15Px = 1.5.dp.toPx()
          val stroke2Px = 2.dp.toPx()

          onDrawBehind {
            // 1. Static Concentric Grid Rings
            drawCircle(
              color = ringColor1,
              radius = maxRadius * 0.35f,
              center = centerOffset,
              style = Stroke(width = stroke1Px)
            )
            drawCircle(
              color = ringColor1,
              radius = maxRadius * 0.68f,
              center = centerOffset,
              style = Stroke(width = stroke1Px)
            )
            drawCircle(
              color = ringColor2,
              radius = maxRadius * 0.98f,
              center = centerOffset,
              style = Stroke(width = stroke15Px)
            )

            // 2. Crosshairs
            drawLine(
              color = crosshairColor,
              start = Offset(centerOffset.x, 0f),
              end = Offset(centerOffset.x, size.height),
              strokeWidth = stroke1Px
            )
            drawLine(
              color = crosshairColor,
              start = Offset(0f, centerOffset.y),
              end = Offset(size.width, centerOffset.y),
              strokeWidth = stroke1Px
            )

            // 3. Dynamic expanding sonic pulse wave (only rendered during active search)
            if (isScanning && pulseAlpha > 0.01f) {
              val currentRadius = maxRadius * pulseRadiusFraction
              drawCircle(
                brush = Brush.radialGradient(
                  colors = listOf(
                    TuringCyan.copy(alpha = pulseAlpha * 0.5f),
                    TuringCyan.copy(alpha = pulseAlpha * 0.1f),
                    Color.Transparent
                  ),
                  center = centerOffset,
                  radius = currentRadius
                ),
                radius = currentRadius,
                center = centerOffset
              )

              drawCircle(
                color = TuringCyan.copy(alpha = pulseAlpha),
                radius = currentRadius,
                center = centerOffset,
                style = Stroke(width = stroke2Px)
              )
            }
          }
        }
    ) {
      // Handled in drawWithCache onDrawBehind
    }

    // Center Core Node
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .background(TuringObsidianCard)
        .border(2.dp, if (isScanning) TuringCyan else TuringEmerald, CircleShape)
    ) {
      Icon(
        imageVector = Icons.Default.Radar,
        contentDescription = "Matchmaking radar",
        tint = if (isScanning) TuringCyan else TuringEmerald,
        modifier = Modifier.size(32.dp)
      )
    }
  }
}
