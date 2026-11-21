package com.wordloop.germana1.di

import android.content.Context
import com.wordloop.germana1.ads.AdManager
import com.wordloop.germana1.tts.TtsManager
import com.wordloop.germana1.util.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    @Singleton
    fun provideTtsManager(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): TtsManager {
        return TtsManager(context)
    }

    @Provides
    @Singleton
    fun provideAdManager(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): AdManager {
        return AdManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }
}