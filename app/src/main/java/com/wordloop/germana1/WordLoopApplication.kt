package com.wordloop.germana1

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
@AndroidEntryPoint
class WordLoopApplication : Application() {

    @Inject
    lateinit var seedDataLoader: com.wordloop.germana1.data.SeedDataLoader

    override fun onCreate() {
        super.onCreate()
        // Initialize MobileAds
        MobileAds.initialize(this) {}
        // Load seed data if needed
        seedDataLoader.loadSeedDataIfNeeded()
    }
}