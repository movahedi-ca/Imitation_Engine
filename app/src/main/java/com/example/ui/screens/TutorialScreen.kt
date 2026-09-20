package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberneticBackground
import com.example.ui.components.ProgressiveBlurAvatar
import com.example.ui.components.QuantumCipherAvatar
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary

data class TutorialStep(
  val stepIndex: Int,
  val icon: ImageVector,
  val title: String,
  val badgeText: String,
  val description: String,
  val highlightClue: String,
  val accentColor: Color
)

@Composable
fun TutorialScreen(
  onCompleteTutorial: () -> Unit,
  onSkipTutorial: () -> Unit,
  modifier: Modifier = Modifier
) {
  val steps = remember {
    listOf(
      TutorialStep(
        stepIndex = 0,
        icon = Icons.Default.Radar,
        title = "THE SANCTUARY",
        badgeText = "DOUBLE-BLIND RADAR",
        description = "Enter an anonymous global queue matched solely by your psycholinguistic frequency. You will be paired with another entity—either human or machine.",
        highlightClue = "Zero-trust protocol ensures no entity identifiers are known beforehand.",
        accentColor = TuringCyan
      ),
      TutorialStep(
        stepIndex = 1,
        icon = Icons.Default.Shield,
        title = "ZERO-TRUST PAYLOADS",
        badgeText = "IDENTICAL CONTRACT",
        description = "Both human and artificial minds transmit identical JSON messages. No entity boolean exists on the wire. Artificial friction mimics natural human pacing.",
        highlightClue = "Inspect the live WebSocket payload contract at any time in The Chamber.",
        accentColor = TuringEmerald
      ),
      TutorialStep(
        stepIndex = 2,
        icon = Icons.Default.BlurOn,
        title = "PROGRESSIVE REVEAL",
        badgeText = "5-STAGE GAUSSIAN SHADER",
        description = "Each message exchange unlocks conversational milestones. From heavy 38dp cryptographic blur down to crystal clarity as mutual rapport grows.",
        highlightClue = "Depth of connection earns visual clarity and location hints.",
        accentColor = TuringPurple
      ),
      TutorialStep(
        stepIndex = 3,
        icon = Icons.Default.Psychology,
        title = "THE TURING VERDICT",
        badgeText = "POST-SESSION INSIGHT",
        description = "When ready, conclude the session and guess: Were you speaking to a Human or an AI? Calibrate your psycholinguistic vectors and intuition score.",
        highlightClue = "Review speech cadence, empathy scores, and compatibility vectors.",
        accentColor = TuringCyan
      )
    )
  }

  var currentStepIndex by remember { mutableIntStateOf(0) }
  val currentStep = steps[currentStepIndex]

  Box(modifier = modifier.fillMaxSize()) {
    CyberneticBackground(accentColor = currentStep.accentColor)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
    // Top Bar: Skip button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "PROTOCOL ONBOARDING",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp,
          color = TuringTextMuted
        )
      )

      TextButton(
        onClick = onSkipTutorial,
        modifier = Modifier.testTag("tutorial_skip_button")
      ) {
        Text(
          text = "SKIP",
          style = MaterialTheme.typography.labelMedium.copy(
            fontFamily = FontFamily.Monospace,
            color = TuringCyan
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Step indicators
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(bottom = 24.dp)
    ) {
      steps.forEachIndexed { index, step ->
        Box(
          modifier = Modifier
            .height(4.dp)
            .weight(1f)
            .clip(RoundedCornerShape(2.dp))
            .background(
              if (index <= currentStepIndex) step.accentColor else TuringObsidianBorder
            )
        )
      }
    }

    // Interactive Step Demonstration
    AnimatedContent(
      targetState = currentStep,
      transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
      label = "tutorial_slide",
      modifier = Modifier.weight(1f)
    ) { step ->
      Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Visual Hero Graphic
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(step.accentColor.copy(alpha = 0.25f), Color.Transparent)
              )
            )
            .border(2.dp, step.accentColor.copy(alpha = 0.5f), CircleShape)
        ) {
          if (step.stepIndex == 2) {
            // Quantum iris progressive reveal showcase
            QuantumCipherAvatar(
              stage = 1,
              size = 110.dp,
              showRings = true
            )
          } else {
            Icon(
              imageVector = step.icon,
              contentDescription = null,
              tint = step.accentColor,
              modifier = Modifier.size(60.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(step.accentColor.copy(alpha = 0.15f))
            .border(1.dp, step.accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = step.badgeText,
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = step.accentColor
            )
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = step.title,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = TuringTextPrimary,
            letterSpacing = 2.sp
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = step.description,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = TuringTextPrimary,
            lineHeight = 22.sp
          ),
          modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Highlight card
        Card(
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TuringObsidianBorder, RoundedCornerShape(12.dp))
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = step.accentColor,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = step.highlightClue,
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = TuringTextMuted
              )
            )
          }
        }
      }
    }

    // Bottom Navigation Buttons
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (currentStepIndex > 0) {
        TextButton(
          onClick = { currentStepIndex-- },
          modifier = Modifier.testTag("tutorial_prev_button")
        ) {
          Text(
            text = "PREVIOUS",
            style = MaterialTheme.typography.labelMedium.copy(
              fontFamily = FontFamily.Monospace,
              color = TuringTextMuted
            )
          )
        }
      } else {
        Spacer(modifier = Modifier.width(8.dp))
      }

      Button(
        onClick = {
          if (currentStepIndex < steps.size - 1) {
            currentStepIndex++
          } else {
            onCompleteTutorial()
          }
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = currentStep.accentColor,
          contentColor = TuringObsidian
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("tutorial_next_button")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (currentStepIndex < steps.size - 1) "CONTINUE" else "ENTER SANCTUARY",
            style = MaterialTheme.typography.labelMedium.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
}
