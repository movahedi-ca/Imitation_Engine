package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.PartnerEntity
import com.example.model.ProgressiveStageDefinitions
import com.example.model.SenderType
import com.example.model.UserVectors
import com.example.network.TuringWebSocketManager
import com.example.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TuringIntegrationTest {

  @Test
  fun testProgressiveStageDefinitions() {
    val stage0 = ProgressiveStageDefinitions.getInfo(0)
    assertEquals(38f, stage0.blurRadiusDp)
    assertEquals(0, stage0.stage)

    val stage4 = ProgressiveStageDefinitions.getInfo(4)
    assertEquals(0f, stage4.blurRadiusDp)
    assertEquals(4, stage4.stage)
  }

  @Test
  fun testChatViewModelInitializationAndSession() = runBlocking {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val chatViewModel = ChatViewModel(app)

    val partner = PartnerEntity(
      id = "test_partner",
      isAi = true,
      codename = "PARTNER-ECHO",
      realName = "Echo System",
      title = "Cognitive Mirror",
      location = "San Francisco",
      philosophy = "To think is to create.",
      avatarGradientStart = 0xFF6366F1,
      avatarGradientEnd = 0xFF00E5FF,
      personalityVectors = UserVectors(),
      bio = "Testing entity"
    )

    chatViewModel.startSession(partner)
    val state = chatViewModel.chatUiState.value
    assertEquals("PARTNER-ECHO", state.partnerCodename)
    assertEquals(0, state.progressiveRevealStage)
    assertNotNull(state.sessionId)

    chatViewModel.sendMessage("Hello, zero-trust chamber.")
    val updatedState = chatViewModel.chatUiState.value
    assertTrue(updatedState.messages.any { it.senderType == SenderType.USER })
  }

  @Test
  fun testWebSocketManagerContract() = runBlocking {
    val wsManager = TuringWebSocketManager()
    assertNotNull(wsManager.connectionStatus)
    assertNotNull(wsManager.incomingMessages)
  }
}
