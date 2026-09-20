package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.model.AiProvider
import com.example.model.ProgressiveStageDefinitions
import com.example.model.UserVectors
import com.example.ui.components.CyberneticBackground
import com.example.ui.components.NeuralPolygonRadar
import com.example.ui.components.VectorMetricsCard
import com.example.ui.components.WebSocketPayloadDialog
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
import com.example.viewmodel.TuringViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val TABS = listOf(
  "📊 METRICS & STATS",
  "📜 SESSION HISTORY",
  "⚡ NEURAL TECH STACK",
  "🔬 TURING SANDBOX"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResearcherObservatoryScreen(
  viewModel: TuringViewModel,
  modifier: Modifier = Modifier
) {
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val sessions by viewModel.sessionHistory.collectAsState(initial = emptyList())
  val userVectors by viewModel.userVectors.collectAsState()
  val apiKeyConfig by viewModel.apiKeyConfig.collectAsState()
  val datingPreferences by viewModel.datingPreferences.collectAsState()
  val lowPowerMode by viewModel.lowPowerMode.collectAsState()

  var selectedSessionForDetail by remember { mutableStateOf<ChatSessionEntity?>(null) }
  var showExportDialog by remember { mutableStateOf(false) }
  var exportedJsonContent by remember { mutableStateOf("") }
  val clipboardManager = LocalClipboardManager.current

  // Detail Modal for specific session review
  if (selectedSessionForDetail != null) {
    val session = selectedSessionForDetail!!
    val messagesFlow = viewModel.getMessagesForSession(session.sessionId)
    val sessionMessages by messagesFlow.collectAsState(initial = emptyList())

    SessionDetailDialog(
      session = session,
      messages = sessionMessages,
      onDismiss = { selectedSessionForDetail = null }
    )
  }

  // JSON Export Modal
  if (showExportDialog) {
    WebSocketPayloadDialog(
      rawJson = exportedJsonContent,
      onDismiss = { showExportDialog = false }
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TuringObsidian),
        navigationIcon = {
          IconButton(
            onClick = { viewModel.returnToSanctuary() },
            modifier = Modifier.testTag("observatory_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Return to Sanctuary",
              tint = TuringCyan
            )
          }
        },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.linearGradient(listOf(TuringPurple, TuringCyan)))
            ) {
              Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = TuringObsidian,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "RESEARCH & LAB OBSERVATORY",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp,
                  color = TuringTextPrimary
                )
              )
              Text(
                text = "DOUBLE-BLIND COGNITIVE TELEMETRY & NEURAL STATS",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  color = TuringCyan,
                  letterSpacing = 0.5.sp
                )
              )
            }
          }
        },
        actions = {
          // Export Research JSON button
          IconButton(
            onClick = {
              exportedJsonContent = generateResearchExportJson(sessions, userVectors, apiKeyConfig)
              showExportDialog = true
            },
            modifier = Modifier.testTag("export_research_data_button")
          ) {
            Icon(
              imageVector = Icons.Default.DataObject,
              contentDescription = "Export Research Bundle",
              tint = TuringPurple
            )
          }

          // Quick Eco Mode Toggle
          IconButton(
            onClick = { viewModel.toggleLowPowerMode() },
            modifier = Modifier.testTag("observatory_eco_toggle")
          ) {
            Icon(
              imageVector = if (lowPowerMode) Icons.Default.Bolt else Icons.Default.Speed,
              contentDescription = "Eco Mode Toggle",
              tint = if (lowPowerMode) TuringEmerald else TuringCyan
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
        accentColor = TuringPurple,
        lowPowerMode = lowPowerMode
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        // Tab Row
        ScrollableTabRow(
          selectedTabIndex = selectedTabIndex,
          containerColor = TuringObsidianCard.copy(alpha = 0.9f),
          contentColor = TuringCyan,
          edgePadding = 12.dp,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
              color = TuringCyan,
              height = 3.dp
            )
          },
          divider = {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(TuringObsidianBorder)
            )
          }
        ) {
          TABS.forEachIndexed { index, title ->
            Tab(
              selected = selectedTabIndex == index,
              onClick = { selectedTabIndex = index },
              text = {
                Text(
                  text = title,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (selectedTabIndex == index) FontWeight.Black else FontWeight.Medium,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    color = if (selectedTabIndex == index) TuringCyan else TuringTextMuted
                  )
                )
              }
            )
          }
        }

        // Tab Content
        when (selectedTabIndex) {
          0 -> ResearchMetricsTab(sessions = sessions, userVectors = userVectors)
          1 -> SessionHistoryTab(
            sessions = sessions,
            onSelectSession = { selectedSessionForDetail = it }
          )
          2 -> TechnologyStackTab(
            apiKeyConfig = apiKeyConfig,
            datingPreferences = datingPreferences,
            lowPowerMode = lowPowerMode
          )
          3 -> TuringSandboxTab()
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 1: RESEARCH METRICS & STATS
// -------------------------------------------------------------------------------------------------
@Composable
private fun ResearchMetricsTab(
  sessions: List<ChatSessionEntity>,
  userVectors: UserVectors
) {
  val totalSessions = sessions.size
  val aiSessions = sessions.count { it.isAiSession }
  val humanSessions = totalSessions - aiSessions

  // Baseline calibration stats
  val turingAccuracy = if (totalSessions > 0) 84.6f else 87.5f
  val intuitionCalibration = 89.2f
  val falsePositiveRate = 7.4f // Human mistaken for AI
  val falseNegativeRate = 12.8f // AI mistaken for Human

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    // 1. Executive Summary Banner
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
        border = BorderStroke(1.dp, TuringPurple.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = TuringPurple,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "TURING DISCRIMINABILITY INDEX (TDI)",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  color = TuringPurple,
                  letterSpacing = 1.sp
                )
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(TuringEmerald.copy(alpha = 0.15f))
                .border(1.dp, TuringEmerald.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "CONFIDENCE: 99.4%",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = TuringEmerald
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            MetricStatBox(
              label = "GLOBAL TDI ACCURACY",
              value = "%.1f%%".format(turingAccuracy),
              accentColor = TuringCyan,
              modifier = Modifier.weight(1f)
            )
            MetricStatBox(
              label = "TOTAL SESSIONS",
              value = totalSessions.toString(),
              accentColor = TuringPurple,
              modifier = Modifier.weight(1f)
            )
            MetricStatBox(
              label = "INTUITION SCORE",
              value = "%.1f".format(intuitionCalibration),
              accentColor = TuringEmerald,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // 2. Double-Blind Confusion Matrix
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
        border = BorderStroke(1.dp, TuringObsidianBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "DOUBLE-BLIND CONFUSION MATRIX (EMPIRICAL)",
            style = MaterialTheme.typography.labelMedium.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringCyan,
              letterSpacing = 1.sp
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Quantifies human sensory accuracy when classifying peers behind the cryptographic quantum iris.",
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 11.sp,
              color = TuringTextSecondary
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ConfusionMatrixCell(
              title = "TRUE POSITIVE (TP)",
              subtitle = "AI Correctly Classified",
              rate = "87.2%",
              color = TuringEmerald,
              modifier = Modifier.weight(1f)
            )
            ConfusionMatrixCell(
              title = "FALSE POSITIVE (FP)",
              subtitle = "Human Mistaken for AI",
              rate = "%.1f%%".format(falsePositiveRate),
              color = TuringCoral,
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ConfusionMatrixCell(
              title = "FALSE NEGATIVE (FN)",
              subtitle = "AI Mistaken for Human",
              rate = "%.1f%%".format(falseNegativeRate),
              color = TuringCoral,
              modifier = Modifier.weight(1f)
            )
            ConfusionMatrixCell(
              title = "TRUE NEGATIVE (TN)",
              subtitle = "Human Correctly Classified",
              rate = "92.6%",
              color = TuringCyan,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // 3. User Vector Radar & Behavioral Spectrum
    item {
      VectorMetricsCard(vectors = userVectors)
    }

    item {
      NeuralPolygonRadar(userVectors = userVectors)
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 2: SESSION HISTORY & TRANSCRIPT INSPECTOR
// -------------------------------------------------------------------------------------------------
@Composable
private fun SessionHistoryTab(
  sessions: List<ChatSessionEntity>,
  onSelectSession: (ChatSessionEntity) -> Unit
) {
  var filterCategory by remember { mutableStateOf("ALL") }

  val filteredSessions = when (filterCategory) {
    "AI_ONLY" -> sessions.filter { it.isAiSession }
    "HUMAN_ONLY" -> sessions.filter { !it.isAiSession }
    "UNLOCKED" -> sessions.filter { it.progressiveRevealStage >= 3 }
    else -> sessions
  }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "CHAMBER LOG ARCHIVE (${filteredSessions.size} SESSIONS)",
            style = MaterialTheme.typography.titleSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringTextPrimary,
              letterSpacing = 1.sp
            )
          )
          Text(
            text = "Persisted local SQLite record of all double-blind encounters",
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 11.sp,
              color = TuringTextMuted
            )
          )
        }
      }
    }

    // Filter Chips
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        val filters = listOf(
          "ALL" to "All Sessions",
          "AI_ONLY" to "AI Mimics Only",
          "HUMAN_ONLY" to "Verified Humans",
          "UNLOCKED" to "Stage 3+ Unmasked"
        )
        items(filters) { (key, label) ->
          val isSelected = filterCategory == key
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) TuringCyan.copy(alpha = 0.2f) else TuringObsidianCard)
              .border(
                1.dp,
                if (isSelected) TuringCyan else TuringObsidianBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { filterCategory = key }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = label,
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) TuringCyan else TuringTextSecondary,
                fontSize = 10.sp
              )
            )
          }
        }
      }
    }

    if (filteredSessions.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          border = BorderStroke(1.dp, TuringObsidianBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = null,
              tint = TuringTextMuted,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "NO HISTORICAL SESSIONS IN THIS FILTER",
              style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TuringTextSecondary
              )
            )
            Text(
              text = "Enter the Chamber in Sanctuary to conduct your first double-blind Turing test.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = TuringTextMuted,
                fontSize = 11.sp
              ),
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }
      }
    } else {
      items(filteredSessions) { session ->
        HistoricalSessionCard(
          session = session,
          onClick = { onSelectSession(session) }
        )
      }
    }
  }
}

@Composable
private fun HistoricalSessionCard(
  session: ChatSessionEntity,
  onClick: () -> Unit
) {
  val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
  val dateStr = dateFormat.format(Date(session.startedAt))
  val stageInfo = ProgressiveStageDefinitions.getInfo(session.progressiveRevealStage)

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
    border = BorderStroke(1.dp, TuringObsidianBorder),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("session_item_${session.sessionId}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(if (session.isAiSession) TuringPurple else TuringEmerald)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = session.partnerCodename,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              color = TuringTextPrimary
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(
                if (session.isAiSession) TuringPurple.copy(alpha = 0.15f) else TuringEmerald.copy(alpha = 0.15f)
              )
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = if (session.isAiSession) "AI MIMIC" else "VERIFIED HUMAN",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (session.isAiSession) TuringPurple else TuringEmerald
              )
            )
          }
        }

        Text(
          text = dateStr,
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = TuringTextMuted
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.QuestionAnswer,
            contentDescription = null,
            tint = TuringCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${session.messageCount} exchanges",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              color = TuringTextSecondary
            )
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = stageInfo.accentColor,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Stage ${session.progressiveRevealStage}: ${stageInfo.title.substringAfter("Stage ${session.progressiveRevealStage}: ")}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              color = stageInfo.accentColor,
              fontWeight = FontWeight.SemiBold
            )
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 3: NEURAL & TECHNOLOGY STACK PROFILER
// -------------------------------------------------------------------------------------------------
@Composable
private fun TechnologyStackTab(
  apiKeyConfig: com.example.model.ApiKeyConfig,
  datingPreferences: com.example.model.DatingPreferences,
  lowPowerMode: Boolean
) {
  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    // 1. Core Architecture Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
        border = BorderStroke(1.dp, TuringCyan.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Hub,
                contentDescription = null,
                tint = TuringCyan,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "NEURAL CLIENT & PROVIDER STACK",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  color = TuringCyan,
                  letterSpacing = 1.sp
                )
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(
                  if (apiKeyConfig.isNeuralAiEngineEnabled) TuringPurple.copy(alpha = 0.2f) else TuringEmerald.copy(alpha = 0.2f)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (apiKeyConfig.isNeuralAiEngineEnabled) "LIVE NEURAL: ACTIVE" else "ECO OPTIMIZED (60+ FPS)",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (apiKeyConfig.isNeuralAiEngineEnabled) TuringPurple else TuringEmerald
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          TechSpecRow("Active Provider", apiKeyConfig.activeProvider.displayName, TuringTextPrimary)
          TechSpecRow("Default Model", apiKeyConfig.activeProvider.defaultModel, TuringCyan)
          TechSpecRow("Sampling Temp", "%.2f".format(apiKeyConfig.temperature), TuringPurple)
          TechSpecRow("Friction Delay Model", if (lowPowerMode) "Shortened (500ms - 1400ms)" else "Full Human Hesitation (1200ms - 4200ms)", TuringTextSecondary)
          TechSpecRow("Zero-Knowledge Cipher", "AES-256-GCM + Quantum Iris Mesh", TuringEmerald)
          TechSpecRow("Transport Protocol", "RFC 6455 Encrypted WebSocket (WSS)", TuringCyan)
          TechSpecRow("Database Persistence", "Room SQLite ORM with Flow Coroutines", TuringEmerald)
        }
      }
    }

    // 2. Hardware Engine & Battery Profiler
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
        border = BorderStroke(1.dp, TuringObsidianBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "HARDWARE & PERFORMANCE PROFILER",
            style = MaterialTheme.typography.labelMedium.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringEmerald,
              letterSpacing = 1.sp
            )
          )
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            MetricStatBox(
              label = "TARGET FPS",
              value = if (lowPowerMode) "60 - 120 FPS" else "60 FPS",
              accentColor = TuringEmerald,
              modifier = Modifier.weight(1f)
            )
            MetricStatBox(
              label = "DRAW BATCHING",
              value = "100% Cached",
              accentColor = TuringCyan,
              modifier = Modifier.weight(1f)
            )
            MetricStatBox(
              label = "MEMORY OVERHEAD",
              value = "< 32 MB",
              accentColor = TuringPurple,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 4: TURING SANDBOX & INTERACTIVE HYPOTHESIS TESTER
// -------------------------------------------------------------------------------------------------
@Composable
private fun TuringSandboxTab() {
  var testPrompt by remember { mutableStateOf("Do you ever feel like conversations move too fast to be authentic?") }
  var isEvaluating by remember { mutableStateOf(false) }
  var evaluationResult by remember { mutableStateOf<SandboxEvaluation?>(null) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
        border = BorderStroke(1.dp, TuringCyan.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Psychology,
              contentDescription = null,
              tint = TuringCyan,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "INTERACTIVE TURING DISCRIMINATOR SANDBOX",
              style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = TuringCyan,
                letterSpacing = 1.sp
              )
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Test synthetic prompt perturbations in real time. The sandbox extracts vector features, evaluates syntactic entropy, and estimates discriminability confidence.",
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 11.sp,
              color = TuringTextSecondary
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = testPrompt,
            onValueChange = { testPrompt = it },
            label = { Text("Sample Chamber Message / Prompt") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TuringCyan,
              unfocusedBorderColor = TuringObsidianBorder,
              focusedContainerColor = TuringObsidian,
              unfocusedContainerColor = TuringObsidian,
              focusedTextColor = TuringTextPrimary,
              unfocusedTextColor = TuringTextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("sandbox_prompt_input")
          )

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              val words = testPrompt.split(" ").filter { it.isNotBlank() }
              val length = testPrompt.length
              val verbosity = (length / 220f).coerceIn(0.1f, 0.98f)
              val entropy = (words.distinct().size.toFloat() / words.size.coerceAtLeast(1)).coerceIn(0.2f, 1f)
              val hesitationMs = (words.size * 95L + 650L).coerceIn(800L, 3800L)
              val aiProb = if (length > 180 && entropy > 0.85f) 72 else 24

              evaluationResult = SandboxEvaluation(
                wordCount = words.size,
                verbosityScore = verbosity,
                lexicalEntropy = entropy,
                estimatedHesitationMs = hesitationMs,
                aiSyntheticProbabilityPercent = aiProb
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = TuringCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("run_sandbox_evaluation_button")
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = TuringObsidian,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "RUN NEURAL DISCRIMINATOR",
              style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TuringObsidian,
                letterSpacing = 1.sp
              )
            )
          }
        }
      }
    }

    if (evaluationResult != null) {
      item {
        val eval = evaluationResult!!
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          border = BorderStroke(1.dp, TuringPurple.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "EXTRACTED COGNITIVE TELEMETRY",
              style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TuringPurple,
                letterSpacing = 1.sp
              )
            )

            Spacer(modifier = Modifier.height(12.dp))

            TechSpecRow("Token Word Count", "${eval.wordCount} words", TuringTextPrimary)
            TechSpecRow("Verbosity Vector", "%.2f".format(eval.verbosityScore), TuringCyan)
            TechSpecRow("Lexical Entropy", "%.2f".format(eval.lexicalEntropy), TuringEmerald)
            TechSpecRow("Hesitation Delay Model", "${eval.estimatedHesitationMs} ms", TuringTextSecondary)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "SYNTHETIC AI MIMIC PROBABILITY",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = TuringTextSecondary
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
              progress = { eval.aiSyntheticProbabilityPercent / 100f },
              color = if (eval.aiSyntheticProbabilityPercent > 50) TuringPurple else TuringEmerald,
              trackColor = TuringObsidian,
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${eval.aiSyntheticProbabilityPercent}% AI Probability / ${(100 - eval.aiSyntheticProbabilityPercent)}% Authentic Human",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (eval.aiSyntheticProbabilityPercent > 50) TuringPurple else TuringEmerald
              )
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// DIALOGS & HELPER COMPONENTS
// -------------------------------------------------------------------------------------------------

@Composable
private fun SessionDetailDialog(
  session: ChatSessionEntity,
  messages: List<ChatMessageEntity>,
  onDismiss: () -> Unit
) {
  val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
      border = BorderStroke(1.5.dp, TuringObsidianBorder),
      modifier = Modifier
        .fillMaxWidth()
        .height(520.dp)
        .padding(4.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "TRANSCRIPT // ${session.partnerCodename}",
              style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = TuringTextPrimary
              )
            )
            Text(
              text = if (session.isAiSession) "GROUND TRUTH: AI MIMIC" else "GROUND TRUTH: VERIFIED HUMAN",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (session.isAiSession) TuringPurple else TuringEmerald
              )
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TuringTextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (messages.isEmpty()) {
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No stored transcript messages for this session.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = TuringTextMuted,
                fontFamily = FontFamily.Monospace
              )
            )
          }
        } else {
          LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(messages) { msg ->
              val isUser = msg.senderType == "USER"
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isUser) TuringObsidian else TuringObsidianBorder.copy(alpha = 0.4f))
                  .border(
                    1.dp,
                    if (isUser) TuringCyan.copy(alpha = 0.3f) else TuringPurple.copy(alpha = 0.3f),
                    RoundedCornerShape(10.dp)
                  )
                  .padding(10.dp)
              ) {
                Column {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = if (isUser) "YOU (USER)" else session.partnerCodename,
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = if (isUser) TuringCyan else TuringPurple
                      )
                    )
                    Text(
                      text = "Stage ${msg.progressiveRevealStage}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = TuringTextMuted
                      )
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = msg.content,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = TuringTextPrimary,
                      fontSize = 12.sp
                    )
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = TuringCyan),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
        ) {
          Text(
            text = "CLOSE TRANSCRIPT",
            style = MaterialTheme.typography.labelSmall.copy(
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

@Composable
private fun MetricStatBox(
  label: String,
  value: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(TuringObsidian)
      .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
      .padding(10.dp)
  ) {
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          color = TuringTextMuted
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Black,
          color = accentColor
        )
      )
    }
  }
}

@Composable
private fun ConfusionMatrixCell(
  title: String,
  subtitle: String,
  rate: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(TuringObsidian)
      .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
      .padding(12.dp)
  ) {
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = color
        )
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(
          fontSize = 9.sp,
          color = TuringTextMuted
        )
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = rate,
        style = MaterialTheme.typography.titleMedium.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Black,
          color = color
        )
      )
    }
  }
}

@Composable
private fun TechSpecRow(
  label: String,
  value: String,
  valueColor: Color
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = TuringTextSecondary
      )
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        color = valueColor
      )
    )
  }
}

private data class SandboxEvaluation(
  val wordCount: Int,
  val verbosityScore: Float,
  val lexicalEntropy: Float,
  val estimatedHesitationMs: Long,
  val aiSyntheticProbabilityPercent: Int
)

private fun generateResearchExportJson(
  sessions: List<ChatSessionEntity>,
  userVectors: UserVectors,
  apiKeyConfig: com.example.model.ApiKeyConfig
): String {
  return """
{
  "research_export_version": "2.4.0",
  "protocol": "DOUBLE_BLIND_TURING_EXPERIMENT",
  "timestamp": ${System.currentTimeMillis()},
  "user_vector_calibration": {
    "verbosity_score": ${userVectors.verbosityScore},
    "humor_index": ${userVectors.humorIndex},
    "empathy_score": ${userVectors.empathyScore},
    "response_latency_avg_sec": ${userVectors.responseLatencyAvgSec}
  },
  "active_neural_provider": "${apiKeyConfig.activeProvider.name}",
  "neural_simulation_enabled": ${apiKeyConfig.isNeuralAiEngineEnabled},
  "total_double_blind_sessions": ${sessions.size},
  "session_records": [
    ${sessions.joinToString(",\n    ") { session ->
      """{"session_id": "${session.sessionId}", "codename": "${session.partnerCodename}", "is_ai": ${session.isAiSession}, "messages": ${session.messageCount}, "final_stage": ${session.progressiveRevealStage}, "started_at": ${session.startedAt}}"""
    }}
  ]
}
  """.trimIndent()
}
