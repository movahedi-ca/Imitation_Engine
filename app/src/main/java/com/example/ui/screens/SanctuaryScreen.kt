package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.platform.testTag
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatSessionEntity
import com.example.ui.components.ApiKeyConfigDialog
import com.example.ui.components.CyberneticBackground
import com.example.ui.components.DatingFiltersDialog
import com.example.ui.components.NeuralPolygonRadar
import com.example.ui.components.NeuralWarningDialog
import com.example.ui.components.RadarPulseMatcher
import com.example.ui.components.VectorMetricsCard
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Analytics
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary
import com.example.ui.theme.TuringTextSecondary
import com.example.viewmodel.QueueState
import com.example.viewmodel.TuringViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SanctuaryScreen(
  viewModel: TuringViewModel,
  modifier: Modifier = Modifier
) {
  val sanctuaryState by viewModel.sanctuaryState.collectAsState()
  val userVectors by viewModel.userVectors.collectAsState()
  val sessionHistory by viewModel.sessionHistory.collectAsState(initial = emptyList())
  val datingPreferences by viewModel.datingPreferences.collectAsState()
  val apiKeyConfig by viewModel.apiKeyConfig.collectAsState()
  val lowPowerMode by viewModel.lowPowerMode.collectAsState()

  var showDatingFiltersDialog by remember { mutableStateOf(false) }
  var showApiKeyConfigDialog by remember { mutableStateOf(false) }
  var showAiModeWarningPrompt by remember { mutableStateOf(false) }

  val isSearching = sanctuaryState.queueState != QueueState.IDLE

  if (showAiModeWarningPrompt) {
    NeuralWarningDialog(
      onConfirmEnable = {
        viewModel.setQueueMode("AI_CALIBRATION")
        showAiModeWarningPrompt = false
      },
      onDismiss = {
        showAiModeWarningPrompt = false
      }
    )
  }

  if (showDatingFiltersDialog) {
    DatingFiltersDialog(
      currentPreferences = datingPreferences,
      onSavePreferences = { viewModel.updateDatingPreferences(it) },
      onDismiss = { showDatingFiltersDialog = false }
    )
  }

  if (showApiKeyConfigDialog) {
    ApiKeyConfigDialog(
      currentConfig = apiKeyConfig,
      onSaveConfig = { viewModel.updateApiKeyConfig(it) },
      onDismiss = { showApiKeyConfigDialog = false }
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                  Brush.linearGradient(
                    listOf(TuringCyan, TuringPurple)
                  )
                )
            ) {
              Text(
                text = "T",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  color = TuringObsidian,
                  fontFamily = FontFamily.Monospace
                )
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "TURING",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.ExtraBold,
                  letterSpacing = 2.sp,
                  color = TuringTextPrimary
                )
              )
              Text(
                text = if (lowPowerMode) "SANCTUARY // 60+ FPS ECO MODE" else "SANCTUARY // FULL NEURAL FX",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  color = if (lowPowerMode) TuringEmerald else TuringCyan,
                  letterSpacing = 1.sp
                )
              )
            }
          }
        },
        actions = {
          // Performance / Eco Mode quick toggle
          IconButton(
            onClick = { viewModel.toggleLowPowerMode() },
            modifier = Modifier.testTag("sanctuary_eco_mode_button")
          ) {
            Icon(
              imageVector = if (lowPowerMode) Icons.Default.Bolt else Icons.Default.Speed,
              contentDescription = "Toggle Eco Performance Mode",
              tint = if (lowPowerMode) TuringEmerald else TuringCyan
            )
          }

          // Research & Lab Observatory
          IconButton(
            onClick = { viewModel.navigateTo(com.example.viewmodel.TuringScreen.RESEARCH_LAB) },
            modifier = Modifier.testTag("sanctuary_research_lab_button")
          ) {
            Icon(
              imageVector = Icons.Default.Science,
              contentDescription = "Research & Telemetry Lab",
              tint = TuringPurple
            )
          }

          IconButton(
            onClick = { showDatingFiltersDialog = true },
            modifier = Modifier.testTag("sanctuary_dating_filters_button")
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "Dating Filters & Preferences",
              tint = TuringCyan
            )
          }

          IconButton(
            onClick = { showApiKeyConfigDialog = true },
            modifier = Modifier.testTag("sanctuary_api_keys_button")
          ) {
            Icon(
              imageVector = Icons.Default.Key,
              contentDescription = "Bring Your Own API Key",
              tint = TuringPurple
            )
          }

          IconButton(
            onClick = { viewModel.navigateTo(com.example.viewmodel.TuringScreen.TUTORIAL) },
            modifier = Modifier.testTag("sanctuary_tutorial_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.HelpOutline,
              contentDescription = "Protocol Guide & Tutorial",
              tint = TuringCyan
            )
          }

          IconButton(
            onClick = { viewModel.navigateTo(com.example.viewmodel.TuringScreen.ACCOUNT_MANAGEMENT) },
            modifier = Modifier.testTag("sanctuary_account_button")
          ) {
            Icon(
              imageVector = Icons.Default.ManageAccounts,
              contentDescription = "Account Management",
              tint = TuringCyan
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

      LazyColumn(
        contentPadding = PaddingValues(
          top = innerPadding.calculateTopPadding() + 8.dp,
        bottom = innerPadding.calculateBottomPadding() + 24.dp,
        start = 16.dp,
        end = 16.dp
      ),
      verticalArrangement = Arrangement.spacedBy(18.dp),
      modifier = Modifier.fillMaxSize()
    ) {

      // Queue Radar & Matchmaking Hero
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, TuringObsidianBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp)
          ) {
            // Radar matcher visualizer
            RadarPulseMatcher(
              isScanning = isSearching,
              modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (sanctuaryState.queueState) {
              QueueState.IDLE -> {
                Text(
                  text = "Imitation Engine Ready",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TuringTextPrimary
                  )
                )
                Text(
                  text = "1,420 peers & calibration nodes in active pool",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TuringTextSecondary
                  ),
                  modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // Active Preferences & AI Engine Readout Bar
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  // Dating Filters Pill Button
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(10.dp))
                      .background(TuringObsidian)
                      .border(1.dp, TuringObsidianBorder, RoundedCornerShape(10.dp))
                      .clickable { showDatingFiltersDialog = true }
                      .padding(horizontal = 10.dp, vertical = 8.dp)
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween,
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                          imageVector = Icons.Default.Tune,
                          contentDescription = null,
                          tint = TuringCyan,
                          modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = "${datingPreferences.minAge}-${datingPreferences.maxAge}y • ${datingPreferences.maxDistanceMiles}mi",
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TuringTextPrimary
                          )
                        )
                      }
                      Text(
                        text = "EDIT",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontFamily = FontFamily.Monospace,
                          fontSize = 9.sp,
                          color = TuringCyan,
                          fontWeight = FontWeight.Bold
                        )
                      )
                    }
                  }

                  // Active AI Engine Pill Button
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(10.dp))
                      .background(TuringPurple.copy(alpha = 0.12f))
                      .border(1.dp, TuringPurple.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                      .clickable { showApiKeyConfigDialog = true }
                      .padding(horizontal = 10.dp, vertical = 8.dp)
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween,
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                          imageVector = Icons.Default.Key,
                          contentDescription = null,
                          tint = TuringPurple,
                          modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = apiKeyConfig.activeProvider.displayName,
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TuringPurple
                          ),
                          maxLines = 1
                        )
                      }
                      Text(
                        text = "BYOK",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontFamily = FontFamily.Monospace,
                          fontSize = 9.sp,
                          color = TuringPurple,
                          fontWeight = FontWeight.Bold
                        )
                      )
                    }
                  }
                }

                // Mode Selector
                Row(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
                ) {
                  QueueModePill(
                    label = "Verified Match",
                    selected = sanctuaryState.queueMode == "VECTOR_COMPATIBILITY",
                    onClick = { viewModel.setQueueMode("VECTOR_COMPATIBILITY") },
                    modifier = Modifier.weight(1f)
                  )
                  QueueModePill(
                    label = "AI Calibration Only",
                    selected = sanctuaryState.queueMode == "AI_CALIBRATION",
                    onClick = {
                      if (sanctuaryState.queueMode != "AI_CALIBRATION") {
                        showAiModeWarningPrompt = true
                      }
                    },
                    modifier = Modifier.weight(1f)
                  )
                }

                Button(
                  onClick = {
                    val forceAi = if (sanctuaryState.queueMode == "AI_CALIBRATION") true else null
                    viewModel.startMatchmaking(forceAi)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = TuringCyan),
                  shape = RoundedCornerShape(14.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.SettingsInputAntenna,
                    contentDescription = null,
                    tint = TuringObsidian,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "ENTER THE CHAMBER",
                    style = MaterialTheme.typography.labelLarge.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = TuringObsidian,
                      letterSpacing = 1.sp
                    )
                  )
                }
              }

              QueueState.SCANNING -> {
                Text(
                  text = "Scanning Vector Proximity...",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TuringCyan
                  )
                )
                Text(
                  text = "Synthesizing vector match across pgvector cluster",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TuringTextSecondary
                  ),
                  modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )

                OutlinedButton(
                  onClick = { viewModel.cancelMatchmaking() },
                  border = androidx.compose.foundation.BorderStroke(1.dp, TuringObsidianBorder),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = "ABORT QUEUE",
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontFamily = FontFamily.Monospace,
                      color = TuringTextSecondary
                    )
                  )
                }
              }

              QueueState.PAIRING -> {
                Text(
                  text = "Pairing Signal Found",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TuringEmerald
                  )
                )
                Text(
                  text = "Deploying Zero-Trust obfuscation veil...",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TuringTextSecondary
                  ),
                  modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )
              }

              QueueState.LOCKED_IN -> {
                Text(
                  text = "Handshake Authenticated",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TuringCyan
                  )
                )
                Text(
                  text = "Opening secure dual-bubble channel...",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TuringTextSecondary
                  ),
                  modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )
              }
            }
          }
        }
      }

      // Zero-Trust & Imitation Engine Protocol Banner
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1322)),
          border = androidx.compose.foundation.BorderStroke(1.dp, TuringCyan.copy(alpha = 0.25f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(16.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(TuringCyan.copy(alpha = 0.15f))
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = TuringCyan,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "ZERO-TRUST DISCOVERY PROTOCOL",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = TuringCyan,
                  letterSpacing = 1.sp
                )
              )
              Text(
                text = "You will be matched with either a Verified Human or an AI Calibration Agent. The client payload is cryptographically identical. Profiles are unblurred solely through alternating conversation milestones.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TuringTextSecondary,
                  fontSize = 12.sp,
                  lineHeight = 17.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }
      }

      // Active Psycholinguistic Vectors (Multi-axial spider web)
      item {
        NeuralPolygonRadar(
          userVectors = userVectors,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // Researcher Observatory & Telemetry Hub Feature Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, TuringPurple.copy(alpha = 0.5f)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.navigateTo(com.example.viewmodel.TuringScreen.RESEARCH_LAB) }
            .testTag("sanctuary_research_hub_card")
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(TuringPurple.copy(alpha = 0.2f))
                ) {
                  Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = TuringPurple,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "RESEARCH & LAB OBSERVATORY",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      letterSpacing = 1.sp,
                      color = TuringTextPrimary
                    )
                  )
                  Text(
                    text = "Cognitive Analytics, Confusion Matrix & Sandbox",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontSize = 11.sp,
                      color = TuringTextMuted
                    )
                  )
                }
              }

              Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = TuringCyan,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(TuringObsidian)
                  .border(1.dp, TuringCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                  .padding(8.dp)
              ) {
                Column {
                  Text(
                    text = "TDI ACCURACY",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 9.sp,
                      color = TuringTextMuted
                    )
                  )
                  Text(
                    text = "87.5%",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      color = TuringCyan
                    )
                  )
                }
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(TuringObsidian)
                  .border(1.dp, TuringEmerald.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                  .padding(8.dp)
              ) {
                Column {
                  Text(
                    text = "CONFUSION MATRIX",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 9.sp,
                      color = TuringTextMuted
                    )
                  )
                  Text(
                    text = "Calibrated",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      color = TuringEmerald
                    )
                  )
                }
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(TuringObsidian)
                  .border(1.dp, TuringPurple.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                  .padding(8.dp)
              ) {
                Column {
                  Text(
                    text = "ROOM ARCHIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 9.sp,
                      color = TuringTextMuted
                    )
                  )
                  Text(
                    text = "${sessionHistory.size} Logged",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      color = TuringPurple
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = { viewModel.navigateTo(com.example.viewmodel.TuringScreen.RESEARCH_LAB) },
              colors = ButtonDefaults.buttonColors(containerColor = TuringPurple),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "OPEN RESEARCH & LAB OBSERVATORY",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  letterSpacing = 1.sp
                )
              )
            }
          }
        }
      }

      // Room Session History
      item {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = TuringTextSecondary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CALIBRATION & DISCOVERY ARCHIVES",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringTextSecondary,
              letterSpacing = 1.sp
            )
          )
        }
      }

      if (sessionHistory.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, TuringObsidianBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
            ) {
              Text(
                text = "No prior sessions stored in Room vault",
                style = MaterialTheme.typography.bodySmall.copy(color = TuringTextSecondary)
              )
              Text(
                text = "Initiate your first session to calibrate your vectors.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TuringTextSecondary.copy(alpha = 0.6f),
                  fontSize = 11.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }
      } else {
        items(
          items = sessionHistory,
          key = { it.sessionId },
          contentType = { "SESSION_ARCHIVE" }
        ) { session ->
          SessionArchiveItem(session)
        }
      }
    }
  }
}
}

@Composable
fun QueueModePill(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(if (selected) TuringCyan.copy(alpha = 0.15f) else Color(0xFF0F172A))
      .border(
        1.dp,
        if (selected) TuringCyan else TuringObsidianBorder,
        RoundedCornerShape(10.dp)
      )
      .clickable { onClick() }
      .padding(vertical = 10.dp, horizontal = 12.dp)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        color = if (selected) TuringCyan else TuringTextSecondary
      )
    )
  }
}

@Composable
fun SessionArchiveItem(session: ChatSessionEntity) {
  val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
  val formattedDate = dateFormat.format(Date(session.startedAt))

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
    border = androidx.compose.foundation.BorderStroke(1.dp, TuringObsidianBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (session.isAiSession) TuringPurple.copy(alpha = 0.2f) else TuringEmerald.copy(alpha = 0.2f))
        ) {
          Icon(
            imageVector = if (session.isAiSession) Icons.Default.Psychology else Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (session.isAiSession) TuringPurple else TuringEmerald,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = session.partnerCodename,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = TuringTextPrimary
            )
          )
          Text(
            text = "$formattedDate • ${session.messageCount} messages",
            style = MaterialTheme.typography.bodySmall.copy(
              color = TuringTextSecondary,
              fontSize = 11.sp
            )
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF1E293B))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = if (session.isAiSession) "AI CALIBRATION" else "VERIFIED HUMAN",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (session.isAiSession) TuringPurple else TuringEmerald
          )
        )
      }
    }
  }
}
