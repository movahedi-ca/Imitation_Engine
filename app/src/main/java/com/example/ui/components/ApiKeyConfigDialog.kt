package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AiProvider
import com.example.model.ApiKeyConfig
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

/**
 * Bring Your Own API Key (BYOK) & Native Connections Dialog:
 * Configure API keys for Google Gemini, xAI Grok, Muse Synthesizer,
 * and Claude MCP (Anthropic Model Context Protocol).
 */
@Composable
fun ApiKeyConfigDialog(
  currentConfig: ApiKeyConfig,
  onSaveConfig: (ApiKeyConfig) -> Unit,
  onDismiss: () -> Unit
) {
  var isNeuralAiEngineEnabled by remember { mutableStateOf(currentConfig.isNeuralAiEngineEnabled) }
  var activeProvider by remember { mutableStateOf(currentConfig.activeProvider) }
  var geminiKey by remember { mutableStateOf(currentConfig.geminiApiKey) }
  var grokKey by remember { mutableStateOf(currentConfig.grokApiKey) }
  var museKey by remember { mutableStateOf(currentConfig.museApiKey) }
  var claudeKey by remember { mutableStateOf(currentConfig.claudeMcpApiKey) }
  var mcpUrl by remember { mutableStateOf(currentConfig.customMcpServerUrl) }
  var temperature by remember { mutableFloatStateOf(currentConfig.temperature) }
  var showSecrets by remember { mutableStateOf(false) }
  var showWarningPrompt by remember { mutableStateOf(false) }

  if (showWarningPrompt) {
    NeuralWarningDialog(
      onConfirmEnable = {
        isNeuralAiEngineEnabled = true
        showWarningPrompt = false
      },
      onDismiss = {
        isNeuralAiEngineEnabled = false
        showWarningPrompt = false
      }
    )
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, TuringObsidianBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .testTag("api_key_config_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Dialog Header
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
                imageVector = Icons.Default.Key,
                contentDescription = null,
                tint = TuringPurple,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "NEURAL AI & BYOK CONFIG",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = TuringTextPrimary,
                  letterSpacing = 1.sp
                )
              )
              Text(
                text = "Grok, Muse, Claude MCP & Gemini Native",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 11.sp,
                  color = TuringTextMuted
                )
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TuringTextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Master Neural Activation Card
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isNeuralAiEngineEnabled) Color(0xFF160E2A) else Color(0xFF09141F)
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isNeuralAiEngineEnabled) TuringPurple.copy(alpha = 0.5f) else TuringEmerald.copy(alpha = 0.35f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = if (isNeuralAiEngineEnabled) Icons.Default.Psychology else Icons.Default.Tune,
                contentDescription = null,
                tint = if (isNeuralAiEngineEnabled) TuringPurple else TuringEmerald,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = if (isNeuralAiEngineEnabled) "NEURAL AI ENGINE: ENABLED" else "NEURAL AI: DISABLED (ECO)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isNeuralAiEngineEnabled) TuringPurple else TuringEmerald
                  )
                )
                Text(
                  text = if (isNeuralAiEngineEnabled) "Continuous live AI generation active" else "Turned off for maximum speed & responsiveness",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = TuringTextSecondary
                  )
                )
              }
            }

            androidx.compose.material3.Switch(
              checked = isNeuralAiEngineEnabled,
              onCheckedChange = { isChecked ->
                if (isChecked) {
                  showWarningPrompt = true
                } else {
                  isNeuralAiEngineEnabled = false
                }
              },
              colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = TuringPurple,
                checkedTrackColor = TuringPurple.copy(alpha = 0.4f),
                uncheckedThumbColor = TuringEmerald,
                uncheckedTrackColor = TuringEmerald.copy(alpha = 0.2f)
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Provider Selector Carousel / Column
        Text(
          text = "ACTIVE NEURAL ENGINE PROVIDER",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = TuringTextSecondary
          )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          AiProvider.values().forEach { provider ->
            val isSelected = activeProvider == provider
            val badgeColor = when (provider) {
              AiProvider.GEMINI -> TuringCyan
              AiProvider.GROK -> TuringCoral
              AiProvider.MUSE -> TuringPurple
              AiProvider.CLAUDE_MCP -> TuringEmerald
              AiProvider.LOCAL_CALIBRATION -> TuringTextMuted
            }

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) badgeColor.copy(alpha = 0.15f) else TuringObsidian)
                .border(
                  1.dp,
                  if (isSelected) badgeColor else TuringObsidianBorder,
                  RoundedCornerShape(12.dp)
                )
                .clickable { activeProvider = provider }
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = provider.displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) badgeColor else TuringTextPrimary
                    )
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "(${provider.defaultModel})",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 10.sp,
                      color = TuringTextMuted
                    )
                  )
                }
                Text(
                  text = provider.description,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = TuringTextSecondary
                  ),
                  modifier = Modifier.padding(top = 2.dp)
                )
              }

              if (isSelected) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = TuringObsidian
                    )
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // API Key Inputs
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "CREDENTIALS & CONFIGURATION",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringTextSecondary
            )
          )
          Text(
            text = if (showSecrets) "Hide Keys" else "Reveal Keys",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              color = TuringCyan
            ),
            modifier = Modifier.clickable { showSecrets = !showSecrets }
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 1. Google Gemini Key Input
        OutlinedTextField(
          value = geminiKey,
          onValueChange = { geminiKey = it },
          label = { Text("Gemini API Key (Leave empty to use Secrets panel)") },
          placeholder = { Text("AIzaSy...") },
          visualTransformation = if (showSecrets) VisualTransformation.None else PasswordVisualTransformation(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TuringCyan,
            unfocusedBorderColor = TuringObsidianBorder,
            focusedContainerColor = TuringObsidian,
            unfocusedContainerColor = TuringObsidian,
            focusedTextColor = TuringTextPrimary,
            unfocusedTextColor = TuringTextPrimary,
            focusedLabelColor = TuringCyan,
            unfocusedLabelColor = TuringTextMuted
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("gemini_api_key_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2. xAI Grok Key Input
        OutlinedTextField(
          value = grokKey,
          onValueChange = { grokKey = it },
          label = { Text("xAI Grok API Key (api.x.ai)") },
          placeholder = { Text("xai-...") },
          visualTransformation = if (showSecrets) VisualTransformation.None else PasswordVisualTransformation(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TuringCoral,
            unfocusedBorderColor = TuringObsidianBorder,
            focusedContainerColor = TuringObsidian,
            unfocusedContainerColor = TuringObsidian,
            focusedTextColor = TuringTextPrimary,
            unfocusedTextColor = TuringTextPrimary,
            focusedLabelColor = TuringCoral,
            unfocusedLabelColor = TuringTextMuted
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("grok_api_key_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Muse Synthesizer Key Input
        OutlinedTextField(
          value = museKey,
          onValueChange = { museKey = it },
          label = { Text("Muse Synthesizer API Key") },
          placeholder = { Text("muse_live_...") },
          visualTransformation = if (showSecrets) VisualTransformation.None else PasswordVisualTransformation(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TuringPurple,
            unfocusedBorderColor = TuringObsidianBorder,
            focusedContainerColor = TuringObsidian,
            unfocusedContainerColor = TuringObsidian,
            focusedTextColor = TuringTextPrimary,
            unfocusedTextColor = TuringTextPrimary,
            focusedLabelColor = TuringPurple,
            unfocusedLabelColor = TuringTextMuted
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("muse_api_key_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Claude MCP Key & Endpoint
        OutlinedTextField(
          value = claudeKey,
          onValueChange = { claudeKey = it },
          label = { Text("Claude MCP / Anthropic API Key") },
          placeholder = { Text("sk-ant-api03-...") },
          visualTransformation = if (showSecrets) VisualTransformation.None else PasswordVisualTransformation(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TuringEmerald,
            unfocusedBorderColor = TuringObsidianBorder,
            focusedContainerColor = TuringObsidian,
            unfocusedContainerColor = TuringObsidian,
            focusedTextColor = TuringTextPrimary,
            unfocusedTextColor = TuringTextPrimary,
            focusedLabelColor = TuringEmerald,
            unfocusedLabelColor = TuringTextMuted
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("claude_mcp_key_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = mcpUrl,
          onValueChange = { mcpUrl = it },
          label = { Text("Custom Claude MCP Server Endpoint (Optional)") },
          placeholder = { Text("https://mcp.yourdomain.com/v1/messages") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TuringEmerald,
            unfocusedBorderColor = TuringObsidianBorder,
            focusedContainerColor = TuringObsidian,
            unfocusedContainerColor = TuringObsidian,
            focusedTextColor = TuringTextPrimary,
            unfocusedTextColor = TuringTextPrimary,
            focusedLabelColor = TuringEmerald,
            unfocusedLabelColor = TuringTextMuted
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("claude_mcp_url_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Temperature Slider
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "CONVERSATIONAL TEMPERATURE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringTextSecondary
            )
          )
          Text(
            text = String.format("%.2f", temperature),
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringCyan
            )
          )
        }

        Slider(
          value = temperature,
          onValueChange = { temperature = it },
          valueRange = 0.2f..1.2f,
          colors = SliderDefaults.colors(
            thumbColor = TuringCyan,
            activeTrackColor = TuringCyan,
            inactiveTrackColor = TuringObsidian
          )
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Save Button
        Button(
          onClick = {
            val updated = currentConfig.copy(
              isNeuralAiEngineEnabled = isNeuralAiEngineEnabled,
              activeProvider = activeProvider,
              geminiApiKey = geminiKey.trim(),
              grokApiKey = grokKey.trim(),
              museApiKey = museKey.trim(),
              claudeMcpApiKey = claudeKey.trim(),
              customMcpServerUrl = mcpUrl.trim(),
              temperature = temperature
            )
            onSaveConfig(updated)
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = TuringPurple),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("save_api_key_config_button")
        ) {
          Text(
            text = "SAVE NEURAL CONFIGURATION",
            style = MaterialTheme.typography.labelMedium.copy(
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
}
