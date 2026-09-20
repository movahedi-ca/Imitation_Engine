package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.ProgressiveStageDefinitions
import com.example.model.SenderType
import com.example.ui.components.ProgressiveBlurAvatar
import com.example.ui.components.WebSocketPayloadDialog
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary
import com.example.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Basic ChatScreen composable with a LazyColumn for message bubbles and an InputField
 * for sending text, following the 'Chamber' UI requirements and driven by ChatViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  chatViewModel: ChatViewModel,
  onNavigateBack: () -> Unit,
  onConcludeSession: () -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by chatViewModel.chatUiState.collectAsState()
  val listState = rememberLazyListState()
  var inputMessage by remember { mutableStateOf("") }
  var showPayloadDialog by remember { mutableStateOf(false) }

  val stageInfo = ProgressiveStageDefinitions.getInfo(uiState.progressiveRevealStage)

  // Auto-scroll to bottom upon new message or typing indicator
  LaunchedEffect(uiState.messages.size, uiState.isPartnerTyping) {
    if (uiState.messages.isNotEmpty()) {
      listState.animateScrollToItem(uiState.messages.size - 1)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TuringObsidian),
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("chat_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Return to Sanctuary",
              tint = TuringTextPrimary
            )
          }
        },
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            ProgressiveBlurAvatar(
              stage = uiState.progressiveRevealStage,
              size = 42.dp,
              showBadge = false
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = uiState.partnerCodename,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TuringTextPrimary
                  )
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Zero-Trust Lock Badge
                Icon(
                  imageVector = Icons.Default.Shield,
                  contentDescription = "Zero-Trust Protocol",
                  tint = TuringCyan,
                  modifier = Modifier.size(13.dp)
                )
              }

              Text(
                text = "STAGE ${uiState.progressiveRevealStage}/4 • ${stageInfo.title.substringAfter(": ")}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  color = stageInfo.accentColor
                )
              )
            }
          }
        },
        actions = {
          IconButton(
            onClick = { showPayloadDialog = true },
            modifier = Modifier.testTag("chat_inspect_payload_button")
          ) {
            Icon(
              imageVector = Icons.Default.Code,
              contentDescription = "Inspect WebSocket JSON Contract",
              tint = TuringCyan
            )
          }

          IconButton(
            onClick = { chatViewModel.forceAdvanceStage() },
            modifier = Modifier.testTag("chat_advance_stage_button")
          ) {
            Icon(
              imageVector = Icons.Default.LockOpen,
              contentDescription = "Simulate Stage Unlock",
              tint = TuringEmerald
            )
          }
        }
      )
    },
    bottomBar = {
      ChatScreenBottomBar(
        inputText = inputMessage,
        onInputChanged = { inputMessage = it },
        onSend = {
          if (inputMessage.isNotBlank()) {
            chatViewModel.sendMessage(inputMessage)
            inputMessage = ""
          }
        },
        onConclude = onConcludeSession
      )
    },
    containerColor = TuringObsidian,
    modifier = modifier
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Milestone Banner
      StageMilestoneBanner(stage = uiState.progressiveRevealStage)

      // LazyColumn of message bubbles
      LazyColumn(
        state = listState,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(uiState.messages, key = { it.messageId }) { msg ->
          MessageBubbleItem(msg)
        }

        if (uiState.isPartnerTyping) {
          item(key = "typing_indicator") {
            TypingFrictionIndicator(statusText = uiState.typingStatusText)
          }
        }
      }
    }
  }

  if (showPayloadDialog) {
    val samplePayload = if (uiState.lastWebSocketJson.isNotEmpty()) {
      uiState.lastWebSocketJson
    } else {
      """
{
  "event": "message:receive",
  "payload": {
    "message_id": "11223344-5566-7788-9900-aabbccddeeff",
    "sender_type": "PARTNER",
    "content": "Zero-trust verified handshake established.",
    "progressive_reveal_stage": ${uiState.progressiveRevealStage},
    "timestamp": ${System.currentTimeMillis() / 1000}
  }
}
      """.trimIndent()
    }

    WebSocketPayloadDialog(
      rawJson = samplePayload,
      onDismiss = { showPayloadDialog = false }
    )
  }
}

@Composable
private fun MessageBubbleItem(message: ChatMessage) {
  val isUser = message.senderType == SenderType.USER
  val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
  val timeStr = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("chat_message_item_${message.messageId}"),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    Card(
      colors = CardDefaults.cardColors(
        containerColor = if (isUser) TuringCyan.copy(alpha = 0.18f) else TuringObsidianCard
      ),
      shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isUser) 16.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 16.dp
      ),
      modifier = Modifier
        .widthIn(max = 290.dp)
        .border(
          width = 1.dp,
          color = if (isUser) TuringCyan.copy(alpha = 0.5f) else TuringObsidianBorder,
          shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isUser) 16.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 16.dp
          )
        )
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = message.content,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = TuringTextPrimary,
            lineHeight = 20.sp
          )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.align(Alignment.End),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = timeStr,
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              color = TuringTextMuted
            )
          )
          if (isUser) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "✓✓",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = TuringCyan
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TypingFrictionIndicator(statusText: String) {
  val infiniteTransition = rememberInfiniteTransition(label = "dots")
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(650, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "alpha"
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .padding(start = 4.dp, top = 2.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(TuringObsidianCard)
      .border(1.dp, TuringObsidianBorder, RoundedCornerShape(12.dp))
      .padding(horizontal = 12.dp, vertical = 6.dp)
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(TuringCyan.copy(alpha = alpha))
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = statusText.ifEmpty { "Artificial friction delay active..." },
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = TuringTextMuted
      )
    )
  }
}

@Composable
private fun StageMilestoneBanner(stage: Int) {
  val info = ProgressiveStageDefinitions.getInfo(stage)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFF0B101B))
      .border(1.dp, info.accentColor.copy(alpha = 0.25f))
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = info.title.uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = info.accentColor,
            letterSpacing = 1.sp
          )
        )
        Text(
          text = info.unlockedClue,
          style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 11.sp,
            color = TuringTextMuted
          ),
          maxLines = 1
        )
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(info.accentColor.copy(alpha = 0.15f))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = "BLUR: ${info.blurRadiusDp.toInt()} DP",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = info.accentColor
          )
        )
      }
    }
  }
}

@Composable
private fun ChatScreenBottomBar(
  inputText: String,
  onInputChanged: (String) -> Unit,
  onSend: () -> Unit,
  onConclude: () -> Unit
) {
  SurfaceBottomBar(
    inputText = inputText,
    onInputChanged = onInputChanged,
    onSend = onSend,
    onConclude = onConclude
  )
}

@Composable
private fun SurfaceBottomBar(
  inputText: String,
  onInputChanged: (String) -> Unit,
  onSend: () -> Unit,
  onConclude: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(TuringObsidian)
      .navigationBarsPadding()
      .imePadding()
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    // Quick action bar: Conclude & Guess
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "ZERO-TRUST DOUBLE-BLIND CIPHER",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          color = TuringTextMuted
        )
      )

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(TuringPurple.copy(alpha = 0.2f))
          .border(1.dp, TuringPurple.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
          .clickable { onConclude() }
          .padding(horizontal = 10.dp, vertical = 4.dp)
          .testTag("chat_conclude_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = null,
            tint = TuringPurple,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "DECIDE: HUMAN OR AI?",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = TuringPurple
            )
          )
        }
      }
    }

    // Text Input field + send button
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = inputText,
        onValueChange = onInputChanged,
        placeholder = {
          Text(
            text = "Transmit zero-trust thought...",
            style = MaterialTheme.typography.bodyMedium.copy(color = TuringTextMuted)
          )
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = TuringObsidianCard,
          unfocusedContainerColor = TuringObsidianCard,
          focusedBorderColor = TuringCyan,
          unfocusedBorderColor = TuringObsidianBorder,
          focusedTextColor = TuringTextPrimary,
          unfocusedTextColor = TuringTextPrimary,
          cursorColor = TuringCyan
        ),
        shape = RoundedCornerShape(24.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
        keyboardActions = KeyboardActions(onSend = { onSend() }),
        maxLines = 4,
        modifier = Modifier
          .weight(1f)
          .testTag("chat_input_field")
      )

      Spacer(modifier = Modifier.width(8.dp))

      IconButton(
        onClick = onSend,
        enabled = inputText.isNotBlank(),
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(
            if (inputText.isNotBlank()) TuringCyan else TuringObsidianCard
          )
          .testTag("chat_send_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send message",
          tint = if (inputText.isNotBlank()) TuringObsidian else TuringTextMuted,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
