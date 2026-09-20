package com.example.ui.components

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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.TuringCoral
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary
import com.example.ui.theme.TuringTextSecondary

/**
 * Performance & Compute Warning Dialog:
 * Alert shown whenever a user attempts to enable Local Neural AI or AI Mimic matching,
 * informing them of the heavy CPU/GPU resource usage and potential device slowness.
 */
@Composable
fun NeuralWarningDialog(
  onConfirmEnable: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, TuringCoral.copy(alpha = 0.6f)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("neural_warning_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Warning Icon Badge
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(TuringCoral.copy(alpha = 0.16f))
            .border(1.dp, TuringCoral.copy(alpha = 0.4f), CircleShape)
        ) {
          Icon(
            imageVector = Icons.Default.WarningAmber,
            contentDescription = "Resource Warning",
            tint = TuringCoral,
            modifier = Modifier.size(28.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "HIGH COMPUTE & RESOURCE NOTICE",
          style = MaterialTheme.typography.titleSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = TuringCoral
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Local AI Neural Engine is turned off by default to keep the application lightweight, responsive, and battery-friendly.",
          style = MaterialTheme.typography.bodySmall.copy(
            color = TuringTextPrimary,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Key points card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF140D1B)),
          border = androidx.compose.foundation.BorderStroke(1.dp, TuringCoral.copy(alpha = 0.25f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(verticalAlignment = Alignment.Top) {
              Icon(
                imageVector = Icons.Default.ElectricBolt,
                contentDescription = null,
                tint = TuringCoral,
                modifier = Modifier
                  .size(16.dp)
                  .padding(top = 2.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Continuous token generation & vector calculus consume heavy GPU/CPU cycles and can cause temporary UI freezing or lag.",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 11.sp,
                  color = TuringTextSecondary,
                  lineHeight = 15.sp
                )
              )
            }

            Row(verticalAlignment = Alignment.Top) {
              Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = TuringEmerald,
                modifier = Modifier
                  .size(16.dp)
                  .padding(top = 2.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Recommended: Keep Optimized Mode for smooth 60+ FPS performance and instant Verified Human matching.",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 11.sp,
                  color = TuringEmerald,
                  lineHeight = 15.sp,
                  fontWeight = FontWeight.SemiBold
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Button(
          onClick = {
            onConfirmEnable()
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = TuringCoral),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("confirm_enable_neural_button")
        ) {
          Text(
            text = "ENABLE NEURAL ENGINE (HIGH LOAD)",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringObsidian,
              letterSpacing = 0.5.sp
            )
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
          onClick = onDismiss,
          border = androidx.compose.foundation.BorderStroke(1.dp, TuringObsidianBorder),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("keep_optimized_button")
        ) {
          Text(
            text = "KEEP OPTIMIZED (RECOMMENDED)",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringEmerald,
              letterSpacing = 0.5.sp
            )
          )
        }
      }
    }
  }
}
