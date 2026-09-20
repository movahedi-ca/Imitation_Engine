package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserVectors
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextPrimary
import com.example.ui.theme.TuringTextSecondary

@Composable
fun VectorMetricsCard(
  vectors: UserVectors,
  title: String = "PSYCHOLINGUISTIC VECTORS",
  subtitle: String = "Active weights calibrated via Imitation Engine",
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(TuringObsidianCard)
      .border(1.dp, TuringObsidianBorder, RoundedCornerShape(16.dp))
      .padding(18.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(TuringCyan)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp,
          color = TuringCyan
        )
      )
    }
    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodySmall.copy(color = TuringTextSecondary),
      modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
    )

    VectorMetricRow(
      icon = Icons.Default.ChatBubbleOutline,
      label = "Verbosity & Depth",
      value = vectors.verbosityScore,
      valueText = "${(vectors.verbosityScore * 100).toInt()}%",
      barColor = TuringCyan
    )

    Spacer(modifier = Modifier.height(10.dp))

    VectorMetricRow(
      icon = Icons.Default.SentimentVerySatisfied,
      label = "Humor Index",
      value = vectors.humorIndex,
      valueText = "${(vectors.humorIndex * 100).toInt()}%",
      barColor = TuringPurple
    )

    Spacer(modifier = Modifier.height(10.dp))

    VectorMetricRow(
      icon = Icons.Default.FavoriteBorder,
      label = "Empathy & Inquiry",
      value = vectors.empathyScore,
      valueText = "${(vectors.empathyScore * 100).toInt()}%",
      barColor = TuringEmerald
    )

    Spacer(modifier = Modifier.height(10.dp))

    VectorMetricRow(
      icon = Icons.Default.Timer,
      label = "Response Latency",
      value = (1.0f - (vectors.responseLatencyAvgSec / 45f)).coerceIn(0.1f, 1.0f),
      valueText = "${vectors.responseLatencyAvgSec}s avg",
      barColor = Color(0xFFF59E0B)
    )
  }
}

@Composable
fun VectorMetricRow(
  icon: ImageVector,
  label: String,
  value: Float,
  valueText: String,
  barColor: Color,
  modifier: Modifier = Modifier
) {
  val animatedValue by animateFloatAsState(
    targetValue = value.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "metric_bar"
  )

  Column(modifier = modifier.fillMaxWidth()) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = barColor,
          modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = label,
          style = MaterialTheme.typography.bodySmall.copy(
            color = TuringTextPrimary,
            fontWeight = FontWeight.Medium
          )
        )
      }
      Text(
        text = valueText,
        style = MaterialTheme.typography.bodySmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          color = barColor
        )
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Progress track
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(Color(0xFF1E293B))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(animatedValue)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(barColor)
      )
    }
  }
}
