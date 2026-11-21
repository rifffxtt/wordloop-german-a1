package com.wordloop.germana1.ads

object AdConfig {
    // Test Ad Unit IDs - Replace with your real IDs before release
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    const val APP_ID = "ca-app-pub-3940256099942544~3347511713"

    // Frequency capping - max 1 interstitial per 3 minutes
    const val INTERSTITIAL_COOLDOWN_MS = 3 * 60 * 1000L
}