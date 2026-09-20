package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.AccountManagementScreen
import com.example.ui.screens.ChamberScreen
import com.example.ui.screens.InsightDashboardScreen
import com.example.ui.screens.RegistrationScreen
import com.example.ui.screens.ResearcherObservatoryScreen
import com.example.ui.screens.SanctuaryScreen
import com.example.ui.screens.TutorialScreen
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringTheme
import com.example.viewmodel.TuringScreen
import com.example.viewmodel.TuringViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: TuringViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      TuringTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = TuringObsidian
        ) {
          TuringApp(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun TuringApp(viewModel: TuringViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val userVectors by viewModel.userVectors.collectAsState()
  val apiKeyConfig by viewModel.apiKeyConfig.collectAsState()
  val datingPreferences by viewModel.datingPreferences.collectAsState()

  Crossfade(
    targetState = currentScreen,
    animationSpec = tween(durationMillis = 350),
    label = "screen_crossfade"
  ) { screen ->
    when (screen) {
      TuringScreen.REGISTRATION -> RegistrationScreen(
        onAuthComplete = { handle, pseudonym ->
          viewModel.completeRegistration(handle, pseudonym)
        }
      )
      TuringScreen.TUTORIAL -> TutorialScreen(
        onCompleteTutorial = { viewModel.completeTutorial() },
        onSkipTutorial = { viewModel.completeTutorial() }
      )
      TuringScreen.SANCTUARY -> SanctuaryScreen(viewModel = viewModel)
      TuringScreen.CHAMBER -> ChamberScreen(viewModel = viewModel)
      TuringScreen.INSIGHT_DASHBOARD -> InsightDashboardScreen(viewModel = viewModel)
      TuringScreen.RESEARCH_LAB -> ResearcherObservatoryScreen(viewModel = viewModel)
      TuringScreen.ACCOUNT_MANAGEMENT -> AccountManagementScreen(
        userProfile = userProfile,
        userVectors = userVectors,
        apiKeyConfig = apiKeyConfig,
        datingPreferences = datingPreferences,
        onUpdateProfile = { viewModel.updateProfile(it) },
        onUpdateApiKeyConfig = { viewModel.updateApiKeyConfig(it) },
        onUpdateDatingPreferences = { viewModel.updateDatingPreferences(it) },
        onResetVectors = { viewModel.resetUserVectors() },
        onOpenResearchLab = { viewModel.navigateTo(TuringScreen.RESEARCH_LAB) },
        onNavigateBack = { viewModel.returnToSanctuary() },
        onLogout = { viewModel.logout() }
      )
    }
  }
}

