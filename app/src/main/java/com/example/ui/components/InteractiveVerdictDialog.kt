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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.PartnerEntity
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
 * Interactive Turing Verdict Judgment Dial:
 * Replaces boring radio buttons or binary "Human/AI" switches with a circular
 * probability dial, live neural confidence meter, and cryptographic verdict seal.
 */
@Composable
fun InteractiveVerdictDialog(
  partner: PartnerEntity,
  onConfirmVerdict: (isHumanGuess: Boolean, confidence: Float) -> Unit,
  onDismiss: () -> Unit
) {
  var probabilityHuman by remember { mutableFloatStateOf(0.50f) }

  val infiniteTransition = rememberInfiniteTransition(label = "verdict_scan")
  val scanAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "dial_rot"
  )

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, TuringObsidianBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("verdict_judgment_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(TuringPurple.copy(alpha = 0.2f))
            ) {
              Icon(
                imageVector = Icons.Default.CrisisAlert,
                contentDescription = null,
                tint = TuringPurple,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "THE TURING ARBITRATION",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = TuringPurple
                )
              )
              Text(
                text = "Zero-trust identity judgment",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 10.sp,
                  color = TuringTextMuted
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Probability Dial View
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.size(170.dp)
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val r = (w / 2f) - 16.dp.toPx()

            // Arc track
            drawCircle(
              color = TuringObsidianBorder,
              radius = r,
              style = Stroke(width = 8.dp.toPx())
            )

            // Dynamic Human vs Synthetic arc
            val sweepAngle = 360f * probabilityHuman
            val arcColor = if (probabilityHuman >= 0.5f) TuringEmerald else TuringCoral

            drawArc(
              brush = Brush.sweepGradient(
                colors = listOf(TuringCoral, TuringPurple, TuringEmerald, TuringCyan)
              ),
              startAngle = -90f,
              sweepAngle = sweepAngle,
              useCenter = false,
              style = Stroke(width = 10.dp.toPx())
            )

            // Center glow
            drawCircle(
              brush = Brush.radialGradient(
                colors = listOf(arcColor.copy(alpha = 0.25f), Color.Transparent),
                center = Offset(cx, cy),
                radius = r * 0.8f
              )
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = if (probabilityHuman >= 0.5f) "CARBON" else "SILICON",
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.5.sp,
                color = if (probabilityHuman >= 0.5f) TuringEmerald else TuringCoral
              )
            )
            Text(
              text = if (probabilityHuman >= 0.5f) "${(probabilityHuman * 100).toInt()}% HUMAN" else "${((1f - probabilityHuman) * 100).toInt()}% SYNTHETIC",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TuringTextPrimary
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Probability Slider
        Text(
          text = "SLIDE TO CALIBRATE ARBITRATION CONFIDENCE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = TuringTextMuted
          )
        )

        Slider(
          value = probabilityHuman,
          onValueChange = { probabilityHuman = it },
          colors = SliderDefaults.colors(
            thumbColor = if (probabilityHuman >= 0.5f) TuringEmerald else TuringCoral,
            activeTrackColor = if (probabilityHuman >= 0.5f) TuringEmerald else TuringCoral,
            inactiveTrackColor = TuringObsidianBorder
          ),
          modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "◀ SYNTHETIC (AI)",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              color = TuringCoral
            )
          )
          Text(
            text = "CARBON (HUMAN) ▶",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              color = TuringEmerald
            )
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = TuringObsidian),
            border = androidx.compose.foundation.BorderStroke(1.dp, TuringObsidianBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).height(46.dp)
          ) {
            Text(
              text = "RESUME",
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                color = TuringTextMuted
              )
            )
          }

          Button(
            onClick = {
              val isHuman = probabilityHuman >= 0.5f
              val confidence = Math.abs(probabilityHuman - 0.5f) * 2f
              onConfirmVerdict(isHuman, confidence)
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (probabilityHuman >= 0.5f) TuringEmerald else TuringCoral
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1.5f).height(46.dp).testTag("confirm_verdict_button")
          ) {
            Text(
              text = "SUBMIT VERDICT",
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TuringObsidian
              )
            )
          }
        }
      }
    }
  }
}
