package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.TuringObsidianBorder

@Composable
fun ProgressiveBlurAvatar(
  stage: Int,
  gradientStart: Long = 0xFF6366F1,
  gradientEnd: Long = 0xFF00E5FF,
  size: Dp = 88.dp,
  showBadge: Boolean = true,
  modifier: Modifier = Modifier
) {
  val stageInfo = ProgressiveStageDefinitions.getInfo(stage)

  // Animated blur radius based on progressive reveal stage
  val targetBlur = stageInfo.blurRadiusDp
  val animatedBlur by animateFloatAsState(
    targetValue = targetBlur,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "blur_anim"
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(size)
    ) {
      // Outer Aperture Ring
      Canvas(modifier = Modifier.size(size)) {
        val strokeWidth = 2.dp.toPx()
        val radius = (this.size.minDimension - strokeWidth) / 2
        drawCircle(
          brush = Brush.sweepGradient(
            colors = listOf(
              stageInfo.accentColor,
              TuringCyan,
              stageInfo.accentColor.copy(alpha = 0.2f),
              stageInfo.accentColor
            )
          ),
          radius = radius,
          center = center,
          style = Stroke(width = strokeWidth)
        )
      }

      // Inner Avatar with dynamic blur & cryptographic shader simulation
      Box(
        modifier = Modifier
          .size(size - 8.dp)
          .clip(CircleShape)
          .background(Color(0xFF0F172A))
      ) {
        // Generative synthetic portrait canvas
        Canvas(
          modifier = Modifier
            .size(size - 8.dp)
            .progressiveBlurShader(animatedBlur)
        ) {
          val canvasWidth = this.size.width
          val canvasHeight = this.size.height

          // Soft radiant background
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(Color(gradientEnd), Color(gradientStart), Color(0xFF090D16)),
              center = center,
              radius = canvasWidth * 0.7f
            )
          )

          // Head / Torso silhouette contours
          drawCircle(
            color = Color(0xFFF8FAFC).copy(alpha = 0.85f),
            radius = canvasWidth * 0.26f,
            center = Offset(canvasWidth * 0.5f, canvasHeight * 0.42f)
          )
          drawOval(
            color = Color(0xFFE2E8F0).copy(alpha = 0.9f),
            topLeft = Offset(canvasWidth * 0.18f, canvasHeight * 0.70f),
            size = androidx.compose.ui.geometry.Size(canvasWidth * 0.64f, canvasHeight * 0.55f)
          )
          // Accent eye-line or glasses aesthetic feature
          drawCircle(
            color = Color(gradientStart),
            radius = canvasWidth * 0.06f,
            center = Offset(canvasWidth * 0.42f, canvasHeight * 0.40f)
          )
          drawCircle(
            color = Color(gradientStart),
            radius = canvasWidth * 0.06f,
            center = Offset(canvasWidth * 0.58f, canvasHeight * 0.40f)
          )
        }

        // Cryptographic cipher overlay for stage 0 and 1
        if (stage <= 1) {
          Canvas(modifier = Modifier.size(size - 8.dp)) {
            val step = 10.dp.toPx()
            var y = 0f
            while (y < this.size.height) {
              drawLine(
                color = Color(0x3300E5FF),
                start = Offset(0f, y),
                end = Offset(this.size.width, y),
                strokeWidth = 1f
              )
              y += step
            }
          }
        }
      }

      // Lock indicator badge when obfuscated
      if (stage < 4) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(24.dp)
            .clip(CircleShape)
            .background(TuringObsidian)
            .border(1.dp, stageInfo.accentColor, CircleShape)
        ) {
          Icon(
            imageVector = if (stage == 0) Icons.Default.Lock else Icons.Default.Security,
            contentDescription = "Lock status",
            tint = stageInfo.accentColor,
            modifier = Modifier.size(12.dp)
          )
        }
      } else {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .size(24.dp)
            .clip(CircleShape)
            .background(TuringEmerald)
        ) {
          Icon(
            imageVector = Icons.Default.LockOpen,
            contentDescription = "Unlocked",
            tint = TuringObsidian,
            modifier = Modifier.size(13.dp)
          )
        }
      }
    }

    if (showBadge) {
      Spacer(modifier = Modifier.height(6.dp))
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(stageInfo.accentColor.copy(alpha = 0.15f))
          .border(1.dp, stageInfo.accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
          .padding(horizontal = 8.dp, vertical = 2.dp)
      ) {
        Text(
          text = if (stage == 4) "UNMASKED" else "STAGE $stage/4",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = stageInfo.accentColor,
            letterSpacing = 1.sp
          )
        )
      }
    }
  }
}
