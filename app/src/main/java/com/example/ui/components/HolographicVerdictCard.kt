package com.example.ui.components

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PartnerEntity
import com.example.model.SessionResult
import com.example.ui.theme.TuringCoral
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary

/**
 * Holographic Turing Verdict Badge & Attestation Certificate:
 * Replaces plain result text with a glowing cryptographic certificate card
 * with dynamic light beams, accuracy calibration stamps, and zero-knowledge proofs.
 */
@Composable
fun HolographicVerdictCard(
  result: SessionResult,
  modifier: Modifier = Modifier
) {
  val isCorrect = result.userGuessIsHuman == result.partner.isAi.not()

  // High-performance finite sheen sweep on card appearance (runs 2 cycles then rests at 0W battery draw)
  val shimmerProgress = remember { Animatable(-1f) }
  LaunchedEffect(result) {
    repeat(2) {
      shimmerProgress.snapTo(-1f)
      shimmerProgress.animateTo(2f, animationSpec = tween(2800, easing = LinearEasing))
    }
  }

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
    border = androidx.compose.foundation.BorderStroke(
      1.5.dp,
      if (isCorrect) TuringEmerald.copy(alpha = 0.6f) else TuringCoral.copy(alpha = 0.6f)
    ),
    modifier = modifier.fillMaxWidth()
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      // Holographic light sheen canvas
      if (shimmerProgress.value < 1.9f) {
        Canvas(modifier = Modifier.matchParentSize()) {
          val w = size.width
          val h = size.height

          val sheenX = shimmerProgress.value * w
          drawLine(
            brush = Brush.horizontalGradient(
              colors = listOf(
                Color.Transparent,
                (if (isCorrect) TuringEmerald else TuringPurple).copy(alpha = 0.15f),
                Color.Transparent
              ),
              startX = sheenX - 100f,
              endX = sheenX + 100f
            ),
            start = Offset(0f, 0f),
            end = Offset(w, h),
            strokeWidth = 60f
          )
        }
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Status Seal Icon
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(
              if (isCorrect) TuringEmerald.copy(alpha = 0.15f) else TuringCoral.copy(alpha = 0.15f)
            )
            .border(
              2.dp,
              if (isCorrect) TuringEmerald else TuringCoral,
              CircleShape
            )
        ) {
          Icon(
            imageVector = if (isCorrect) Icons.Default.WorkspacePremium else Icons.Default.CrisisAlert,
            contentDescription = null,
            tint = if (isCorrect) TuringEmerald else TuringCoral,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = if (isCorrect) "TURING TEST ACCURACY: SUCCESS" else "TURING TEST DECEPTION DETECTED",
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 1.sp,
            color = if (isCorrect) TuringEmerald else TuringCoral
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = if (isCorrect) {
            "Zero-trust discernment verified. Your psycholinguistic analysis correctly pierced the cipher."
          } else {
            "The imitation engine successfully emulated biological nuance, outsmarting perception."
          },
          style = MaterialTheme.typography.bodySmall.copy(
            color = TuringTextPrimary.copy(alpha = 0.85f),
            fontSize = 12.sp,
            lineHeight = 18.sp
          ),
          modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dual Identity Reveal Comparison Block
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0A0F1A))
            .border(1.dp, TuringObsidianBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "YOUR ARBITRATION",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = TuringTextMuted
              )
            )
            Text(
              text = if (result.userGuessIsHuman == true) "HUMAN (CARBON)" else "AI AGENT (SILICON)",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TuringCyan
              )
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(32.dp)
              .background(TuringObsidianBorder)
          )

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "ACTUAL ENTITY",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = TuringTextMuted
              )
            )
            Text(
              text = if (result.partner.isAi) "AI AGENT (SILICON)" else "HUMAN (CARBON)",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (result.partner.isAi) TuringPurple else TuringEmerald
              )
            )
          }
        }
      }
    }
  }
}
