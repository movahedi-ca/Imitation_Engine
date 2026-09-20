package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ApiKeyConfig
import com.example.model.DatingPreferences
import com.example.model.UserProfile
import com.example.model.UserVectors
import com.example.ui.components.ApiKeyConfigDialog
import com.example.ui.components.CyberneticBackground
import com.example.ui.components.DatingFiltersDialog
import com.example.ui.components.NeuralPolygonRadar
import com.example.ui.components.QuantumCipherAvatar
import com.example.ui.components.VectorMetricsCard
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
  userProfile: UserProfile,
  userVectors: UserVectors,
  apiKeyConfig: ApiKeyConfig = ApiKeyConfig(),
  datingPreferences: DatingPreferences = DatingPreferences(),
  onUpdateProfile: (UserProfile) -> Unit,
  onUpdateApiKeyConfig: (ApiKeyConfig) -> Unit = {},
  onUpdateDatingPreferences: (DatingPreferences) -> Unit = {},
  onResetVectors: () -> Unit,
  onOpenResearchLab: () -> Unit = {},
  onNavigateBack: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  var pseudonym by remember { mutableStateOf(userProfile.pseudonym) }
  var bio by remember { mutableStateOf(userProfile.bio) }
  var location by remember { mutableStateOf(userProfile.location) }
  var biometricVerified by remember { mutableStateOf(userProfile.isBiometricVerified) }
  var isSavedNoticeVisible by remember { mutableStateOf(false) }

  var showApiKeyConfigDialog by remember { mutableStateOf(false) }
  var showDatingFiltersDialog by remember { mutableStateOf(false) }

  if (showApiKeyConfigDialog) {
    ApiKeyConfigDialog(
      currentConfig = apiKeyConfig,
      onSaveConfig = onUpdateApiKeyConfig,
      onDismiss = { showApiKeyConfigDialog = false }
    )
  }

  if (showDatingFiltersDialog) {
    DatingFiltersDialog(
      currentPreferences = datingPreferences,
      onSavePreferences = onUpdateDatingPreferences,
      onDismiss = { showDatingFiltersDialog = false }
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("account_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = TuringTextPrimary
            )
          }
        },
        title = {
          Column {
            Text(
              text = "ACCOUNT CIPHER",
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = TuringTextPrimary
              )
            )
            Text(
              text = "IDENTITY & CRYPTOGRAPHIC MANAGEMENT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = TuringCyan
              )
            )
          }
        },
        actions = {
          IconButton(
            onClick = onLogout,
            modifier = Modifier.testTag("account_logout_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Logout,
              contentDescription = "Sign Out",
              tint = TuringTextMuted
            )
          }
        }
      )
    },
    containerColor = Color.Transparent,
    modifier = modifier
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize()) {
      CyberneticBackground(accentColor = TuringEmerald)

      LazyColumn(
        contentPadding = PaddingValues(
          top = innerPadding.calculateTopPadding() + 8.dp,
          bottom = innerPadding.calculateBottomPadding() + 24.dp,
          start = 16.dp,
          end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
      // Identity Card
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TuringObsidianBorder, RoundedCornerShape(16.dp))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              QuantumCipherAvatar(
                stage = 4,
                size = 56.dp,
                showRings = false
              )

              Spacer(modifier = Modifier.width(16.dp))

              Column {
                Text(
                  text = pseudonym,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TuringTextPrimary
                  )
                )
                Text(
                  text = userProfile.handle,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = TuringCyan,
                    fontFamily = FontFamily.Monospace
                  )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = TuringEmerald,
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Liveness Attestation Active",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.sp,
                      color = TuringEmerald
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Key Fingerprint
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TuringObsidian)
                .padding(horizontal = 12.dp, vertical = 8.dp)
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
                    tint = TuringTextMuted,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "KEY FINGERPRINT",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 9.sp,
                      color = TuringTextMuted
                    )
                  )
                }
                Text(
                  text = userProfile.encryptionKeyFingerprint,
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

      // Stats Summary
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Card(
            colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .border(1.dp, TuringObsidianBorder, RoundedCornerShape(12.dp))
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "SESSIONS COMPLETED",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  color = TuringTextMuted
                )
              )
              Text(
                text = "${userProfile.totalSessionsCompleted}",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = TuringTextPrimary
                )
              )
            }
          }

          Card(
            colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .border(1.dp, TuringObsidianBorder, RoundedCornerShape(12.dp))
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "TURING ACCURACY",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  color = TuringTextMuted
                )
              )
              Text(
                text = "${userProfile.intuitionAccuracyPercent}%",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = TuringEmerald
                )
              )
            }
          }
        }
      }

      // Psycholinguistic Vector Calibration Radar
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TuringObsidianBorder, RoundedCornerShape(16.dp))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "PSYCHOLINGUISTIC VECTORS",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TuringTextPrimary
                  )
                )
                Text(
                  text = "Dynamic frequency used in radar queue matchmaking",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = TuringTextMuted
                  )
                )
              }

              IconButton(
                onClick = onResetVectors,
                modifier = Modifier.testTag("account_reset_vectors_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = "Recalibrate vectors",
                  tint = TuringCyan
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Radar visualization
            NeuralPolygonRadar(
              userVectors = userVectors,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }

      // Dating App Filters & Preferences Configuration Card
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TuringObsidianBorder, RoundedCornerShape(16.dp))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "DATING FILTERS & MATCH PREFERENCES",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TuringTextPrimary
                  )
                )
                Text(
                  text = "Age, geographic radius, intent, and AI-to-Human calibration ratio",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = TuringTextMuted
                  )
                )
              }

              IconButton(
                onClick = { showDatingFiltersDialog = true },
                modifier = Modifier.testTag("account_open_filters_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Tune,
                  contentDescription = "Edit Dating Filters",
                  tint = TuringCyan
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(TuringObsidian)
                  .border(1.dp, TuringObsidianBorder, RoundedCornerShape(8.dp))
                  .padding(8.dp)
              ) {
                Column {
                  Text("AGE RANGE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TuringTextMuted, fontFamily = FontFamily.Monospace))
                  Text("${datingPreferences.minAge} - ${datingPreferences.maxAge} yrs", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TuringCyan, fontFamily = FontFamily.Monospace))
                }
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(TuringObsidian)
                  .border(1.dp, TuringObsidianBorder, RoundedCornerShape(8.dp))
                  .padding(8.dp)
              ) {
                Column {
                  Text("MAX RADIUS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TuringTextMuted, fontFamily = FontFamily.Monospace))
                  Text("${datingPreferences.maxDistanceMiles} miles", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TuringCyan, fontFamily = FontFamily.Monospace))
                }
              }

              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(TuringObsidian)
                  .border(1.dp, TuringObsidianBorder, RoundedCornerShape(8.dp))
                  .padding(8.dp)
              ) {
                Column {
                  Text("AI POOL RATIO", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = TuringTextMuted, fontFamily = FontFamily.Monospace))
                  Text("${datingPreferences.aiRatioPercent}% AI", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TuringPurple, fontFamily = FontFamily.Monospace))
                }
              }
            }
          }
        }
      }

      // Bring Your Own API Key & Native External LLM Connections
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TuringPurple.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "BRING YOUR OWN API KEY (BYOK)",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TuringPurple
                  )
                )
                Text(
                  text = "Natively connect Grok, Muse, Claude MCP, or Gemini",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = TuringTextMuted
                  )
                )
              }

              IconButton(
                onClick = { showApiKeyConfigDialog = true },
                modifier = Modifier.testTag("account_open_byok_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Key,
                  contentDescription = "Configure API Keys",
                  tint = TuringPurple
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TuringObsidian)
                .border(1.dp, TuringPurple.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .clickable { showApiKeyConfigDialog = true }
                .padding(12.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column {
                  Text(
                    text = "ACTIVE NEURAL ENGINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 9.sp,
                      color = TuringTextMuted
                    )
                  )
                  Text(
                    text = apiKeyConfig.activeProvider.displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = TuringTextPrimary
                    )
                  )
                }

                Text(
                  text = "CONFIGURE →",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = TuringPurple,
                    fontWeight = FontWeight.Bold
                  )
                )
              }
            }
          }
        }
      }

      // Research & Statistics Observatory Hub Entry Point
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TuringCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable { onOpenResearchLab() }
            .testTag("account_open_research_lab_card")
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(36.dp)
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
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "RESEARCH & LAB STATS",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TuringCyan
                  )
                )
                Text(
                  text = "Confusion Matrix, Session Archive & Tech Stack",
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
              tint = TuringCyan
            )
          }
        }
      }

      // Profile Configuration Settings
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TuringObsidianBorder, RoundedCornerShape(16.dp))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "EDIT IDENTIFIER SETTINGS",
              style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TuringTextPrimary
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = pseudonym,
              onValueChange = { pseudonym = it },
              label = { Text("Session Pseudonym") },
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = TuringObsidian,
                unfocusedContainerColor = TuringObsidian,
                focusedBorderColor = TuringCyan,
                unfocusedBorderColor = TuringObsidianBorder,
                focusedTextColor = TuringTextPrimary,
                unfocusedTextColor = TuringTextPrimary,
                focusedLabelColor = TuringCyan,
                unfocusedLabelColor = TuringTextMuted
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("account_pseudonym_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = location,
              onValueChange = { location = it },
              label = { Text("Geographic Coordinates / Clue") },
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = TuringObsidian,
                unfocusedContainerColor = TuringObsidian,
                focusedBorderColor = TuringCyan,
                unfocusedBorderColor = TuringObsidianBorder,
                focusedTextColor = TuringTextPrimary,
                unfocusedTextColor = TuringTextPrimary,
                focusedLabelColor = TuringCyan,
                unfocusedLabelColor = TuringTextMuted
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("account_location_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = bio,
              onValueChange = { bio = it },
              label = { Text("Core Philosophy / Bio") },
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = TuringObsidian,
                unfocusedContainerColor = TuringObsidian,
                focusedBorderColor = TuringCyan,
                unfocusedBorderColor = TuringObsidianBorder,
                focusedTextColor = TuringTextPrimary,
                unfocusedTextColor = TuringTextPrimary,
                focusedLabelColor = TuringCyan,
                unfocusedLabelColor = TuringTextMuted
              ),
              shape = RoundedCornerShape(10.dp),
              maxLines = 3,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("account_bio_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hardware Liveness toggle
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(TuringObsidian)
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Biometric Liveness Proof",
                  style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TuringTextPrimary
                  )
                )
                Text(
                  text = "Attach cryptographic key proof to prevent bot spoofing",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = TuringTextMuted
                  )
                )
              }

              Switch(
                checked = biometricVerified,
                onCheckedChange = { biometricVerified = it },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = TuringEmerald,
                  checkedTrackColor = TuringEmerald.copy(alpha = 0.3f),
                  uncheckedThumbColor = TuringTextMuted,
                  uncheckedTrackColor = TuringObsidianBorder
                ),
                modifier = Modifier.testTag("account_biometric_switch")
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
              onClick = {
                val updated = userProfile.copy(
                  pseudonym = pseudonym,
                  bio = bio,
                  location = location,
                  isBiometricVerified = biometricVerified
                )
                onUpdateProfile(updated)
                isSavedNoticeVisible = true
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = TuringCyan,
                contentColor = TuringObsidian
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("account_save_button")
            ) {
              Text(
                text = "SAVE PROFILE CHANGES",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                )
              )
            }

            if (isSavedNoticeVisible) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "✓ Changes encrypted and updated to local cipher store.",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
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
}
