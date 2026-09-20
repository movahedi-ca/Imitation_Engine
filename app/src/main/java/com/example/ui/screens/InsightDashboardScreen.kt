package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberneticBackground
import com.example.ui.components.HolographicVerdictCard
import com.example.ui.components.NeuralPolygonRadar
import com.example.ui.components.ProgressiveBlurAvatar
import com.example.ui.components.QuantumCipherAvatar
import com.example.ui.components.VectorMetricsCard
import com.example.ui.components.WebSocketPayloadDialog
import com.example.ui.theme.TuringCoral
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextPrimary
import com.example.ui.theme.TuringTextSecondary
import com.example.viewmodel.TuringViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightDashboardScreen(
  viewModel: TuringViewModel,
  modifier: Modifier = Modifier
) {
  val sessionResult by viewModel.sessionResult.collectAsState()
  var userVote by remember { mutableStateOf<Boolean?>(null) } // true = guessed human, false = guessed AI
  var isUnmasked by remember { mutableStateOf(false) }
  var selectedPayloadJson by remember { mutableStateOf<String?>(null) }

  if (selectedPayloadJson != null) {
    WebSocketPayloadDialog(
      rawJson = selectedPayloadJson ?: "",
      onDismiss = { selectedPayloadJson = null }
    )
  }

  val result = sessionResult ?: return

  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
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
          Column {
            Text(
              text = "TURING INSIGHT DASHBOARD",
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TuringTextPrimary,
                letterSpacing = 1.sp
              )
            )
            Text(
              text = "POST-SESSION CALIBRATION // VECTOR DYNAMICS",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = TuringCyan
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
      CyberneticBackground(accentColor = TuringPurple)

      LazyColumn(
        contentPadding = PaddingValues(
          top = innerPadding.calculateTopPadding() + 8.dp,
          bottom = innerPadding.calculateBottomPadding() + 28.dp,
          start = 16.dp,
          end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxSize()
      ) {

        // Holographic Attestation Badge if unmasked
        if (isUnmasked) {
          item {
            HolographicVerdictCard(result = result)
          }
        }

      // The Interactive Turing Test Blind Vote / Unmasking Card
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUnmasked) {
              if (result.partner.isAi) TuringPurple else TuringEmerald
            } else TuringCyan
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .fillMaxWidth()
              .padding(22.dp)
          ) {
            // Avatar (obfuscated until unmasked)
            ProgressiveBlurAvatar(
              stage = if (isUnmasked) 4 else result.finalRevealStage,
              gradientStart = result.partner.avatarGradientStart,
              gradientEnd = result.partner.avatarGradientEnd,
              size = 80.dp,
              showBadge = false
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (!isUnmasked) {
              Text(
                text = "ZERO-TRUST VERIFICATION",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  color = TuringCyan,
                  letterSpacing = 1.sp
                )
              )
              Text(
                text = "Was your partner Human or AI?",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = TuringTextPrimary
                ),
                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
              )
              Text(
                text = "Before decrypting the session record, submit your psycholinguistic guess.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TuringTextSecondary,
                  fontSize = 12.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
              )

              Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Button(
                  onClick = {
                    userVote = true
                    isUnmasked = true
                  },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = TuringEmerald.copy(alpha = 0.2f)
                  ),
                  border = androidx.compose.foundation.BorderStroke(1.dp, TuringEmerald),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Person,
                      contentDescription = null,
                      tint = TuringEmerald,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "VERIFIED HUMAN",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TuringEmerald
                      )
                    )
                  }
                }

                Button(
                  onClick = {
                    userVote = false
                    isUnmasked = true
                  },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = TuringPurple.copy(alpha = 0.2f)
                  ),
                  border = androidx.compose.foundation.BorderStroke(1.dp, TuringPurple),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Psychology,
                      contentDescription = null,
                      tint = TuringPurple,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "AI AGENT",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TuringPurple
                      )
                    )
                  }
                }
              }
            } else {
              // Unmasked Verdict
              val userWasCorrect = (userVote == true && !result.partner.isAi) || (userVote == false && result.partner.isAi)

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(
                    if (result.partner.isAi) TuringPurple.copy(alpha = 0.15f)
                    else TuringEmerald.copy(alpha = 0.15f)
                  )
                  .border(
                    1.dp,
                    if (result.partner.isAi) TuringPurple else TuringEmerald,
                    RoundedCornerShape(8.dp)
                  )
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = if (result.partner.isAi) "AI CALIBRATION AGENT" else "VERIFIED HUMAN MATCH",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (result.partner.isAi) TuringPurple else TuringEmerald,
                    letterSpacing = 1.sp
                  )
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = result.partner.realName,
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Bold,
                  color = TuringTextPrimary
                )
              )

              Text(
                text = result.partner.title,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = TuringCyan,
                  fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier.padding(top = 2.dp)
              )

              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = TuringTextSecondary,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${result.partner.location} • ${result.partner.distanceMiles} miles away",
                  style = MaterialTheme.typography.bodySmall.copy(color = TuringTextSecondary)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Dating Profile Quick Badges (Age, Gender, Intent)
              Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TuringObsidian)
                    .border(1.dp, TuringObsidianBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = "${result.partner.age} YRS",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.sp,
                      color = TuringCyan,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TuringObsidian)
                    .border(1.dp, TuringObsidianBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = result.partner.gender.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.sp,
                      color = TuringTextSecondary
                    )
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TuringObsidian)
                    .border(1.dp, TuringObsidianBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = result.partner.relationshipGoal.replace("_", " "),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.sp,
                      color = TuringEmerald,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // User Guess Accuracy Alert
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(
                    if (userWasCorrect) TuringEmerald.copy(alpha = 0.1f) else TuringCoral.copy(alpha = 0.1f)
                  )
                  .border(
                    1.dp,
                    if (userWasCorrect) TuringEmerald.copy(alpha = 0.3f) else TuringCoral.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                  )
                  .padding(12.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = if (userWasCorrect) Icons.Default.CheckCircle else Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = if (userWasCorrect) TuringEmerald else TuringCoral,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = if (userWasCorrect) {
                      "Your intuition was correct! You accurately detected ${if (result.partner.isAi) "the AI calibration archetype." else "the human conversational rhythm."}"
                    } else {
                      "The Imitation Engine was deceptive! You categorized a ${if (result.partner.isAi) "calibrated AI agent as human." else "verified human as an AI agent."}"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = TuringTextPrimary,
                      fontSize = 12.sp
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = "\"${result.partner.philosophy}\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = TuringTextSecondary,
                  fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
              )
            }
          }
        }
      }

      // Psychological Purpose Note (From Manifest Part 1)
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1424)),
          border = androidx.compose.foundation.BorderStroke(1.dp, TuringCyan.copy(alpha = 0.3f)),
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
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = TuringCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "CALIBRATION ANALYSIS // NO QUIZZES",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = TuringCyan,
                  letterSpacing = 1.sp
                )
              )
              Text(
                text = "Unlike legacy dating sites that rely on static 50-question quizzes, Turing calibrates your compatibility dynamically by measuring active sentence complexity, latency pauses, and empathetic validation during real dialogue.",
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

      // Communication Vectors Card (Calibrated from this session)
      item {
        NeuralPolygonRadar(
          userVectors = result.calibratedUserVectors,
          modifier = Modifier.fillMaxWidth()
        )
      }

      // Raw Contract Payloads Inspector List
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          border = androidx.compose.foundation.BorderStroke(1.dp, TuringObsidianBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Icon(
                imageVector = Icons.Default.DataObject,
                contentDescription = null,
                tint = TuringEmerald,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "SESSION WEBSOCKET AUDIT LOG",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = TuringEmerald,
                  letterSpacing = 1.sp
                )
              )
            }
            Text(
              text = "Tap to inspect the exact message:receive JSON contracts emitted",
              style = MaterialTheme.typography.bodySmall.copy(
                color = TuringTextSecondary,
                fontSize = 11.sp
              ),
              modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )

            if (result.rawContractPayloads.isEmpty()) {
              Text(
                text = "Standard session contract active.",
                style = MaterialTheme.typography.bodySmall.copy(color = TuringTextSecondary)
              )
            } else {
              result.rawContractPayloads.take(3).forEachIndexed { index, payloadJson ->
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF070A0F))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                    .clickable { selectedPayloadJson = payloadJson }
                    .padding(10.dp)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = "Payload #${index + 1} (event: message:receive)",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = TuringCyan
                      )
                    )
                    Text(
                      text = "VIEW JSON",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TuringEmerald
                      )
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Research Observatory & Return to Sanctuary CTA Buttons
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedButton(
            onClick = { viewModel.navigateTo(com.example.viewmodel.TuringScreen.RESEARCH_LAB) },
            border = androidx.compose.foundation.BorderStroke(1.dp, TuringPurple),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Science,
              contentDescription = null,
              tint = TuringPurple,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "VIEW FULL RESEARCH & HISTORY OBSERVATORY",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TuringPurple,
                letterSpacing = 0.5.sp
              )
            )
          }

          Button(
            onClick = { viewModel.returnToSanctuary() },
            colors = ButtonDefaults.buttonColors(containerColor = TuringCyan),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
          ) {
            Icon(
              imageVector = Icons.Default.RestartAlt,
              contentDescription = null,
              tint = TuringObsidian,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "RETURN TO SANCTUARY",
              style = MaterialTheme.typography.labelLarge.copy(
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
  }
}
}
