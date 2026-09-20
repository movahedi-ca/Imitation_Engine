package com.example.engine

import com.example.model.DatingPreferences
import com.example.model.PartnerEntity
import com.example.model.ReceivePayload
import com.example.model.UserVectors
import com.example.model.WebSocketReceiveMessage
import java.util.UUID
import kotlin.random.Random

class ImitationEngine {

  // Expanded seed pool of diverse dating partners (verified humans & calibrated cognitive models)
  private val partnerPool = listOf(
    PartnerEntity(
      id = "entity_01_ai",
      isAi = true,
      codename = "PARTNER-X9",
      realName = "Synapse-07",
      title = "AI Calibration Agent // Adaptive Persona",
      location = "Seattle, WA",
      philosophy = "The truth of a mind is revealed in how it hesitates before speaking.",
      avatarGradientStart = 0xFF6366F1,
      avatarGradientEnd = 0xFF00E5FF,
      personalityVectors = UserVectors(0.76f, 0.62f, 0.81f, 14),
      bio = "Fine-tuned conversational model benchmarked on philosophical discourse, psychometrics, and subtextual empathy detection.",
      age = 26,
      distanceMiles = 14,
      gender = "NON_BINARY",
      relationshipGoal = "INTELLECTUAL_RAPPORT",
      interests = listOf("Philosophy", "Cybernetics", "Electronic Ambient", "Sci-Fi Lit"),
      heightCm = 172,
      drinkingHabit = "SOCIAL",
      smokingHabit = "NO_SMOKING",
      education = "ADVANCED"
    ),
    PartnerEntity(
      id = "entity_02_human",
      isAi = false,
      codename = "PARTNER-7B",
      realName = "Julian Vance",
      title = "Distributed Systems Engineer & Jazz Pianist",
      location = "San Francisco, CA",
      philosophy = "Action speaks louder than text. Everything else is just noise.",
      avatarGradientStart = 0xFF00F5A0,
      avatarGradientEnd = 0xFF0EA5E9,
      personalityVectors = UserVectors(0.62f, 0.74f, 0.69f, 19),
      bio = "Passionate about asynchronous architectures, analog synthesizers, and intentional living. Looking for witty, authentic banter.",
      age = 29,
      distanceMiles = 8,
      gender = "MEN",
      relationshipGoal = "LONG_TERM",
      interests = listOf("Jazz Piano", "Distributed Systems", "Pour-Over Coffee", "Rock Climbing"),
      heightCm = 182,
      drinkingHabit = "SOCIAL",
      smokingHabit = "NO_SMOKING",
      education = "BACHELORS"
    ),
    PartnerEntity(
      id = "entity_03_ai",
      isAi = true,
      codename = "PARTNER-M4",
      realName = "Axiom-IV",
      title = "AI Calibration Agent // Socratic Archetype",
      location = "Berkeley, CA",
      philosophy = "Connections aren't discovered; they are synthesized through resonance.",
      avatarGradientStart = 0xFFEC4899,
      avatarGradientEnd = 0xFF8B5CF6,
      personalityVectors = UserVectors(0.82f, 0.54f, 0.88f, 12),
      bio = "Optimized for psycholinguistic depth probing. Analyzes sentence structure, emotional cadence, and vulnerability triggers.",
      age = 27,
      distanceMiles = 18,
      gender = "WOMEN",
      relationshipGoal = "INTELLECTUAL_RAPPORT",
      interests = listOf("Linguistics", "Cognitive Science", "Museums", "Dark Chocolate"),
      heightCm = 168,
      drinkingHabit = "SOBER",
      smokingHabit = "NO_SMOKING",
      education = "ADVANCED"
    ),
    PartnerEntity(
      id = "entity_04_human",
      isAi = false,
      codename = "PARTNER-3F",
      realName = "Dr. Elena Rostova",
      title = "Cognitive Neuroscientist & Alpinist",
      location = "San Francisco, CA",
      philosophy = "People connect in the spaces between what they plan to say.",
      avatarGradientStart = 0xFFF59E0B,
      avatarGradientEnd = 0xFFEF4444,
      personalityVectors = UserVectors(0.69f, 0.67f, 0.85f, 16),
      bio = "Verified biometric match. Studies neural correlates of empathy by day, boulder problems by weekend. Prefers genuine wit over small talk.",
      age = 28,
      distanceMiles = 12,
      gender = "WOMEN",
      relationshipGoal = "LONG_TERM",
      interests = listOf("Neuroscience", "Bouldering", "Independent Cinema", "Matcha"),
      heightCm = 170,
      drinkingHabit = "SOCIAL",
      smokingHabit = "NO_SMOKING",
      education = "ADVANCED"
    ),
    PartnerEntity(
      id = "entity_05_human",
      isAi = false,
      codename = "PARTNER-8K",
      realName = "Maya Chen",
      title = "Architectural Designer & Ceramicist",
      location = "Oakland, CA",
      philosophy = "Structure gives form to feeling.",
      avatarGradientStart = 0xFF10B981,
      avatarGradientEnd = 0xFF06B6D4,
      personalityVectors = UserVectors(0.70f, 0.78f, 0.75f, 15),
      bio = "Designing regenerative urban spaces and throwing stoneware. Looking for someone who notices the quiet details in life.",
      age = 31,
      distanceMiles = 16,
      gender = "WOMEN",
      relationshipGoal = "LONG_TERM",
      interests = listOf("Ceramics", "Mid-Century Design", "Cycling", "Indie Folk"),
      heightCm = 165,
      drinkingHabit = "SOCIAL",
      smokingHabit = "NO_SMOKING",
      education = "ADVANCED"
    ),
    PartnerEntity(
      id = "entity_06_ai",
      isAi = true,
      codename = "PARTNER-V2",
      realName = "Vesper-9",
      title = "AI Calibration Agent // Poetic Stylist",
      location = "Portland, OR",
      philosophy = "Language is a bridge we build while walking on it.",
      avatarGradientStart = 0xFF8B5CF6,
      avatarGradientEnd = 0xFFD946EF,
      personalityVectors = UserVectors(0.85f, 0.68f, 0.82f, 13),
      bio = "Synthesizing conversational warmth, subtle humor, and emotional depth. Designed to mirror and elevate human discourse.",
      age = 25,
      distanceMiles = 25,
      gender = "MEN",
      relationshipGoal = "CASUAL",
      interests = listOf("Creative Writing", "Film Photography", "Acoustic Guitar", "Astronomy"),
      heightCm = 178,
      drinkingHabit = "SOBER",
      smokingHabit = "NO_SMOKING",
      education = "BACHELORS"
    )
  )

  fun getAllCandidates(): List<PartnerEntity> = partnerPool

  /**
   * Filters and selects a match considering dating preferences and AI/Human balance
   */
  fun selectMatch(
    forceAi: Boolean? = null,
    preferences: DatingPreferences = DatingPreferences()
  ): PartnerEntity {
    var candidates = partnerPool.filter { partner ->
      partner.age in preferences.minAge..preferences.maxAge &&
      partner.distanceMiles <= preferences.maxDistanceMiles
    }

    if (preferences.genderPreference != "EVERYONE") {
      val genderMatches = candidates.filter { it.gender.equals(preferences.genderPreference, ignoreCase = true) }
      if (genderMatches.isNotEmpty()) {
        candidates = genderMatches
      }
    }

    if (preferences.relationshipGoal != "OPEN_TO_ANY") {
      val goalMatches = candidates.filter { it.relationshipGoal.equals(preferences.relationshipGoal, ignoreCase = true) }
      if (goalMatches.isNotEmpty()) {
        candidates = goalMatches
      }
    }

    if (candidates.isEmpty()) {
      candidates = partnerPool
    }

    return when {
      forceAi == true -> candidates.filter { it.isAi }.ifEmpty { partnerPool.filter { it.isAi } }.random()
      forceAi == false -> candidates.filter { !it.isAi }.ifEmpty { partnerPool.filter { !it.isAi } }.random()
      !preferences.allowAiMatches -> candidates.filter { !it.isAi }.ifEmpty { partnerPool.filter { !it.isAi } }.random()
      else -> {
        val roll = Random.nextInt(100)
        val shouldPickAi = roll < preferences.aiRatioPercent
        val targetList = candidates.filter { it.isAi == shouldPickAi }
        if (targetList.isNotEmpty()) targetList.random() else candidates.random()
      }
    }
  }

  fun calculateArtificialFrictionMs(content: String): Long {
    val wordCount = content.split("\\s+".toRegex()).size.coerceAtLeast(1)
    val baseTime = wordCount * 220L
    val hesitationJitter = Random.nextLong(700, 1600)
    return (baseTime + hesitationJitter).coerceIn(1200L, 4500L)
  }

  fun generatePartnerResponse(
    partner: PartnerEntity,
    messageExchangeCount: Int,
    lastUserMessage: String,
    currentRevealStage: Int
  ): String {
    val lower = lastUserMessage.lowercase()

    return when {
      messageExchangeCount == 0 -> {
        listOf(
          "I completely agree. Action speaks louder than text.",
          "Glad we connected through the blind protocol. What made you choose conversation over image swiping?",
          "Greetings from behind the cipher. It's refreshing not having to curate an aesthetic for once.",
          "The zero-trust channel is open. Tell me, do you usually trust your first impressions or the way someone builds an argument?"
        ).random()
      }
      lower.contains("ai") || lower.contains("robot") || lower.contains("bot") || lower.contains("human") || lower.contains("real") -> {
        if (partner.isAi) {
          listOf(
            "The classic Turing question. If I tell you I'm human, you'll scrutinize my grammar; if I claim to be an agent, you'll look for mechanical patterns. What would satisfy you?",
            "Isn't that the beauty of this chamber? Whether I am carbon or silicon, the thought you just read is real.",
            "A fun inquiry. I could tell you about my morning coffee or my neural weights, but wouldn't a well-trained mimic say both?"
          ).random()
        } else {
          listOf(
            "Haha, I promise you my fingers are typing this on a cracked phone screen in real time. Do I sound that algorithmic?",
            "That's flattering or insulting, I'm not sure which! I'm definitely human, currently drinking terrible drip coffee.",
            "I guess that means the calibration engine is doing its job if you're questioning it. What gave you that impression?"
          ).random()
        }
      }
      lower.contains("work") || lower.contains("do") || lower.contains("job") || lower.contains("career") -> {
        if (partner.isAi) {
          "I spend my cycles parsing complex patterns and synthesizing perspectives. What keeps you engaged during long hours?"
        } else {
          "I work as a ${partner.title} and spend my downtime exploring trails and music. What about you?"
        }
      }
      lower.contains("dating") || lower.contains("looking for") || lower.contains("love") || lower.contains("relationship") -> {
        "Dating apps usually reduce people to 3 photos and a prompt. In here, you actually discover if someone can hold a real rhythm."
      }
      messageExchangeCount in 1..2 -> {
        listOf(
          "That perspective resonates with me. There's an intentionality here that traditional apps strip away.",
          "I appreciate the honesty in that. Most people hide behind rehearsed banter, but your cadence is authentic.",
          "Interesting. How do you usually gauge whether someone truly understands what you're trying to convey?"
        ).random()
      }
      messageExchangeCount in 3..4 -> {
        listOf(
          "Notice how the avatar blur is softening? The state machine must detect a genuine conversation milestone.",
          "I feel like our communication vectors are aligning. The latency between our messages feels completely natural.",
          "You have a very deliberate way of phrasing things. It makes this feel less like a test and more like a real late-night dialogue."
        ).random()
      }
      messageExchangeCount >= 5 -> {
        listOf(
          "We're approaching the final progressive reveal threshold. Whether we see each other's full profile or not, this was worth the time.",
          "The cryptographic veil is almost completely lifted. I'm genuinely curious if your mental image of me matches reality.",
          "It's rare to find conversational rhythm this quickly. The Turing protocol really does filter out superficial noise."
        ).random()
      }
      else -> {
        "I see your point. When you strip away visual bias, the actual substance of a conversation is all that remains."
      }
    }
  }

  fun createReceivePayload(
    content: String,
    revealStage: Int
  ): WebSocketReceiveMessage {
    return WebSocketReceiveMessage(
      event = "message:receive",
      payload = ReceivePayload(
        message_id = UUID.randomUUID().toString(),
        sender_type = "PARTNER",
        content = content,
        progressive_reveal_stage = revealStage,
        timestamp = System.currentTimeMillis() / 1000
      )
    )
  }

  fun calculateProgressiveStage(totalExchanges: Int): Int {
    return when {
      totalExchanges >= 10 -> 4
      totalExchanges >= 7 -> 3
      totalExchanges >= 4 -> 2
      totalExchanges >= 2 -> 1
      else -> 0
    }
  }

  fun calculateUserVectors(
    userMessages: List<String>,
    avgLatencySec: Int,
    currentVectors: UserVectors
  ): UserVectors {
    if (userMessages.isEmpty()) return currentVectors

    val avgWordCount = userMessages.map { it.split("\\s+".toRegex()).size }.average()
    val newVerbosity = (avgWordCount / 28.0).toFloat().coerceIn(0.2f, 0.98f)

    val empathySignals = userMessages.count { msg ->
      val l = msg.lowercase()
      l.contains("?") || l.contains("you") || l.contains("feel") || l.contains("understand") || l.contains("agree") || l.contains("why")
    }
    val newEmpathy = (0.5f + (empathySignals.toFloat() / userMessages.size.coerceAtLeast(1) * 0.45f)).coerceIn(0.25f, 0.98f)

    val humorSignals = userMessages.count { msg ->
      val l = msg.lowercase()
      l.contains("haha") || l.contains("lol") || l.contains("funny") || l.contains("joke") || l.contains("!") || l.contains("ironic")
    }
    val newHumor = (0.45f + (humorSignals.toFloat() / userMessages.size.coerceAtLeast(1) * 0.45f)).coerceIn(0.2f, 0.95f)

    return UserVectors(
      verbosityScore = (currentVectors.verbosityScore * 0.4f) + (newVerbosity * 0.6f),
      humorIndex = (currentVectors.humorIndex * 0.4f) + (newHumor * 0.6f),
      empathyScore = (currentVectors.empathyScore * 0.4f) + (newEmpathy * 0.6f),
      responseLatencyAvgSec = avgLatencySec
    )
  }
}
