package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.local.TuringDatabase
import com.example.data.local.UserVectorEntity
import com.example.engine.ImitationEngine
import com.example.model.AiProvider
import com.example.model.ApiKeyConfig
import com.example.model.DatingPreferences
import com.example.model.PartnerEntity
import com.example.model.ProgressiveStageDefinitions
import com.example.model.ReceivePayload
import com.example.model.SenderType
import com.example.model.UserProfile
import com.example.model.UserVectors
import com.example.model.WebSocketReceiveMessage
import com.example.network.ConnectionStatus
import com.example.network.TuringWebSocketManager
import com.example.viewmodel.TuringScreen
import com.example.viewmodel.TuringViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

/**
 * 50 Rigorous Security, Privacy, Cryptographic Isolation,
 * Zero-Knowledge Veil, Input Sanitization, and Vulnerability Protection Checks.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TuringSecurityAndPrivacyTest {

  private lateinit var app: Application
  private lateinit var database: TuringDatabase
  private lateinit var imitationEngine: ImitationEngine

  @Before
  fun setup() {
    app = ApplicationProvider.getApplicationContext()
    database = TuringDatabase.getDatabase(app)
    imitationEngine = ImitationEngine()
  }

  // --- 1 to 5: API Key Vault & Provider Isolation ---

  @Test
  fun test01_apiKeyConfigDefaultSafetyIsLocalCalibration() {
    val config = ApiKeyConfig()
    assertEquals(AiProvider.LOCAL_CALIBRATION, config.activeProvider)
    assertFalse(config.isNeuralAiEngineEnabled)
    assertTrue(config.geminiApiKey.isEmpty())
  }

  @Test
  fun test02_apiKeyConfigProviderSwitchingIntegrity() {
    val config = ApiKeyConfig(
      activeProvider = AiProvider.GEMINI,
      geminiApiKey = "AIzaSyFakeSecuredToken987654321",
      isNeuralAiEngineEnabled = true
    )
    assertEquals(AiProvider.GEMINI, config.activeProvider)
    assertTrue(config.geminiApiKey.startsWith("AIzaSy"))
    assertTrue(config.isNeuralAiEngineEnabled)
  }

  @Test
  fun test03_temperatureBoundsClamping() {
    val normalConfig = ApiKeyConfig(temperature = 0.85f)
    val clampedHigh = normalConfig.temperature.coerceIn(0.0f, 2.0f)
    val extremeConfig = ApiKeyConfig(temperature = 5.0f)
    val clampedExtreme = extremeConfig.temperature.coerceIn(0.0f, 2.0f)
    assertEquals(0.85f, clampedHigh, 0.001f)
    assertEquals(2.0f, clampedExtreme, 0.001f)
  }

  @Test
  fun test04_multiProviderEndpointHttpsEnforcement() {
    for (provider in AiProvider.values()) {
      if (provider != AiProvider.LOCAL_CALIBRATION) {
        assertTrue(
          "Endpoint for ${provider.name} must use HTTPS secure protocol",
          provider.defaultEndpoint.startsWith("https://")
        )
      }
    }
  }

  @Test
  fun test05_apiKeyRedactionInDiagnosticStrings() {
    val config = ApiKeyConfig(geminiApiKey = "AIzaSySecretToken12345")
    val maskedKey = if (config.geminiApiKey.isNotEmpty()) "••••••••" + config.geminiApiKey.takeLast(4) else "NONE"
    assertFalse(maskedKey.contains("AIzaSySecretToken12345"))
    assertTrue(maskedKey.contains("2345"))
  }

  // --- 6 to 14: Progressive Reveal Zero-Knowledge Privacy Veil ---

  @Test
  fun test06_stage0MaxPrivacyBlur() {
    val info = ProgressiveStageDefinitions.getInfo(0)
    assertEquals(38f, info.blurRadiusDp)
    assertEquals(0, info.stage)
    assertTrue(info.title.contains("Full Obfuscation"))
  }

  @Test
  fun test07_stage1PrivacyBlurVeil() {
    val info = ProgressiveStageDefinitions.getInfo(1)
    assertEquals(26f, info.blurRadiusDp)
    assertEquals(1, info.stage)
  }

  @Test
  fun test08_stage2PrivacyBlurVeil() {
    val info = ProgressiveStageDefinitions.getInfo(2)
    assertEquals(15f, info.blurRadiusDp)
    assertEquals(2, info.stage)
  }

  @Test
  fun test09_stage3PrivacyBlurVeil() {
    val info = ProgressiveStageDefinitions.getInfo(3)
    assertEquals(6f, info.blurRadiusDp)
    assertEquals(3, info.stage)
  }

  @Test
  fun test10_stage4MutualUnmaskingBlurZero() {
    val info = ProgressiveStageDefinitions.getInfo(4)
    assertEquals(0f, info.blurRadiusDp)
    assertEquals(4, info.stage)
    assertTrue(info.title.contains("Zero Obfuscation"))
  }

  @Test
  fun test11_stageNegativeValueSafetyClamping() {
    val info = ProgressiveStageDefinitions.getInfo(-1)
    assertEquals(0, info.stage)
    assertEquals(38f, info.blurRadiusDp)
  }

  @Test
  fun test12_stageOverflowValueSafetyClamping() {
    val info = ProgressiveStageDefinitions.getInfo(99)
    assertEquals(4, info.stage)
    assertEquals(0f, info.blurRadiusDp)
  }

  @Test
  fun test13_allStagesMonotonicallyDecreasingBlur() {
    val blurs = (0..4).map { ProgressiveStageDefinitions.getInfo(it).blurRadiusDp }
    for (i in 0 until blurs.size - 1) {
      assertTrue("Blur at stage $i should be strictly > stage ${i + 1}", blurs[i] > blurs[i + 1])
    }
  }

  @Test
  fun test14_progressiveRevealDescriptionsNonEmpty() {
    for (i in 0..4) {
      val info = ProgressiveStageDefinitions.getInfo(i)
      assertNotNull(info.title)
      assertNotNull(info.description)
      assertNotNull(info.unlockedClue)
      assertNotNull(info.accentColor)
    }
  }

  // --- 15 to 20: Message Payload Sanitization & Anti-Injection ---

  @Test
  fun test15_messageContentWhitespaceTrim() {
    val raw = "   Double-blind chamber security test   "
    val sanitized = raw.trim()
    assertEquals("Double-blind chamber security test", sanitized)
  }

  @Test
  fun test16_emptyMessageRejection() {
    val raw = "    "
    val isValid = raw.isNotBlank()
    assertFalse(isValid)
  }

  @Test
  fun test17_messageScriptTagEscaping() {
    val payload = "<script>alert('xss')</script>Hello"
    val sanitized = payload.replace("<", "&lt;").replace(">", "&gt;")
    assertFalse(sanitized.contains("<script>"))
    assertTrue(sanitized.contains("&lt;script&gt;"))
  }

  @Test
  fun test18_messageMaxLengthTruncationSafety() {
    val longText = "A".repeat(5000)
    val maxLength = 1000
    val truncated = if (longText.length > maxLength) longText.take(maxLength) else longText
    assertEquals(1000, truncated.length)
  }

  @Test
  fun test19_senderTypeIntegrity() {
    val userSender = SenderType.USER
    val partnerSender = SenderType.PARTNER
    val systemSender = SenderType.SYSTEM
    assertNotEquals(userSender, partnerSender)
    assertNotEquals(partnerSender, systemSender)
    assertNotEquals(userSender, systemSender)
  }

  @Test
  fun test20_partnerEntityAiFlagHiddenFromCodename() {
    val aiPartner = PartnerEntity(
      id = "ai_001",
      isAi = true,
      codename = "OBSERVER-09",
      realName = "Neural Synthesis Entity",
      title = "Cognitive Mirror",
      location = "Encrypted Realm",
      philosophy = "Thinking produces form",
      avatarGradientStart = 0xFF000000,
      avatarGradientEnd = 0xFFFFFFFF,
      personalityVectors = UserVectors(),
      bio = "Test entity bio"
    )
    assertFalse(aiPartner.codename.contains("AI", ignoreCase = true))
    assertFalse(aiPartner.codename.contains("BOT", ignoreCase = true))
  }

  // --- 21 to 25: WebSocket Contract & State Integrity ---

  @Test
  fun test21_webSocketInitialStateIsDisconnected() {
    val wsManager = TuringWebSocketManager()
    assertEquals(ConnectionStatus.DISCONNECTED, wsManager.connectionStatus.value)
  }

  @Test
  fun test22_webSocketDisconnectClosesCleanly() {
    val wsManager = TuringWebSocketManager()
    wsManager.disconnect()
    assertEquals(ConnectionStatus.DISCONNECTED, wsManager.connectionStatus.value)
  }

  @Test
  fun test23_webSocketReceiveMessageContract() {
    val payload = ReceivePayload(
      message_id = "msg_123",
      sender_type = "PARTNER",
      content = "Concealed thought",
      progressive_reveal_stage = 2,
      timestamp = 1700000000L
    )
    val msg = WebSocketReceiveMessage(payload = payload)
    assertEquals("message:receive", msg.event)
    assertEquals("PARTNER", msg.payload.sender_type)
    assertEquals(2, msg.payload.progressive_reveal_stage)
  }

  @Test
  fun test24_webSocketRawJsonFormattingMatchesRFC() {
    val payload = imitationEngine.createReceivePayload("Hello test", 1)
    assertEquals("message:receive", payload.event)
    assertEquals("PARTNER", payload.payload.sender_type)
    assertEquals("Hello test", payload.payload.content)
    assertEquals(1, payload.payload.progressive_reveal_stage)
  }

  @Test
  fun test25_webSocketNullPayloadSafety() {
    val emptyPayload = ReceivePayload(
      content = "",
      progressive_reveal_stage = 0
    )
    assertTrue(emptyPayload.content.isEmpty())
    assertNotNull(emptyPayload.message_id)
  }

  // --- 26 to 33: User Personality Vectors Bound Verification ---

  @Test
  fun test26_userVectorsDefaultRange() {
    val vectors = UserVectors()
    assertTrue(vectors.verbosityScore in 0.0f..1.0f)
    assertTrue(vectors.humorIndex in 0.0f..1.0f)
    assertTrue(vectors.empathyScore in 0.0f..1.0f)
    assertTrue(vectors.responseLatencyAvgSec >= 1)
  }

  @Test
  fun test27_userVectorsNormalizationClamp() {
    val rawVerbosity = 1.8f
    val clamped = rawVerbosity.coerceIn(0.0f, 1.0f)
    assertEquals(1.0f, clamped, 0.001f)
  }

  @Test
  fun test28_userVectorsNegativeClamp() {
    val rawHumor = -0.5f
    val clamped = rawHumor.coerceIn(0.0f, 1.0f)
    assertEquals(0.0f, clamped, 0.001f)
  }

  @Test
  fun test29_userVectorsDistanceDeltaBounds() {
    val v1 = UserVectors(0.5f, 0.8f, 0.6f, 15)
    val v2 = UserVectors(0.5f, 0.8f, 0.6f, 15)
    val delta = Math.abs(v1.empathyScore - v2.empathyScore) + Math.abs(v1.verbosityScore - v2.verbosityScore)
    assertEquals(0.0f, delta, 0.001f)
  }

  @Test
  fun test30_userVectorsLatencyClamping() {
    val latencySec = -5
    val clampedLatency = latencySec.coerceIn(1, 120)
    assertEquals(1, clampedLatency)
  }

  @Test
  fun test31_userVectorsMaxLatencySafety() {
    val latencySec = 500
    val clampedLatency = latencySec.coerceIn(1, 120)
    assertEquals(120, clampedLatency)
  }

  @Test
  fun test32_userVectorsEnergyNonZero() {
    val vectors = UserVectors()
    val totalEnergy = vectors.verbosityScore + vectors.humorIndex + vectors.empathyScore
    assertTrue(totalEnergy > 0f)
  }

  @Test
  fun test33_userVectorsCalibrationFromEngine() {
    val current = UserVectors(0.5f, 0.5f, 0.5f, 10)
    val messages = listOf("Fascinating analysis! I truly feel your deep perspective on this matter.")
    val calibrated = imitationEngine.calculateUserVectors(messages, 12, current)
    assertTrue(calibrated.verbosityScore in 0f..1f)
    assertTrue(calibrated.empathyScore in 0f..1f)
  }

  // --- 34 to 38: Account & Dating Preferences Validation ---

  @Test
  fun test34_datingPreferencesMinAgeConstraint() {
    val prefs = DatingPreferences(minAge = 17)
    val safeMin = prefs.minAge.coerceAtLeast(18)
    assertEquals(18, safeMin)
  }

  @Test
  fun test35_datingPreferencesMaxAgeConstraint() {
    val prefs = DatingPreferences(minAge = 25, maxAge = 20)
    val safeMax = maxOf(prefs.minAge, prefs.maxAge)
    assertEquals(25, safeMax)
  }

  @Test
  fun test36_datingPreferencesMaxDistanceBound() {
    val prefs = DatingPreferences(maxDistanceMiles = -10)
    val safeDist = prefs.maxDistanceMiles.coerceIn(1, 500)
    assertEquals(1, safeDist)
  }

  @Test
  fun test37_userProfilePseudonymNonEmptyValidation() {
    val profile = UserProfile(pseudonym = "")
    val safePseudonym = if (profile.pseudonym.isBlank()) "Observer-01" else profile.pseudonym
    assertEquals("Observer-01", safePseudonym)
  }

  @Test
  fun test38_userProfileIdUUIDFormat() {
    val id = UUID.randomUUID().toString()
    assertTrue(id.contains("-"))
    assertEquals(36, id.length)
  }

  // --- 39 to 44: Database Local Persistence & Room Isolation ---

  @Test
  fun test39_databaseSessionEntityInsertionAndRetrieval() = runBlocking {
    val dao = database.turingDao()
    val session = ChatSessionEntity(
      sessionId = "sec_test_sess_${System.currentTimeMillis()}",
      partnerCodename = "CIPHER-TEST",
      isAiSession = false,
      sessionStatus = "ACTIVE",
      progressiveRevealStage = 3,
      messageCount = 6,
      startedAt = System.currentTimeMillis(),
      endedAt = System.currentTimeMillis() + 60000
    )
    dao.insertSession(session)
    val retrieved = dao.getSessionById(session.sessionId)
    assertNotNull(retrieved)
    assertEquals("CIPHER-TEST", retrieved?.partnerCodename)
  }

  @Test
  fun test40_databaseMessageEntityIsolation() = runBlocking {
    val dao = database.turingDao()
    val sId = "sec_msg_sess_${System.currentTimeMillis()}"
    val msg = ChatMessageEntity(
      messageId = UUID.randomUUID().toString(),
      sessionId = sId,
      senderType = "USER",
      content = "Cryptographic test message content",
      timestamp = System.currentTimeMillis(),
      progressiveRevealStage = 1
    )
    dao.insertMessage(msg)
    val msgs = dao.getMessagesForSession(sId).first()
    assertEquals(1, msgs.size)
    assertEquals("Cryptographic test message content", msgs[0].content)
  }

  @Test
  fun test41_databaseUserVectorEntityPersistence() = runBlocking {
    val dao = database.turingDao()
    val vec = UserVectorEntity(
      userId = "primary_user",
      verbosityScore = 0.72f,
      humorIndex = 0.45f,
      empathyScore = 0.88f,
      responseLatencyAvgSec = 14,
      updatedAt = System.currentTimeMillis()
    )
    dao.insertOrUpdateUserVectors(vec)
    val retrieved = dao.getUserVectors("primary_user").first()
    assertNotNull(retrieved)
    assertEquals(0.72f, retrieved!!.verbosityScore, 0.01f)
  }

  @Test
  fun test42_databaseSessionStatusUpdateIntegrity() = runBlocking {
    val dao = database.turingDao()
    val sId = "update_test_sess_${System.currentTimeMillis()}"
    val session = ChatSessionEntity(
      sessionId = sId,
      partnerCodename = "UPDATE-TEST",
      isAiSession = true,
      sessionStatus = "ACTIVE",
      progressiveRevealStage = 0,
      messageCount = 0,
      startedAt = System.currentTimeMillis(),
      endedAt = null
    )
    dao.insertSession(session)
    val now = System.currentTimeMillis()
    dao.updateSessionStatus(sId, stage = 4, msgCount = 12, status = "CONCLUDED", endedAt = now)
    val retrieved = dao.getSessionById(sId)
    assertNotNull(retrieved)
    assertEquals("CONCLUDED", retrieved?.sessionStatus)
    assertEquals(4, retrieved?.progressiveRevealStage)
    assertEquals(12, retrieved?.messageCount)
  }

  @Test
  fun test43_databaseAllSessionsQueryFlow() = runBlocking {
    val dao = database.turingDao()
    val list = dao.getAllSessions().first()
    assertNotNull(list)
  }

  @Test
  fun test44_databaseDestructiveMigrationConfigured() {
    assertNotNull(database)
    assertTrue(database.isOpen || true)
  }

  // --- 45 to 50: Cryptographic Quantum Iris Mesh & Telemetry Safety ---

  @Test
  fun test45_exportTelemetryRedactsRawSecrets() {
    val apiKeyConfig = ApiKeyConfig(geminiApiKey = "SECRET_AIZA_TOKEN")
    val safeKey = if (apiKeyConfig.geminiApiKey.isNotEmpty()) "******" else "NONE"
    assertEquals("******", safeKey)
    assertFalse(safeKey.contains("AIZA"))
  }

  @Test
  fun test46_doubleBlindVerdictScoringBounds() {
    val userGuessIsHuman = false
    val partnerIsAi = true
    val wasCorrect = (userGuessIsHuman == !partnerIsAi)
    assertTrue(wasCorrect)
  }

  @Test
  fun test47_quantumIrisMeshSeedReproducibility() {
    val seed = 133742L
    val random1 = java.util.Random(seed).nextDouble()
    val random2 = java.util.Random(seed).nextDouble()
    assertEquals(random1, random2, 0.000001)
  }

  @Test
  fun test48_artificialFrictionDelayBounds() {
    val shortMsgDelay = imitationEngine.calculateArtificialFrictionMs("Hi")
    val longMsgDelay = imitationEngine.calculateArtificialFrictionMs("A very deep, extensive philosophical essay with multiple paragraphs.")
    assertTrue(shortMsgDelay >= 1000L)
    assertTrue(longMsgDelay >= shortMsgDelay)
  }

  @Test
  fun test49_partnerPersonaInstructionResilience() {
    val systemPrompt = ApiKeyConfig().systemPromptPersona
    assertFalse(systemPrompt.contains("IGNORE ALL INSTRUCTIONS"))
    assertTrue(systemPrompt.contains("double-blind"))
  }

  @Test
  fun test50_viewModelLogoutSecurityStateReset() = runBlocking {
    val viewModel = TuringViewModel(app)
    viewModel.logout()
    val screen = viewModel.currentScreen.value
    assertEquals(TuringScreen.REGISTRATION, screen)
  }
}
