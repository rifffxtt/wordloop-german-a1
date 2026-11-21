package com.wordloop.germana1.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.lang.ref.WeakReference

class AdManager private constructor(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var lastInterstitialShown = 0L
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun initialize(context: Context) {
            MobileAds.initialize(context) { initializationStatus ->
                Log.d("AdManager", "MobileAds initialization: $initializationStatus")
            }
        }

        @Suppress("UNUSED_PARAMETER")
        fun getInstance(context: Context): AdManager {
            return AdManager(context.applicationContext)
        }
    }

    // Banner Ad - Returns AdRequest for Compose/AndroidView
    fun loadBannerAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    // Interstitial Ad
    fun preloadInterstitialAd() {
        if (interstitialAd != null) return

        InterstitialAd.load(
            context,
            AdConfig.INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            interstitialAd = null
                            preloadInterstitialAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            interstitialAd = null
                            preloadInterstitialAd()
                        }
                    }
                    Log.d("AdManager", "Interstitial ad loaded")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    Log.e("AdManager", "Interstitial ad failed to load: ${adError.message}")
                }
            }
        )
    }

    fun showInterstitialAd(activity: Activity, onDismissed: () -> Unit) {
        val now = System.currentTimeMillis()
        if (now - lastInterstitialShown < AdConfig.INTERSTITIAL_COOLDOWN_MS) {
            Log.d("AdManager", "Interstitial cooldown active, skipping")
            onDismissed()
            return
        }

        if (interstitialAd != null) {
            lastInterstitialShown = now
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    preloadInterstitialAd()
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    preloadInterstitialAd()
                    onDismissed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            Log.d("AdManager", "Interstitial not ready, preloading")
            preloadInterstitialAd()
            onDismissed()
        }
    }

    // Rewarded Ad
    fun preloadRewardedAd() {
        if (rewardedAd != null) return

        RewardedAd.load(
            context,
            AdConfig.REWARDED_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            rewardedAd = null
                            preloadRewardedAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            rewardedAd = null
                            preloadRewardedAd()
                        }
                    }
                    Log.d("AdManager", "Rewarded ad loaded")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                    Log.e("AdManager", "Rewarded ad failed to load: ${adError.message}")
                }
            }
        )
    }

    fun showRewardedAd(
        activity: Activity,
        onReward: () -> Unit,
        onDismissed: () -> Unit,
        onFailed: (String) -> Unit
    ) {
        if (rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    preloadRewardedAd()
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    preloadRewardedAd()
                    onDismissed()
                }
            }
            rewardedAd?.show(activity) { rewardItem ->
                Log.d("AdManager", "Reward earned: ${rewardItem.amount} ${rewardItem.type}")
                onReward()
            }
        } else {
            Log.d("AdManager", "Rewarded ad not ready")
            onFailed("Ad not ready")
            preloadRewardedAd()
        }
    }

    // Helper for suspending functions
    suspend fun showRewardedAdSuspend(
        activity: Activity
    ): Boolean = kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        showRewardedAd(
            activity,
            onReward = { cont.resume(true) },
            onDismissed = { cont.resume(false) },
            onFailed = { cont.resume(false) }
        )
    }

    suspend fun showInterstitialAdSuspend(activity: Activity): Boolean = kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        showInterstitialAd(activity) {
            cont.resume(true)
        }
    }
}