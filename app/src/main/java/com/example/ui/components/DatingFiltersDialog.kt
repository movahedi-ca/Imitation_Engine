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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DatingPreferences
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
 * Full Generic Dating Apps Features & Discovery Filters Dialog:
 * Age range, distance radius, gender/dating intent, relationship goals,
 * lifestyle habits, and Turing AI/Human double-blind mixture ratio.
 */
@Composable
fun DatingFiltersDialog(
  currentPreferences: DatingPreferences,
  onSavePreferences: (DatingPreferences) -> Unit,
  onDismiss: () -> Unit
) {
  var minAge by remember { mutableFloatStateOf(currentPreferences.minAge.toFloat()) }
  var maxAge by remember { mutableFloatStateOf(currentPreferences.maxAge.toFloat()) }
  var maxDistance by remember { mutableFloatStateOf(currentPreferences.maxDistanceMiles.toFloat()) }
  var selectedGender by remember { mutableStateOf(currentPreferences.genderPreference) }
  var selectedGoal by remember { mutableStateOf(currentPreferences.relationshipGoal) }
  var smokingFilter by remember { mutableStateOf(currentPreferences.smokingFilter) }
  var drinkingFilter by remember { mutableStateOf(currentPreferences.drinkingFilter) }
  var verifiedOnly by remember { mutableStateOf(currentPreferences.verifiedOnly) }
  var allowAiMatches by remember { mutableStateOf(currentPreferences.allowAiMatches) }
  var aiRatio by remember { mutableFloatStateOf(currentPreferences.aiRatioPercent.toFloat()) }

  val genderOptions = listOf(
    "EVERYONE" to "Everyone",
    "WOMEN" to "Women",
    "MEN" to "Men",
    "NON_BINARY" to "Non-Binary"
  )

  val goalOptions = listOf(
    "LONG_TERM" to "Long-Term Partner",
    "INTELLECTUAL_RAPPORT" to "Intellectual Rapport",
    "CASUAL" to "Casual Dating",
    "OPEN_TO_ANY" to "Open to Anything"
  )

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, TuringObsidianBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .testTag("dating_filters_dialog")
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
                .size(32.dp)
                .clip(CircleShape)
                .background(TuringCyan.copy(alpha = 0.15f))
            ) {
              Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = TuringCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "DATING FILTERS & MATCH PROTOCOL",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = TuringTextPrimary,
                  letterSpacing = 1.sp
                )
              )
              Text(
                text = "Preferences for blind radar queue",
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

        Spacer(modifier = Modifier.height(18.dp))

        // 1. Age Range Filter
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "AGE RANGE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringTextSecondary
            )
          )
          Text(
            text = "${minAge.toInt()} - ${maxAge.toInt()} yrs",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringCyan
            )
          )
        }

        @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
        RangeSlider(
          value = minAge..maxAge,
          onValueChange = { range ->
            minAge = range.start
            maxAge = range.endInclusive
          },
          valueRange = 18f..65f,
          colors = SliderDefaults.colors(
            thumbColor = TuringCyan,
            activeTrackColor = TuringCyan,
            inactiveTrackColor = TuringObsidian
          ),
          modifier = Modifier.testTag("filter_age_slider")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Maximum Distance Filter
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "MAXIMUM DISTANCE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringTextSecondary
            )
          )
          Text(
            text = "${maxDistance.toInt()} miles",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = TuringCyan
            )
          )
        }

        Slider(
          value = maxDistance,
          onValueChange = { maxDistance = it },
          valueRange = 5f..150f,
          colors = SliderDefaults.colors(
            thumbColor = TuringCyan,
            activeTrackColor = TuringCyan,
            inactiveTrackColor = TuringObsidian
          ),
          modifier = Modifier.testTag("filter_distance_slider")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Gender / Discovery Preference
        Text(
          text = "SHOW ME",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = TuringTextSecondary
          )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          genderOptions.forEach { (key, label) ->
            val isSel = selectedGender == key
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSel) TuringCyan.copy(alpha = 0.2f) else TuringObsidian)
                .border(1.dp, if (isSel) TuringCyan else TuringObsidianBorder, RoundedCornerShape(8.dp))
                .clickable { selectedGender = key }
                .padding(vertical = 8.dp)
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSel) TuringCyan else TuringTextSecondary
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Relationship Goal / Dating Intent
        Text(
          text = "RELATIONSHIP INTENT",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = TuringTextSecondary
          )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          goalOptions.forEach { (key, label) ->
            val isSel = selectedGoal == key
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSel) TuringPurple.copy(alpha = 0.2f) else TuringObsidian)
                .border(1.dp, if (isSel) TuringPurple else TuringObsidianBorder, RoundedCornerShape(10.dp))
                .clickable { selectedGoal = key }
                .padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSel) TuringTextPrimary else TuringTextSecondary
                )
              )
              if (isSel) {
                Icon(
                  imageVector = Icons.Default.Favorite,
                  contentDescription = null,
                  tint = TuringPurple,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Verification Toggle
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TuringObsidian)
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = null,
              tint = TuringEmerald,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Biometrically Verified Profiles",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = TuringTextPrimary
                )
              )
              Text(
                text = "Only match with liveness-proven accounts",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  color = TuringTextMuted
                )
              )
            }
          }

          Switch(
            checked = verifiedOnly,
            onCheckedChange = { verifiedOnly = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = TuringEmerald,
              checkedTrackColor = TuringEmerald.copy(alpha = 0.3f),
              uncheckedThumbColor = TuringTextMuted,
              uncheckedTrackColor = TuringObsidianBorder
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        var showWarningPrompt by remember { mutableStateOf(false) }

        if (showWarningPrompt) {
          NeuralWarningDialog(
            onConfirmEnable = {
              allowAiMatches = true
              showWarningPrompt = false
            },
            onDismiss = {
              allowAiMatches = false
              showWarningPrompt = false
            }
          )
        }

        // 6. Turing Double-Blind AI Calibration Engine Toggle
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (allowAiMatches) Color(0xFF140D26) else Color(0xFF0B1724)
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (allowAiMatches) TuringPurple.copy(alpha = 0.5f) else TuringEmerald.copy(alpha = 0.35f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Icon(
                  imageVector = if (allowAiMatches) Icons.Default.Psychology else Icons.Default.VerifiedUser,
                  contentDescription = null,
                  tint = if (allowAiMatches) TuringPurple else TuringEmerald,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = if (allowAiMatches) "LOCAL AI CALIBRATION MIMICS" else "VERIFIED HUMAN MATCHING",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = if (allowAiMatches) TuringPurple else TuringEmerald,
                      letterSpacing = 0.5.sp
                    )
                  )
                  Text(
                    text = if (allowAiMatches) "High-compute neural simulation active" else "Optimized Mode (Recommended, fast & smooth)",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 10.sp,
                      color = TuringTextMuted
                    )
                  )
                }
              }

              Switch(
                checked = allowAiMatches,
                onCheckedChange = { isChecked ->
                  if (isChecked) {
                    showWarningPrompt = true
                  } else {
                    allowAiMatches = false
                  }
                },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = TuringPurple,
                  checkedTrackColor = TuringPurple.copy(alpha = 0.4f),
                  uncheckedThumbColor = TuringEmerald,
                  uncheckedTrackColor = TuringEmerald.copy(alpha = 0.2f)
                ),
                modifier = Modifier.testTag("allow_ai_matches_switch")
              )
            }

            if (allowAiMatches) {
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "AI DISCOVERY RATIO",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TuringTextSecondary
                  )
                )
                Text(
                  text = "${aiRatio.toInt()}% AI / ${(100 - aiRatio.toInt())}% HUMAN",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = TuringTextPrimary
                  )
                )
              }

              Slider(
                value = aiRatio,
                onValueChange = { aiRatio = it },
                valueRange = 10f..90f,
                colors = SliderDefaults.colors(
                  thumbColor = TuringPurple,
                  activeTrackColor = TuringPurple,
                  inactiveTrackColor = TuringObsidian
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Save Button
        Button(
          onClick = {
            val updated = currentPreferences.copy(
              minAge = minAge.toInt(),
              maxAge = maxAge.toInt(),
              maxDistanceMiles = maxDistance.toInt(),
              genderPreference = selectedGender,
              relationshipGoal = selectedGoal,
              smokingFilter = smokingFilter,
              drinkingFilter = drinkingFilter,
              verifiedOnly = verifiedOnly,
              allowAiMatches = allowAiMatches,
              aiRatioPercent = aiRatio.toInt()
            )
            onSavePreferences(updated)
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = TuringCyan),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("save_dating_filters_button")
        ) {
          Text(
            text = "APPLY DATING FILTERS",
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
}
