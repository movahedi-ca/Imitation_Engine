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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Polyline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserVectors
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary

/**
 * Polygonal Neural Spider-Web Radar View:
 * Replaces plain rectangular percentage bars with a 4-axis geometric radar web,
 * overlaying the user's vector polygon against the partner's signature in real time.
 */
@Composable
fun NeuralPolygonRadar(
  userVectors: UserVectors,
  partnerVectors: UserVectors? = null,
  modifier: Modifier = Modifier
) {
  val animProgress = remember { Animatable(0f) }
  LaunchedEffect(userVectors, partnerVectors) {
    animProgress.snapTo(0f)
    animProgress.animateTo(1f, animationSpec = tween(750, easing = FastOutSlowInEasing))
  }

  // Pre-allocated reusable paths to prevent GC pressure and heap thrashing
  val cachedRingPath = remember { Path() }
  val cachedPartnerPath = remember { Path() }
  val cachedUserPath = remember { Path() }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(TuringObsidianCard)
      .border(1.dp, TuringObsidianBorder, RoundedCornerShape(16.dp))
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(TuringCyan.copy(alpha = 0.15f))
      ) {
        Icon(
          imageVector = Icons.Default.Polyline,
          contentDescription = null,
          tint = TuringCyan,
          modifier = Modifier.size(16.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "NEURAL VECTOR TOPOLOGY",
          style = MaterialTheme.typography.labelMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = TuringCyan
          )
        )
        Text(
          text = "Multi-axial psycholinguistic coordinate projection",
          style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 11.sp,
            color = TuringTextMuted
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 4-Axis Spider Web Canvas
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(220.dp)
        .padding(8.dp)
    ) {
      Canvas(modifier = Modifier.size(200.dp)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = (Math.min(w, h) / 2f) - 18.dp.toPx()

        // 4 Cardinal Axis Vectors:
        // Top: Verbosity
        // Right: Empathy
        // Bottom: Latency / Processing
        // Left: Humor
        val axesAngles = listOf(270.0, 0.0, 90.0, 180.0)

        // Draw concentric diamond rings
        val ringFractions = listOf(0.25f, 0.50f, 0.75f, 1.0f)
        ringFractions.forEach { frac ->
          cachedRingPath.reset()
          axesAngles.forEachIndexed { i, deg ->
            val rad = Math.toRadians(deg)
            val px = cx + (radius * frac * Math.cos(rad)).toFloat()
            val py = cy + (radius * frac * Math.sin(rad)).toFloat()
            if (i == 0) cachedRingPath.moveTo(px, py) else cachedRingPath.lineTo(px, py)
          }
          cachedRingPath.close()
          drawPath(
            path = cachedRingPath,
            color = TuringObsidianBorder.copy(alpha = if (frac == 1.0f) 0.8f else 0.4f),
            style = Stroke(
              width = 1.dp.toPx(),
              pathEffect = if (frac < 1f) PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f) else null
            )
          )
        }

        // Draw crosshair axes
        axesAngles.forEach { deg ->
          val rad = Math.toRadians(deg)
          val ex = cx + (radius * Math.cos(rad)).toFloat()
          val ey = cy + (radius * Math.sin(rad)).toFloat()
          drawLine(
            color = TuringObsidianBorder,
            start = Offset(cx, cy),
            end = Offset(ex, ey),
            strokeWidth = 1.dp.toPx()
          )
        }

        // Optional Partner Vector Polygon
        partnerVectors?.let { pv ->
          cachedPartnerPath.reset()
          val pScores = listOf(
            pv.verbosityScore,
            pv.empathyScore,
            (1.0f - (pv.responseLatencyAvgSec / 40f)).coerceIn(0.2f, 0.95f),
            pv.humorIndex
          )
          axesAngles.forEachIndexed { i, deg ->
            val rad = Math.toRadians(deg)
            val score = pScores[i] * animProgress.value
            val px = cx + (radius * score * Math.cos(rad)).toFloat()
            val py = cy + (radius * score * Math.sin(rad)).toFloat()
            if (i == 0) cachedPartnerPath.moveTo(px, py) else cachedPartnerPath.lineTo(px, py)
          }
          cachedPartnerPath.close()
          drawPath(
            path = cachedPartnerPath,
            color = TuringPurple.copy(alpha = 0.2f),
            style = Fill
          )
          drawPath(
            path = cachedPartnerPath,
            color = TuringPurple,
            style = Stroke(width = 1.5.dp.toPx())
          )
        }

        // User Vector Polygon (Cyan radiant mesh)
        val userScores = listOf(
          userVectors.verbosityScore,
          userVectors.empathyScore,
          (1.0f - (userVectors.responseLatencyAvgSec / 40f)).coerceIn(0.2f, 0.95f),
          userVectors.humorIndex
        )

        cachedUserPath.reset()
        axesAngles.forEachIndexed { i, deg ->
          val rad = Math.toRadians(deg)
          val score = (userScores[i] * animProgress.value).coerceIn(0.1f, 1.0f)
          val px = cx + (radius * score * Math.cos(rad)).toFloat()
          val py = cy + (radius * score * Math.sin(rad)).toFloat()
          if (i == 0) cachedUserPath.moveTo(px, py) else cachedUserPath.lineTo(px, py)
        }
        cachedUserPath.close()

        drawPath(
          path = cachedUserPath,
          brush = Brush.radialGradient(
            colors = listOf(TuringCyan.copy(alpha = 0.45f), TuringCyan.copy(alpha = 0.15f)),
            center = Offset(cx, cy),
            radius = radius
          ),
          style = Fill
        )
        drawPath(
          path = cachedUserPath,
          color = TuringCyan,
          style = Stroke(width = 2.dp.toPx())
        )

        // Point Vertices
        axesAngles.forEachIndexed { i, deg ->
          val rad = Math.toRadians(deg)
          val score = (userScores[i] * animProgress.value).coerceIn(0.1f, 1.0f)
          val px = cx + (radius * score * Math.cos(rad)).toFloat()
          val py = cy + (radius * score * Math.sin(rad)).toFloat()
          drawCircle(
            color = TuringCyan,
            radius = 3.5.dp.toPx(),
            center = Offset(px, py)
          )
          drawCircle(
            color = TuringObsidian,
            radius = 1.5.dp.toPx(),
            center = Offset(px, py)
          )
        }
      }
    }

    // Legend
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      LegendPill("VERBOSITY", "${(userVectors.verbosityScore * 100).toInt()}%", TuringCyan)
      LegendPill("EMPATHY", "${(userVectors.empathyScore * 100).toInt()}%", TuringEmerald)
      LegendPill("TEMPO", "${userVectors.responseLatencyAvgSec}s", Color(0xFFF59E0B))
      LegendPill("HUMOR", "${(userVectors.humorIndex * 100).toInt()}%", TuringPurple)
    }
  }
}

@Composable
private fun LegendPill(label: String, value: String, color: Color) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        color = color
      )
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 8.sp,
        color = TuringTextMuted
      )
    )
  }
}
