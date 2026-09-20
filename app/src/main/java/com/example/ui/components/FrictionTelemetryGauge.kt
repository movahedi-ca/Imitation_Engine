package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary

/**
 * Interactive Turing Friction Gauge & Live Telemetry Inspector:
 * Replaces plain "PARTNER TYPING..." dot animations with a high-tech
 * artificial hesitation / token latency speedometer and anti-bot entropy meter.
 */
@Composable
fun FrictionTelemetryGauge(
  isPartnerTyping: Boolean,
  frictionDelayMs: Long = 1800L,
  entropyScore: Float = 0.89f,
  modifier: Modifier = Modifier
) {
  var showDetails by remember { mutableStateOf(false) }

  AnimatedVisibility(
    visible = isPartnerTyping,
    enter = fadeIn(animationSpec = tween(250)),
    exit = fadeOut(animationSpec = tween(200))
  ) {
    Column(
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 4.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(TuringObsidianCard)
        .border(1.dp, TuringPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
        .clickable { showDetails = !showDetails }
        .padding(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(TuringPurple.copy(alpha = 0.2f))
          ) {
            Icon(
              imageVector = Icons.Default.Speed,
              contentDescription = "Friction Delay",
              tint = TuringPurple,
              modifier = Modifier.size(14.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "ARTIFICIAL FRICTION: ~${frictionDelayMs}ms",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  color = TuringPurple
                )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(TuringEmerald)
              )
            }
            Text(
              text = "Human typing cadence jitter emulation active",
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 9.sp,
                color = TuringTextMuted
              )
            )
          }
        }

        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Details",
          tint = TuringTextMuted,
          modifier = Modifier.size(16.dp)
        )
      }

      if (showDetails) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(TuringObsidianBorder)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          TelemetryStat(
            label = "ZERO-TRUST ENTROPY",
            value = "${(entropyScore * 100).toInt()}%",
            color = TuringEmerald
          )
          TelemetryStat(
            label = "SYNTACTIC JITTER",
            value = "±340ms",
            color = TuringCyan
          )
          TelemetryStat(
            label = "TOKEN THROTTLE",
            value = "220ms/w",
            color = TuringPurple
          )
        }
      }
    }
  }
}

@Composable
private fun TelemetryStat(label: String, value: String, color: Color) {
  Column {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 8.sp,
        color = TuringTextMuted
      )
    )
    Text(
      text = value,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        color = color
      )
    )
  }
}
