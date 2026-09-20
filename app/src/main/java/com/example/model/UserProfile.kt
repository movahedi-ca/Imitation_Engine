package com.example.model

data class UserProfile(
  val userId: String = "primary_user",
  val handle: String = "@observer_alpha",
  val pseudonym: String = "Observer-01",
  val realName: String = "Alex Vance",
  val bio: String = "Exploring cognitive interfaces, emergent AI behaviors, and authentic conversation.",
  val location: String = "San Francisco, CA",
  val isBiometricVerified: Boolean = true,
  val verificationTimestamp: Long = System.currentTimeMillis() - (86400000 * 3),
  val totalSessionsCompleted: Int = 14,
  val intuitionAccuracyPercent: Int = 78,
  val memberSince: String = "September 2026",
  val encryptionKeyFingerprint: String = "SHA256:4f8e...9a12:ed25519"
)
