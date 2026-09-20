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
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.ProgressiveStageDefinitions
import com.example.model.SenderType
import com.example.ui.components.CyberneticBackground
import com.example.ui.components.FrictionTelemetryGauge
import com.example.ui.components.HolographicMessageBubble
import com.example.ui.components.InteractiveVerdictDialog
import com.example.ui.components.MomentIntuitionBar
import com.example.ui.components.NeuralAcousticVisualizer
import com.example.ui.components.ProgressiveBlurAvatar
import com.example.ui.components.QuantumCipherAvatar
import com.example.ui.components.WebSocketPayloadDialog
import com.example.ui.theme.TuringBubblePartner
import com.example.ui.theme.TuringBubblePartnerText
import com.example.ui.theme.TuringBubbleUser
import com.example.ui.theme.TuringBubbleUserText
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextPrimary
import com.example.ui.theme.TuringTextSecondary
import com.example.viewmodel.TuringViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val QUICK_PROMPTS = listOf(
  "Action speaks louder than text.",
  "Do you believe in human intuition?",
  "Are you an AI calibration agent?",
  "What is an idea you've recently reconsidered?",
  "How do you define genuine compatibility?"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChamberScreen(
  viewModel: TuringViewModel,
  modifier: Modifier = Modifier
) {
  val chamberState by viewModel.chamberState.collectAsState()
  val lowPowerMode by viewModel.lowPowerMode.collectAsState()
  var inputMessage by remember { mutableStateOf("") }
  var showPayloadDialog by remember { mutableStateOf(false) }
  var showVerdictDialog by remember { mutableStateOf(false) }
  val listState = rememberLazyListState()
  val focusManager = LocalFocusManager.current

  val stageInfo = ProgressiveStageDefinitions.getInfo(chamberState.progressiveRevealStage)

  // Scroll to bottom when new messages or typing state arrives
  LaunchedEffect(chamberState.messages.size, chamberState.isPartnerTyping) {
    val targetIndex = chamberState.messages.size + (if (chamberState.isPartnerTyping) 1 else 0)
    if (targetIndex > 0) {
      listState.animateScrollToItem(targetIndex - 1)
    }
  }

  if (showPayloadDialog) {
    WebSocketPayloadDialog(
      rawJson = chamberState.lastWebSocketJson,
      onDismiss = { showPayloadDialog = false }
    )
  }

  chamberState.partner?.let { partner ->
    if (showVerdictDialog) {
      InteractiveVerdictDialog(
        partner = partner,
        onConfirmVerdict = { isHuman, confidence ->
          showVerdictDialog = false
          viewModel.concludeSession(userGuessIsHuman = isHuman)
        },
        onDismiss = { showVerdictDialog = false }
      )
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TuringObsidian),
        navigationIcon = {
          IconButton(onClick = { viewModel.returnToSanctuary() }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Return to Sanctuary",
              tint = TuringTextSecondary
            )
          }
        },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(TuringEmerald)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = chamberState.partnerCodename,
                style = MaterialTheme.typography.titleSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = TuringTextPrimary,
                  letterSpacing = 1.sp
                )
              )
              Text(
                text = "ZERO-TRUST SESSION // E2EE SIGNAL",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  color = TuringCyan
                )
              )
            }
          }
        },
        actions = {
          // Inspect WebSocket JSON Contract
          IconButton(onClick = { showPayloadDialog = true }) {
            Icon(
              imageVector = Icons.Default.Code,
              contentDescription = "Inspect WebSocket Payload",
              tint = TuringCyan
            )
          }
          // Conclude / Call Turing Verdict button
          Box(
            modifier = Modifier
              .padding(end = 12.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(TuringPurple.copy(alpha = 0.2f))
              .border(1.dp, TuringPurple.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
              .clickable { showVerdictDialog = true }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "VERDICT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TuringPurple,
                letterSpacing = 1.sp
              )
            )
          }
        }
      )
    },
    containerColor = Color.Transparent,
    modifier = modifier
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize()) {
      CyberneticBackground(
        accentColor = TuringCyan,
        lowPowerMode = lowPowerMode
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .imePadding()
      ) {

        // Task 4.4: Progressive Reveal Header & Quantum Cipher Iris
        Card(
          shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard.copy(alpha = 0.9f)),
          border = androidx.compose.foundation.BorderStroke(1.dp, TuringObsidianBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              // Quantum Morphing Shader Iris
              QuantumCipherAvatar(
                stage = chamberState.progressiveRevealStage,
                gradientStart = chamberState.partner?.avatarGradientStart ?: 0xFF6366F1,
                gradientEnd = chamberState.partner?.avatarGradientEnd ?: 0xFF00E5FF,
                size = 72.dp,
                showRings = true
              )

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = stageInfo.title,
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = stageInfo.accentColor
                    )
                  )
                  Text(
                    text = "${chamberState.progressiveRevealStage}/4",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = stageInfo.accentColor
                    )
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = stageInfo.description,
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = TuringTextSecondary
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Neural Acoustic Resonance & Frequency Telemetry
            NeuralAcousticVisualizer(
              isPartnerSpeaking = chamberState.isPartnerTyping,
              stage = chamberState.progressiveRevealStage
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Milestone Progress Bar
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF1E293B))
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(chamberState.progressiveRevealStage / 4f)
                  .height(4.dp)
                  .clip(RoundedCornerShape(2.dp))
                  .background(stageInfo.accentColor)
              )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = stageInfo.unlockedClue,
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = TuringTextSecondary,
                lineHeight = 15.sp
              )
            )

            // Advance Stage tester chip
            Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
            ) {
              Text(
                text = "State machine triggers unblur as message milestones are hit",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 10.sp,
                  color = TuringTextSecondary.copy(alpha = 0.7f)
                )
              )

              if (chamberState.progressiveRevealStage < 4) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1E293B))
                    .clickable { viewModel.forceAdvanceStage() }
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "+ Milestone",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.sp,
                      color = TuringCyan
                    )
                  )
                }
              }
            }
          }
        }

        // Dual-Bubble Chat Conversation Stream (Task 4.3) with key diffing and contentType
        LazyColumn(
          state = listState,
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          items(
            items = chamberState.messages,
            key = { it.messageId },
            contentType = { it.senderType }
          ) { message ->
            HolographicMessageBubble(message = message)
          }

          // Artificial Friction Typing Indicator Gauge
          if (chamberState.isPartnerTyping) {
            item(key = "typing_indicator", contentType = "INDICATOR") {
              FrictionTelemetryGauge(
                isPartnerTyping = true,
                frictionDelayMs = 1800L,
                entropyScore = 0.92f
              )
            }
          }
        }

        // Live At-Each-Moment "Human or AI?" Intuition Bar
        MomentIntuitionBar(
          currentSuspicion = chamberState.momentIntuitionHuman,
          onSuspicionChange = { viewModel.updateMomentIntuition(it) },
          modifier = Modifier.padding(bottom = 2.dp)
        )

        // Quick conversation starter prompts with stable keys
        LazyRow(
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
        items(
          items = QUICK_PROMPTS,
          key = { it }
        ) { prompt ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(TuringObsidianCard)
              .border(1.dp, TuringObsidianBorder, RoundedCornerShape(12.dp))
              .clickable {
                viewModel.sendUserMessage(prompt)
              }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = prompt,
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = TuringTextSecondary
              )
            )
          }
        }
      }

      // Bottom Message Input Row
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .background(TuringObsidianCard)
          .border(
            width = 1.dp,
            color = TuringObsidianBorder,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
          )
          .padding(horizontal = 12.dp, vertical = 8.dp)
          .navigationBarsPadding()
      ) {
        OutlinedTextField(
          value = inputMessage,
          onValueChange = { inputMessage = it },
          placeholder = {
            Text(
              text = "Transmit zero-trust thought...",
              style = MaterialTheme.typography.bodyMedium.copy(color = TuringTextSecondary.copy(alpha = 0.6f))
            )
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TuringCyan,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = Color(0xFF0C111C),
            unfocusedContainerColor = Color(0xFF0C111C),
            focusedTextColor = TuringTextPrimary,
            unfocusedTextColor = TuringTextPrimary
          ),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
          keyboardActions = KeyboardActions(
            onSend = {
              if (inputMessage.isNotBlank()) {
                viewModel.sendUserMessage(inputMessage)
                inputMessage = ""
                focusManager.clearFocus()
              }
            }
          ),
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (inputMessage.isNotBlank()) TuringCyan else Color(0xFF1E293B))
            .clickable(enabled = inputMessage.isNotBlank()) {
              viewModel.sendUserMessage(inputMessage)
              inputMessage = ""
              focusManager.clearFocus()
            }
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send message",
            tint = if (inputMessage.isNotBlank()) TuringObsidian else TuringTextSecondary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
}

@Composable
fun ChatBubbleItem(message: ChatMessage) {
  val isUser = message.senderType == SenderType.USER
  val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
  val timeString = timeFormat.format(Date(message.timestamp))

  Column(
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
    modifier = Modifier.fillMaxWidth()
  ) {
    // Sender label
    Text(
      text = if (isUser) "YOU" else "PARTNER",
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = if (isUser) TuringCyan else TuringTextSecondary,
        letterSpacing = 1.sp
      ),
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )

    // Message Bubble
    Box(
      modifier = Modifier
        .widthIn(max = 290.dp)
        .clip(
          RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isUser) 16.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 16.dp
          )
        )
        .background(
          if (isUser) TuringBubbleUser else TuringBubblePartner
        )
        .then(
          if (!isUser) Modifier.border(1.dp, TuringObsidianBorder, RoundedCornerShape(16.dp)) else Modifier
        )
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Column {
        Text(
          text = message.content,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = if (isUser) TuringBubbleUserText else TuringBubblePartnerText,
            lineHeight = 20.sp
          )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = timeString,
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = (if (isUser) TuringBubbleUserText else TuringBubblePartnerText).copy(alpha = 0.6f)
          ),
          modifier = Modifier.align(Alignment.End)
        )
      }
    }
  }
}

@Composable
fun ArtificialFrictionTypingIndicator(
  statusText: String
) {
  val infiniteTransition = rememberInfiniteTransition(label = "dots_transition")

  val dot1Alpha by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 600),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot1"
  )
  val dot2Alpha by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 600, delayMillis = 200),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot2"
  )
  val dot3Alpha by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 600, delayMillis = 400),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot3"
  )

  Column(
    horizontalAlignment = Alignment.Start,
    modifier = Modifier.fillMaxWidth()
  ) {
    Text(
      text = "PARTNER // ARTIFICIAL FRICTION DELAY",
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = TuringCyan,
        letterSpacing = 1.sp
      ),
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(14.dp))
        .background(TuringBubblePartner)
        .border(1.dp, TuringCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(TuringCyan.copy(alpha = dot1Alpha))
        )
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(TuringCyan.copy(alpha = dot2Alpha))
        )
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(TuringCyan.copy(alpha = dot3Alpha))
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Text(
        text = statusText.ifEmpty { "Encrypting response..." },
        style = MaterialTheme.typography.bodySmall.copy(
          color = TuringTextSecondary,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
      )
    }
  }
}
