package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringTextPrimary
import com.example.ui.theme.TuringTextSecondary

@Composable
fun WebSocketPayloadDialog(
  rawJson: String,
  onDismiss: () -> Unit
) {
  val horizontalScroll = rememberScrollState()

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
      border = androidx.compose.foundation.BorderStroke(1.dp, TuringObsidianBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(TuringCyan.copy(alpha = 0.2f))
              .border(1.dp, TuringCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Code,
              contentDescription = null,
              tint = TuringCyan,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "WEBSOCKET PAYLOAD CONTRACT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TuringCyan,
                letterSpacing = 1.sp
              )
            )
            Text(
              text = "Zero-Trust Protocol (Event: message:receive)",
              style = MaterialTheme.typography.bodySmall.copy(color = TuringTextSecondary)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF070A0F))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(14.dp)
            .horizontalScroll(horizontalScroll)
        ) {
          Text(
            text = rawJson.ifEmpty {
              """
{
  "event": "message:receive",
  "payload": {
    "message_id": "11223344-5566-7788-9900-aabbccddeeff",
    "sender_type": "PARTNER",
    "content": "I completely agree. Action speaks louder than text.",
    "progressive_reveal_stage": 1,
    "timestamp": 1773964812
  }
}
              """.trimIndent()
            },
            style = MaterialTheme.typography.bodySmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              color = TuringEmerald,
              lineHeight = 18.sp
            )
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Note: In accordance with Zero-Trust guidelines, payload structure is identical for both Human and AI partners. No entity boolean is transmitted.",
          style = MaterialTheme.typography.bodySmall.copy(
            color = TuringTextSecondary,
            fontSize = 11.sp
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = TuringCyan),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "CLOSE CONTRACT VIEWER",
            style = MaterialTheme.typography.labelMedium.copy(
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
