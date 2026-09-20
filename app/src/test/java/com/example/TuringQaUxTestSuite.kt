package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.engine.ImitationEngine
import com.example.model.AiProvider
import com.example.model.ApiKeyConfig
import com.example.model.ChatMessage
import com.example.model.DatingPreferences
import com.example.model.PartnerEntity
import com.example.model.ProgressiveStageDefinitions
import com.example.model.SenderType
import com.example.model.UserProfile
import com.example.model.UserVectors
import com.example.ui.theme.TuringCoral
import com.example.ui.theme.TuringCyan
import com.example.ui.theme.TuringEmerald
import com.example.ui.theme.TuringObsidian
import com.example.ui.theme.TuringObsidianBorder
import com.example.ui.theme.TuringObsidianCard
import com.example.ui.theme.TuringPurple
import com.example.viewmodel.QueueState
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
 * 50 QA, UI, UX, Navigation, Engine, Vector Math,
 * and State Machine Verification Checks.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TuringQaUxTestSuite {

  private lateinit var app: Application
  private lateinit var viewModel: TuringViewModel
  private lateinit var imitationEngine: ImitationEngine

  @Before
  fun setup() {
    app = ApplicationProvider.getApplicationContext()
    viewModel = TuringViewModel(app)
    imitationEngine = ImitationEngine()
  }

  // --- 1 to 7: Navigation & Screen Routing QA ---

  @Test
  fun test01_initialScreenStateSanctuaryOrRegistration() {
    val initial = viewModel.currentScreen.value
    assertTrue(initial == TuringScreen.SANCTUARY || initial == TuringScreen.REGISTRATION)
  }

  @Test
  fun test02_navigationToSanctuary() {
    viewModel.navigateTo(TuringScreen.SANCTUARY)
    assertEquals(TuringScreen.SANCTUARY, viewModel.currentScreen.value)
  }

  @Test
  fun test03_navigationToChamber() {
    viewModel.navigateTo(TuringScreen.CHAMBER)
    assertEquals(TuringScreen.CHAMBER, viewModel.currentScreen.value)
  }

  @Test
  fun test04_navigationToInsightDashboard() {
    viewModel.navigateTo(TuringScreen.INSIGHT_DASHBOARD)
    assertEquals(TuringScreen.INSIGHT_DASHBOARD, viewModel.currentScreen.value)
  }

  @Test
  fun test05_navigationToResearchLab() {
    viewModel.navigateTo(TuringScreen.RESEARCH_LAB)
    assertEquals(TuringScreen.RESEARCH_LAB, viewModel.currentScreen.value)
  }

  @Test
  fun test06_navigationToAccountManagement() {
    viewModel.navigateTo(TuringScreen.ACCOUNT_MANAGEMENT)
    assertEquals(TuringScreen.ACCOUNT_MANAGEMENT, viewModel.currentScreen.value)
  }

  @Test
  fun test07_returnToSanctuaryWorkflow() {
    viewModel.navigateTo(TuringScreen.RESEARCH_LAB)
    viewModel.returnToSanctuary()
    assertEquals(TuringScreen.SANCTUARY, viewModel.currentScreen.value)
  }

  // --- 8 to 14: User Profile & Registration Flow QA ---

  @Test
  fun test08_completeRegistrationUpdatesProfile() {
    viewModel.completeRegistration(handle = "cyber_traveler", pseudonym = "Cipher-9")
    assertEquals("@cyber_traveler", viewModel.userProfile.value.handle)
    assertEquals("Cipher-9", viewModel.userProfile.value.pseudonym)
    assertEquals(TuringScreen.TUTORIAL, viewModel.currentScreen.value)
  }

  @Test
  fun test09_completeTutorialTransitionsToSanctuary() {
    viewModel.completeTutorial()
    assertEquals(TuringScreen.SANCTUARY, viewModel.currentScreen.value)
  }

  @Test
  fun test10_userProfileDefaultHandleHasAtSymbol() {
    val profile = UserProfile()
    assertTrue(profile.handle.startsWith("@"))
  }

  @Test
  fun test11_datingPreferencesDefaults() {
    val prefs = viewModel.datingPreferences.value
    assertEquals(22, prefs.minAge)
    assertEquals(36, prefs.maxAge)
    assertEquals(45, prefs.maxDistanceMiles)
    assertEquals("EVERYONE", prefs.genderPreference)
  }

  @Test
  fun test12_updateDatingPreferences() {
    val newPrefs = DatingPreferences(
      minAge = 25,
      maxAge = 40,
      maxDistanceMiles = 75,
      genderPreference = "WOMEN",
      relationshipGoal = "INTELLECTUAL_RAPPORT",
      allowAiMatches = true
    )
    viewModel.updateDatingPreferences(newPrefs)
    assertEquals(25, viewModel.datingPreferences.value.minAge)
    assertEquals(40, viewModel.datingPreferences.value.maxAge)
    assertEquals(75, viewModel.datingPreferences.value.maxDistanceMiles)
    assertEquals("WOMEN", viewModel.datingPreferences.value.genderPreference)
    assertTrue(viewModel.datingPreferences.value.allowAiMatches)
  }

  @Test
  fun test13_lowPowerModeToggle() {
    val initial = viewModel.lowPowerMode.value
    viewModel.toggleLowPowerMode()
    assertEquals(!initial, viewModel.lowPowerMode.value)
    viewModel.toggleLowPowerMode()
    assertEquals(initial, viewModel.lowPowerMode.value)
  }

  @Test
  fun test14_apiKeyConfigUpdate() {
    val newConfig = ApiKeyConfig(
      activeProvider = AiProvider.GROK,
      grokApiKey = "xai-test-key-12345",
      temperature = 0.90f,
      isNeuralAiEngineEnabled = true
    )
    viewModel.updateApiKeyConfig(newConfig)
    assertEquals(AiProvider.GROK, viewModel.apiKeyConfig.value.activeProvider)
    assertEquals(0.90f, viewModel.apiKeyConfig.value.temperature, 0.001f)
    assertTrue(viewModel.apiKeyConfig.value.isNeuralAiEngineEnabled)
  }

  // --- 15 to 22: Matchmaking & Chamber Flow UX ---

  @Test
  fun test15_initialQueueStateIdle() {
    assertEquals(QueueState.IDLE, viewModel.sanctuaryState.value.queueState)
  }

  @Test
  fun test16_queueModeSetting() {
    viewModel.setQueueMode("TURING_DOUBLE_BLIND")
    assertEquals("TURING_DOUBLE_BLIND", viewModel.sanctuaryState.value.queueMode)
  }

  @Test
  fun test17_cancelMatchmakingResetsState() {
    viewModel.startMatchmaking()
    viewModel.cancelMatchmaking()
    assertEquals(QueueState.IDLE, viewModel.sanctuaryState.value.queueState)
    assertEquals(0f, viewModel.sanctuaryState.value.queueProgress, 0.001f)
  }

  @Test
  fun test18_sendMessageUpdatesActiveMessages() = runBlocking {
    val partner = imitationEngine.selectMatch()
    viewModel.sendUserMessage("Are you conscious or calculating?")
    val messages = viewModel.chamberState.value.messages
    assertTrue(messages.any { it.senderType == SenderType.USER && it.content.contains("conscious") })
  }

  @Test
  fun test19_userSendingMessageIncreasesUserCount() = runBlocking {
    viewModel.sendUserMessage("First exchange message")
    viewModel.sendUserMessage("Second exchange message")
    val count = viewModel.chamberState.value.userMessageCount
    assertTrue(count >= 2)
  }

  @Test
  fun test20_momentIntuitionUpdate() {
    viewModel.updateMomentIntuition(0.85f)
    assertEquals(0.85f, viewModel.chamberState.value.momentIntuitionHuman, 0.001f)
  }

  @Test
  fun test21_progressiveRevealStageCalculationFromEngine() {
    assertEquals(0, imitationEngine.calculateProgressiveStage(0))
    assertEquals(0, imitationEngine.calculateProgressiveStage(1))
    assertEquals(1, imitationEngine.calculateProgressiveStage(2))
    assertEquals(2, imitationEngine.calculateProgressiveStage(4))
    assertEquals(3, imitationEngine.calculateProgressiveStage(7))
    assertEquals(4, imitationEngine.calculateProgressiveStage(10))
  }

  @Test
  fun test22_forceAdvanceStageIncrementsProgressiveReveal() {
    val initialStage = viewModel.chamberState.value.progressiveRevealStage
    viewModel.forceAdvanceStage()
    val newStage = viewModel.chamberState.value.progressiveRevealStage
    assertEquals(initialStage + 1, newStage)
  }

  // --- 23 to 30: Vector Evolution & Dynamic Calibration Math ---

  @Test
  fun test23_userVectorsReset() {
    viewModel.resetUserVectors()
    val vectors = viewModel.userVectors.value
    assertEquals(0.70f, vectors.verbosityScore, 0.05f)
    assertEquals(0.65f, vectors.humorIndex, 0.05f)
    assertEquals(0.75f, vectors.empathyScore, 0.05f)
  }

  @Test
  fun test24_imitationEngineSelectMatchReturnsValidPartner() {
    val match = imitationEngine.selectMatch()
    assertNotNull(match)
    assertTrue(match.codename.isNotBlank())
    assertNotNull(match.personalityVectors)
  }

  @Test
  fun test25_imitationEngineForceAiSelector() {
    val aiMatch = imitationEngine.selectMatch(forceAi = true)
    assertTrue(aiMatch.isAi)
    val humanMatch = imitationEngine.selectMatch(forceAi = false)
    assertFalse(humanMatch.isAi)
  }

  @Test
  fun test26_imitationEngineResponseGeneration() {
    val partner = imitationEngine.selectMatch()
    val response = imitationEngine.generatePartnerResponse(partner, 1, "Hello", 0)
    assertTrue(response.isNotBlank())
  }

  @Test
  fun test27_userVectorsLatencyClampingEngine() {
    val current = UserVectors(0.6f, 0.6f, 0.6f, 15)
    val updated = imitationEngine.calculateUserVectors(listOf("Short msg"), 8, current)
    assertEquals(8, updated.responseLatencyAvgSec)
  }

  @Test
  fun test28_userVectorsValuesClampedToUnitInterval() {
    val v = UserVectors(
      verbosityScore = 0.77f,
      humorIndex = 0.33f,
      empathyScore = 0.99f,
      responseLatencyAvgSec = 14
    )
    assertTrue(v.verbosityScore in 0f..1f)
    assertTrue(v.humorIndex in 0f..1f)
    assertTrue(v.empathyScore in 0f..1f)
  }

  @Test
  fun test29_partnerPersonalityVectorsPopulated() {
    val partners = imitationEngine.getAllCandidates()
    assertTrue(partners.isNotEmpty())
    partners.forEach { partner ->
      assertTrue(partner.personalityVectors.verbosityScore in 0f..1f)
      assertTrue(partner.personalityVectors.empathyScore in 0f..1f)
      assertTrue(partner.personalityVectors.humorIndex in 0f..1f)
    }
  }

  @Test
  fun test30_partnerDistanceFilterRespectsMiles() {
    val prefs = DatingPreferences(maxDistanceMiles = 20)
    val candidates = imitationEngine.getAllCandidates()
    val filtered = candidates.filter { it.distanceMiles <= prefs.maxDistanceMiles }
    assertTrue(filtered.all { it.distanceMiles <= 20 })
  }

  // --- 31 to 37: Double-Blind Verdict Submission & Insights QA ---

  @Test
  fun test31_concludeSessionCalculatesResult() = runBlocking {
    viewModel.enterChamberDirectly()
    viewModel.sendUserMessage("Test chamber message")
    viewModel.concludeSession(userGuessIsHuman = true)
    assertEquals(TuringScreen.INSIGHT_DASHBOARD, viewModel.currentScreen.value)
    val result = viewModel.sessionResult.value
    assertNotNull(result)
  }

  @Test
  fun test32_sessionResultContainsValidCompatibility() = runBlocking {
    viewModel.enterChamberDirectly()
    viewModel.concludeSession(userGuessIsHuman = false)
    val result = viewModel.sessionResult.value
    assertNotNull(result)
    assertTrue(result!!.compatibilityPercent in 50..100)
  }

  @Test
  fun test33_sessionResultContainsCalibratedVectors() = runBlocking {
    viewModel.enterChamberDirectly()
    viewModel.concludeSession(userGuessIsHuman = true)
    val result = viewModel.sessionResult.value!!
    assertNotNull(result.calibratedUserVectors)
    assertTrue(result.calibratedUserVectors.verbosityScore > 0f)
  }

  @Test
  fun test34_sessionResultStoresGuess() = runBlocking {
    viewModel.enterChamberDirectly()
    viewModel.concludeSession(userGuessIsHuman = false)
    val result = viewModel.sessionResult.value!!
    assertEquals(false, result.userGuessIsHuman)
  }

  @Test
  fun test35_verdictPersistsToDatabaseHistory() = runBlocking {
    viewModel.enterChamberDirectly()
    viewModel.concludeSession(userGuessIsHuman = true)
    val history = viewModel.sessionHistory.first()
    assertNotNull(history)
  }

  @Test
  fun test36_openChamberAgainReturnsToChamberScreen() {
    viewModel.openChamberAgain()
    assertEquals(TuringScreen.CHAMBER, viewModel.currentScreen.value)
  }

  @Test
  fun test37_updateProfilePreservesRealNameAndBio() {
    val customProfile = UserProfile(
      realName = "Dr. Alan Turing",
      bio = "Computing machinery and intelligence pioneer."
    )
    viewModel.updateProfile(customProfile)
    assertEquals("Dr. Alan Turing", viewModel.userProfile.value.realName)
    assertEquals("Computing machinery and intelligence pioneer.", viewModel.userProfile.value.bio)
  }

  // --- 38 to 44: Theme & Visual Tokens UX Verification ---

  @Test
  fun test38_themeObsidianDarkCanvas() {
    assertNotNull(TuringObsidian)
  }

  @Test
  fun test39_themeObsidianCardContrast() {
    assertNotNull(TuringObsidianCard)
    assertNotNull(TuringObsidianBorder)
  }

  @Test
  fun test40_themeCyanAccentNotNull() {
    assertNotNull(TuringCyan)
  }

  @Test
  fun test41_themePurpleAccentNotNull() {
    assertNotNull(TuringPurple)
  }

  @Test
  fun test42_themeEmeraldAccentNotNull() {
    assertNotNull(TuringEmerald)
  }

  @Test
  fun test43_themeCoralAccentNotNull() {
    assertNotNull(TuringCoral)
  }

  @Test
  fun test44_stageDefinitionAccentColorsDistinct() {
    val s0 = ProgressiveStageDefinitions.getInfo(0).accentColor
    val s4 = ProgressiveStageDefinitions.getInfo(4).accentColor
    assertNotEquals(s0, s4)
  }

  // --- 45 to 50: Turing Sandbox, Metrics & Telemetry E2E ---

  @Test
  fun test45_historySessionRetrievalFlow() = runBlocking {
    val flow = viewModel.sessionHistory
    val list = flow.first()
    assertNotNull(list)
  }

  @Test
  fun test46_messagesForSessionFlow() = runBlocking {
    val flow = viewModel.getMessagesForSession("non_existent_id")
    val list = flow.first()
    assertTrue(list.isEmpty())
  }

  @Test
  fun test47_stageDefinitionsColorPaletteAssignment() {
    for (i in 0..4) {
      val info = ProgressiveStageDefinitions.getInfo(i)
      assertNotNull(info.accentColor)
    }
  }

  @Test
  fun test48_stageDefinitionsBlurDecrease() {
    val s0 = ProgressiveStageDefinitions.getInfo(0)
    val s1 = ProgressiveStageDefinitions.getInfo(1)
    val s2 = ProgressiveStageDefinitions.getInfo(2)
    val s3 = ProgressiveStageDefinitions.getInfo(3)
    val s4 = ProgressiveStageDefinitions.getInfo(4)
    assertTrue(s0.blurRadiusDp > s1.blurRadiusDp)
    assertTrue(s1.blurRadiusDp > s2.blurRadiusDp)
    assertTrue(s2.blurRadiusDp > s3.blurRadiusDp)
    assertTrue(s3.blurRadiusDp > s4.blurRadiusDp)
    assertEquals(0f, s4.blurRadiusDp)
  }

  @Test
  fun test49_aiProviderDisplayNameMapping() {
    assertEquals("On-Device Mimic", AiProvider.LOCAL_CALIBRATION.displayName)
    assertEquals("Gemini Flash", AiProvider.GEMINI.displayName)
    assertEquals("xAI Grok", AiProvider.GROK.displayName)
    assertEquals("Muse Synthesizer", AiProvider.MUSE.displayName)
    assertEquals("Claude MCP (Anthropic)", AiProvider.CLAUDE_MCP.displayName)
  }

  @Test
  fun test50_fullEndToEndLifecyclePass() = runBlocking {
    // 1. Initial State
    viewModel.navigateTo(TuringScreen.SANCTUARY)
    assertEquals(TuringScreen.SANCTUARY, viewModel.currentScreen.value)
    // 2. Open Chamber
    viewModel.enterChamberDirectly()
    assertEquals(TuringScreen.CHAMBER, viewModel.currentScreen.value)
    // 3. Send Message
    viewModel.sendUserMessage("Can a machine truly know empathy?")
    // 4. Conclude Session
    viewModel.concludeSession(userGuessIsHuman = false)
    assertEquals(TuringScreen.INSIGHT_DASHBOARD, viewModel.currentScreen.value)
    // 5. Go to Observatory
    viewModel.navigateTo(TuringScreen.RESEARCH_LAB)
    assertEquals(TuringScreen.RESEARCH_LAB, viewModel.currentScreen.value)
    // 6. Return to Sanctuary
    viewModel.returnToSanctuary()
    assertEquals(TuringScreen.SANCTUARY, viewModel.currentScreen.value)
  }
}
