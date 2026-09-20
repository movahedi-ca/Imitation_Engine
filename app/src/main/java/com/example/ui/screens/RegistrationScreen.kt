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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberneticBackground
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.ui.theme.TuringTextMuted
import com.example.ui.theme.TuringTextPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AuthMode {
  LOGIN,
  REGISTER,
  BIOMETRIC_ENROLL
}

@Composable
fun RegistrationScreen(
  onAuthComplete: (username: String, pseudonym: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var authMode by remember { mutableStateOf(AuthMode.REGISTER) }
  var handle by remember { mutableStateOf("@observer_alpha") }
  var pseudonym by remember { mutableStateOf("Observer-01") }
  var passphrase by remember { mutableStateOf("••••••••••••") }
  var isAuthenticating by remember { mutableStateOf(false) }
  var livenessChecked by remember { mutableStateOf(false) }
  var enrollmentStep by remember { mutableStateOf(1) }

  val coroutineScope = rememberCoroutineScope()

  Box(
    modifier = modifier.fillMaxSize()
  ) {
    CyberneticBackground(accentColor = TuringCyan)

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      item {
        // App Identity Header
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
              Brush.linearGradient(listOf(TuringCyan, TuringPurple))
            )
            .border(2.dp, TuringCyan.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
        ) {
          Text(
            text = "T",
            style = MaterialTheme.typography.headlineLarge.copy(
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              color = TuringObsidian
            )
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "TURING SANCTUARY",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp,
            color = TuringTextPrimary
          )
        )

        Text(
          text = "ZERO-TRUST SOCIAL DISCOVERY",
          style = MaterialTheme.typography.labelMedium.copy(
            fontFamily = FontFamily.Monospace,
            color = TuringCyan,
            letterSpacing = 2.sp
          )
        )

        Spacer(modifier = Modifier.height(28.dp))
      }

      item {
        // Auth Card
        Card(
          colors = CardDefaults.cardColors(containerColor = TuringObsidianCard),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TuringObsidianBorder, RoundedCornerShape(16.dp))
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Mode selector tabs
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(TuringObsidian)
                .padding(4.dp)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (authMode == AuthMode.REGISTER) TuringCyan.copy(alpha = 0.2f) else Color.Transparent)
                  .clickable { authMode = AuthMode.REGISTER }
                  .padding(vertical = 8.dp)
                  .testTag("tab_register")
              ) {
                Text(
                  text = "REGISTER",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (authMode == AuthMode.REGISTER) TuringCyan else TuringTextMuted
                  )
                )
              }

              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (authMode == AuthMode.LOGIN) TuringCyan.copy(alpha = 0.2f) else Color.Transparent)
                  .clickable { authMode = AuthMode.LOGIN }
                  .padding(vertical = 8.dp)
                  .testTag("tab_login")
              ) {
                Text(
                  text = "SIGN IN",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (authMode == AuthMode.LOGIN) TuringCyan else TuringTextMuted
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pseudonym / Handle input
            OutlinedTextField(
              value = handle,
              onValueChange = { handle = it },
              label = { Text("Protocol Handle") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = TuringCyan
                )
              },
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
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_handle_input")
            )

            if (authMode == AuthMode.REGISTER) {
              Spacer(modifier = Modifier.height(14.dp))

              OutlinedTextField(
                value = pseudonym,
                onValueChange = { pseudonym = it },
                label = { Text("Double-Blind Pseudonym") },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = TuringPurple
                  )
                },
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = TuringObsidian,
                  unfocusedContainerColor = TuringObsidian,
                  focusedBorderColor = TuringPurple,
                  unfocusedBorderColor = TuringObsidianBorder,
                  focusedTextColor = TuringTextPrimary,
                  unfocusedTextColor = TuringTextPrimary,
                  focusedLabelColor = TuringPurple,
                  unfocusedLabelColor = TuringTextMuted
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_pseudonym_input")
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = passphrase,
              onValueChange = { passphrase = it },
              label = { Text("Zero-Knowledge Keyphrase") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Key,
                  contentDescription = null,
                  tint = TuringEmerald
                )
              },
              visualTransformation = PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = TuringObsidian,
                unfocusedContainerColor = TuringObsidian,
                focusedBorderColor = TuringEmerald,
                unfocusedBorderColor = TuringObsidianBorder,
                focusedTextColor = TuringTextPrimary,
                unfocusedTextColor = TuringTextPrimary,
                focusedLabelColor = TuringEmerald,
                unfocusedLabelColor = TuringTextMuted
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_keyphrase_input")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Liveness & Biometric Verification Simulation
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (livenessChecked) TuringEmerald.copy(alpha = 0.12f) else TuringObsidian)
                .border(
                  1.dp,
                  if (livenessChecked) TuringEmerald.copy(alpha = 0.4f) else TuringObsidianBorder,
                  RoundedCornerShape(12.dp)
                )
                .clickable {
                  livenessChecked = !livenessChecked
                }
                .padding(12.dp)
                .testTag("auth_biometric_toggle")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (livenessChecked) Icons.Default.CheckCircle else Icons.Default.Fingerprint,
                  contentDescription = null,
                  tint = if (livenessChecked) TuringEmerald else TuringCyan,
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = if (livenessChecked) "BIOMETRIC LIVENESS VERIFIED" else "TAP TO ATTEST BIOMETRIC LIVENESS",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = if (livenessChecked) TuringEmerald else TuringTextPrimary
                    )
                  )
                  Text(
                    text = "Hardware attestation token prevents Sybil bot attacks",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontSize = 10.sp,
                      color = TuringTextMuted
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            Button(
              onClick = {
                isAuthenticating = true
                coroutineScope.launch {
                  delay(900)
                  isAuthenticating = false
                  onAuthComplete(handle, pseudonym)
                }
              },
              enabled = !isAuthenticating && handle.isNotBlank(),
              colors = ButtonDefaults.buttonColors(
                containerColor = TuringCyan,
                contentColor = TuringObsidian
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("auth_submit_button")
            ) {
              if (isAuthenticating) {
                CircularProgressIndicator(
                  color = TuringObsidian,
                  modifier = Modifier.size(20.dp),
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "VERIFYING CRYPTOGRAPHIC PROOF...",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  )
                )
              } else {
                Text(
                  text = if (authMode == AuthMode.REGISTER) "ENROLL IN SANCTUARY" else "AUTHENTICATE SESSION",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                  )
                )
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = "Zero entity booleans are stored or transmitted. All conversations remain cryptographically masked until milestone unlocks.",
          style = MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = TuringTextMuted,
            lineHeight = 14.sp
          ),
          modifier = Modifier.padding(horizontal = 16.dp)
        )
      }
    }
  }
}
