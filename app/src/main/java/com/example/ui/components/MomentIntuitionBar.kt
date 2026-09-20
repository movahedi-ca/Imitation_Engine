package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TuringCoral
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary
import com.example.ui.theme.TuringTextSecondary

/**
 * At Each Moment: "Person or AI?" Intuition Bar
 * Placed persistently above the chat input in the Chamber, allowing the user
 * to dynamically record and test their evolving suspicion in real time.
 */
@Composable
fun MomentIntuitionBar(
  currentSuspicion: Float, // 0.0f = Definite AI, 1.0f = Definite Human
  onSuspicionChange: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  val isLeaningHuman = currentSuspicion >= 0.5f
  val humanPct = (currentSuspicion * 100).toInt()
  val aiPct = 100 - humanPct

  val trackColor by animateColorAsState(
    targetValue = if (isLeaningHuman) TuringEmerald else TuringCoral,
    label = "suspicion_color"
  )

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, TuringObsidianBorder),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 4.dp)
      .testTag("moment_intuition_bar")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CrisisAlert,
            contentDescription = null,
            tint = trackColor,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "LIVE INTUITION // ARE THEY HUMAN OR AI?",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = TuringTextPrimary,
              letterSpacing = 0.5.sp
            )
          )
        }

        // Live readout pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(trackColor.copy(alpha = 0.15f))
            .border(1.dp, trackColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = if (isLeaningHuman) "$humanPct% HUMAN" else "$aiPct% AI",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = trackColor
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Compact Slider
      Slider(
        value = currentSuspicion,
        onValueChange = onSuspicionChange,
        colors = SliderDefaults.colors(
          thumbColor = trackColor,
          activeTrackColor = trackColor,
          inactiveTrackColor = TuringObsidian
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(24.dp)
          .testTag("moment_intuition_slider")
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { onSuspicionChange(0.15f) }
        ) {
          Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = TuringCoral,
            modifier = Modifier.size(11.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "AI Mimic",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 8.sp,
              color = TuringCoral
            )
          )
        }

        Text(
          text = "Slide anytime as they reply",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            color = TuringTextMuted
          )
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { onSuspicionChange(0.85f) }
        ) {
          Text(
            text = "Real Person",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 8.sp,
              color = TuringEmerald
            )
          )
          Spacer(modifier = Modifier.width(3.dp))
          Icon(
            imageVector = Icons.Default.RecordVoiceOver,
            contentDescription = null,
            tint = TuringEmerald,
            modifier = Modifier.size(11.dp)
          )
        }
      }
    }
  }
}
