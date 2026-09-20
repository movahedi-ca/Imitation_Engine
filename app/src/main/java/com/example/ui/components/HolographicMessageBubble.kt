package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.SenderType
import com.example.ui.theme.TuringBubblePartner
import com.example.ui.theme.TuringBubblePartnerText
import com.example.ui.theme.TuringBubbleUser
import com.example.ui.theme.TuringBubbleUserText
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary
import com.example.ui.theme.TuringTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Pre-allocated static shape and gradient singletons to prevent allocation churn in LazyColumn
private val UserBubbleShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
private val PartnerBubbleShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
private val UserBubbleGradient = Brush.linearGradient(listOf(Color(0xFF00E5FF), Color(0xFF00B4D8)))
private val PartnerBubbleGradient = Brush.linearGradient(listOf(Color(0xFF131B2A), Color(0xFF0E1624)))

/**
 * Holographic Cryptographic Message Bubble:
 * Replaces dull plain chat bubbles with neon-accented cyber cards featuring:
 * - Dynamic cipher decode button (tap to toggle encrypted hex stream vs plain text)
 * - Micro zero-trust payload hash badge
 * - Sentiment & token depth indicator
 * - Reaction glyph chips (witty, philosophical, mechanical, human)
 */
@Composable
fun HolographicMessageBubble(
  message: ChatMessage,
  onInspectPayload: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val isUser = message.senderType == SenderType.USER
  var isDecrypted by remember { mutableStateOf(true) }
  var selectedReaction by remember { mutableStateOf<String?>(null) }
  val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
  val timeStr = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

  // Generate pseudo hex hash from message id
  val shortHash = remember(message.messageId) {
    message.messageId.take(8).uppercase()
  }

  // Obfuscated hex stream: computed only when decrypted is toggled off
  val cipherText = remember(message.content, isDecrypted) {
    if (isDecrypted) "" else {
      message.content.map { c ->
        if (c.isWhitespace()) " " else (c.code % 16).toString(16).uppercase()
      }.joinToString("")
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 5.dp),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    // Header telemetry line
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
      Text(
        text = if (isUser) "YOU // OBSERVER" else "PARTNER // CIPHER-7",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = if (isUser) TuringCyan else TuringPurple
        )
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "#$shortHash",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 8.sp,
          color = TuringTextMuted
        )
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = timeStr,
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 8.sp,
          color = TuringTextMuted
        )
      )
    }

    // Message Body Card with static singleton shapes and cached gradients
    val bubbleShape = if (isUser) UserBubbleShape else PartnerBubbleShape
    val bubbleBrush = if (isUser) UserBubbleGradient else PartnerBubbleGradient

    Box(
      modifier = Modifier
        .clip(bubbleShape)
        .background(bubbleBrush)
        .border(
          width = 1.dp,
          color = if (isUser) TuringCyan.copy(alpha = 0.5f) else TuringObsidianBorder,
          shape = bubbleShape
        )
        .clickable { isDecrypted = !isDecrypted }
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Column {
        Text(
          text = if (isDecrypted) message.content else cipherText,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = if (isUser) TuringObsidian else TuringTextPrimary,
            fontFamily = if (!isDecrypted) FontFamily.Monospace else FontFamily.Default,
            fontSize = if (!isDecrypted) 12.sp else 14.sp,
            lineHeight = 20.sp
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Micro footer controls
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth(if (isUser) 0.6f else 0.8f)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isDecrypted = !isDecrypted }
          ) {
            Icon(
              imageVector = if (isDecrypted) Icons.Default.Lock else Icons.Default.Visibility,
              contentDescription = "Toggle cipher view",
              tint = if (isUser) TuringObsidian.copy(alpha = 0.7f) else TuringCyan.copy(alpha = 0.7f),
              modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = if (isDecrypted) "TAP TO ENCRYPT" else "TAP TO DECRYPT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUser) TuringObsidian.copy(alpha = 0.7f) else TuringCyan.copy(alpha = 0.7f)
              )
            )
          }

          if (message.progressiveRevealStage > 0) {
            Text(
              text = "STAGE ${message.progressiveRevealStage}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                color = if (isUser) TuringObsidian.copy(alpha = 0.7f) else TuringEmerald
              )
            )
          }
        }
      }
    }

    // Reaction Chips row (Zero-Trust Turing Appraisal)
    if (!isUser) {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(start = 4.dp)
      ) {
        val reactions = listOf("🤖 Synthetic", "🧠 Human", "✨ Lucid", "⚡ Witty")
        reactions.forEach { r ->
          val isSelected = selectedReaction == r
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) TuringPurple.copy(alpha = 0.25f) else Color(0xFF090E17))
              .border(
                1.dp,
                if (isSelected) TuringPurple else TuringObsidianBorder.copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
              )
              .clickable {
                selectedReaction = if (isSelected) null else r
              }
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = r,
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = if (isSelected) TuringPurple else TuringTextMuted
              )
            )
          }
        }
      }
    }
  }
}
