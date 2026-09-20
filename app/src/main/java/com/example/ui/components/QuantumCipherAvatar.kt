package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProgressiveStageDefinitions
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringPurple
import kotlin.random.Random

import androidx.compose.ui.graphics.graphicsLayer

private val PrecomputedCos12 = FloatArray(12) { i -> Math.cos(Math.toRadians(i * 30.0)).toFloat() }
private val PrecomputedSin12 = FloatArray(12) { i -> Math.sin(Math.toRadians(i * 30.0)).toFloat() }
private const val Cos45 = 0.70710678f
private const val Sin45 = 0.70710678f

/**
 * Quantum Glitch & Morphing Cryptographic Shader Avatar:
 * Replaces standard blurred circles with a stunning interactive cybernetic iris
 * that has animated cryptographic rings, glyph arcs, reactive noise particle rings,
 * and dynamic unmasking layers.
 */
@Composable
fun QuantumCipherAvatar(
  stage: Int,
  gradientStart: Long = 0xFF6366F1,
  gradientEnd: Long = 0xFF00E5FF,
  size: Dp = 100.dp,
  showRings: Boolean = true,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val stageInfo = ProgressiveStageDefinitions.getInfo(stage)
  val infiniteTransition = rememberInfiniteTransition(label = "cipher_rings")

  val spinClockwise by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(12000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "ring_spin_cw"
  )

  val spinCounterClockwise by infiniteTransition.animateFloat(
    initialValue = 360f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(9000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "ring_spin_ccw"
  )

  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(2400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "iris_pulse"
  )

  val outerDashEffect = remember { PathEffect.dashPathEffect(floatArrayOf(20f, 15f, 40f, 15f), 0f) }
  val innerDashEffect = remember { PathEffect.dashPathEffect(floatArrayOf(8f, 12f), 0f) }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .size(size)
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
  ) {
    if (showRings) {
      // Outer cryptographic dial ring: graphicsLayer executes rotation on RenderNode with 0 relayout
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .graphicsLayer { rotationZ = spinClockwise }
      ) {
        val strokeW = 1.5.dp.toPx()
        val r = (this.size.minDimension / 2f) - (4.dp.toPx())

        // Dashed segmented ring
        drawCircle(
          color = stageInfo.accentColor.copy(alpha = 0.5f),
          radius = r,
          style = Stroke(
            width = strokeW,
            pathEffect = outerDashEffect
          )
        )

        // Cardinal tick markers using precomputed trigonometry
        val tickInset = 6.dp.toPx()
        for (i in 0 until 12) {
          val cos = PrecomputedCos12[i]
          val sin = PrecomputedSin12[i]
          val startX = center.x + (r - tickInset) * cos
          val startY = center.y + (r - tickInset) * sin
          val endX = center.x + r * cos
          val endY = center.y + r * sin

          drawLine(
            color = stageInfo.accentColor.copy(alpha = if (i % 3 == 0) 0.9f else 0.4f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = if (i % 3 == 0) 2.5f else 1.2f
          )
        }
      }

      // Counter-rotating inner glyph orbit using graphicsLayer
      Canvas(
        modifier = Modifier
          .fillMaxSize(0.88f)
          .graphicsLayer { rotationZ = spinCounterClockwise }
      ) {
        val strokeW = 1.dp.toPx()
        val r = (this.size.minDimension / 2f) - (2.dp.toPx())

        drawCircle(
          color = TuringCyan.copy(alpha = 0.35f),
          radius = r,
          style = Stroke(
            width = strokeW,
            pathEffect = innerDashEffect
          )
        )

        // Orbital beacon satellite
        val beaconX = center.x + r * Cos45
        val beaconY = center.y + r * Sin45
        drawCircle(
          color = stageInfo.accentColor,
          radius = 3.dp.toPx(),
          center = Offset(beaconX, beaconY)
        )
      }
    }

    // Core avatar viewport with progressive blur & holographic shader
    Box(
      modifier = Modifier
        .size(size * 0.72f)
        .clip(CircleShape)
        .border(1.5.dp, stageInfo.accentColor.copy(alpha = 0.7f), CircleShape)
        .background(Color(0xFF070B14))
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .progressiveBlurShader(stageInfo.blurRadiusDp)
      ) {
        val w = this.size.width
        val h = this.size.height

        // Ethereal gradient aura
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(gradientEnd),
              Color(gradientStart),
              Color(0xFF030712)
            ),
            center = center,
            radius = w * 0.75f * pulseRadius
          )
        )

        // Synthetic persona facial geometry
        // Torso/Shoulders
        drawOval(
          brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFF1F5F9), Color(0xFF94A3B8).copy(alpha = 0.7f))
          ),
          topLeft = Offset(w * 0.15f, h * 0.68f),
          size = Size(w * 0.70f, h * 0.60f)
        )

        // Head
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0)),
            center = Offset(w * 0.5f, h * 0.42f),
            radius = w * 0.28f
          ),
          radius = w * 0.26f,
          center = Offset(w * 0.5f, h * 0.42f)
        )

        // Distinctive aesthetic accents unmasking with stages
        if (stage >= 1) {
          // Cyber hair / contour silhouette
          drawArc(
            color = Color(0xFF0F172A).copy(alpha = 0.9f),
            startAngle = 160f,
            sweepAngle = 220f,
            useCenter = true,
            topLeft = Offset(w * 0.22f, h * 0.16f),
            size = Size(w * 0.56f, h * 0.46f)
          )
        }

        if (stage >= 2) {
          // Eye-line / neural optic glow
          drawLine(
            color = stageInfo.accentColor,
            start = Offset(w * 0.36f, h * 0.42f),
            end = Offset(w * 0.64f, h * 0.42f),
            strokeWidth = 2.5.dp.toPx()
          )
        }

        if (stage >= 3) {
          // High-frequency biometric nodes
          drawCircle(
            color = TuringEmerald,
            radius = 2.dp.toPx(),
            center = Offset(w * 0.40f, h * 0.42f)
          )
          drawCircle(
            color = TuringEmerald,
            radius = 2.dp.toPx(),
            center = Offset(w * 0.60f, h * 0.42f)
          )
        }
      }

      // Stage Lock Overlay indicator if heavily blurred
      if (stage < 2) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.28f))
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Encrypted Persona",
            tint = stageInfo.accentColor.copy(alpha = 0.85f),
            modifier = Modifier.size(size * 0.22f)
          )
        }
      }
    }
  }
}
