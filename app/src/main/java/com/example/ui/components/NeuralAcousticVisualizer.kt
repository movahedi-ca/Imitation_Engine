package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary

/**
 * Speech Acoustic & Neural Resonance Synthesizer:
 * Replaces static silence / boring text headers with a reactive speech cadence visualizer
 * that oscillates neural frequencies when typing, receiving or playing cryptographic acoustics.
 */
@Composable
fun NeuralAcousticVisualizer(
  isPartnerSpeaking: Boolean,
  stage: Int,
  modifier: Modifier = Modifier
) {
  var isAudioMonitoringActive by remember { mutableStateOf(false) }

  val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
  val wavePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = (Math.PI * 2).toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wave_phase"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF090E17))
      .border(1.dp, TuringObsidianBorder, RoundedCornerShape(12.dp))
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (isPartnerSpeaking) TuringCyan.copy(alpha = 0.2f) else TuringObsidianCard)
            .clickable { isAudioMonitoringActive = !isAudioMonitoringActive }
            .testTag("acoustic_toggle_button")
        ) {
          Icon(
            imageVector = if (isPartnerSpeaking) Icons.Default.Sensors else Icons.Default.GraphicEq,
            contentDescription = "Neural Acoustic Resonance",
            tint = if (isPartnerSpeaking) TuringCyan else TuringTextMuted,
            modifier = Modifier.size(16.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = if (isPartnerSpeaking) "SYNTACTIC CADENCE ACTIVE" else "PSYCHOLINGUISTIC TELEMETRY",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (isPartnerSpeaking) TuringCyan else TuringTextPrimary
              )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "432 Hz",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = TuringEmerald
              )
            )
          }

          Text(
            text = if (isPartnerSpeaking) "Analyzing token entropy & latency jitter" else "Real-time human vs synthetic cadence calibration",
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 10.sp,
              color = TuringTextMuted
            )
          )
        }
      }

      // Live Oscilloscope Frequency Waveform
      Canvas(
        modifier = Modifier
          .width(86.dp)
          .height(28.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(Color(0xFF030712))
          .padding(4.dp)
      ) {
        val w = size.width
        val h = size.height
        val midY = h / 2f

        val barCount = 12
        val barWidth = w / barCount

        for (i in 0 until barCount) {
          val amp = if (isPartnerSpeaking) {
            val sinVal = Math.sin((wavePhase + (i * 0.5)).toDouble()).toFloat()
            (Math.abs(sinVal) * (h * 0.42f) + (h * 0.15f)).coerceIn(4f, h * 0.9f)
          } else {
            ((i % 4) * 2f + 4f)
          }

          val color = when {
            i < 4 -> TuringCyan
            i < 8 -> TuringPurple
            else -> TuringEmerald
          }

          val x = i * barWidth + (barWidth * 0.2f)
          drawLine(
            color = if (isPartnerSpeaking) color else color.copy(alpha = 0.35f),
            start = Offset(x, midY - (amp / 2f)),
            end = Offset(x, midY + (amp / 2f)),
            strokeWidth = 2.dp.toPx()
          )
        }
      }
    }
  }
}
