package com.wordloop.germana1.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference

class TtsManager(private val context: Context) {

    private val ttsRef = AtomicReference<TextToSpeech?>(null)
    private val initDeferred = CompletableDeferred<Boolean>()
    private val isInitialized = AtomicReference(false)
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        initialize()
    }

    private fun initialize() {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.GERMAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TtsManager", "German language not supported")
                    initDeferred.complete(false)
                } else {
                    isInitialized.set(true)
                    ttsRef.set(tts)
                    initDeferred.complete(true)
                    Log.d("TtsManager", "TTS initialized successfully")
                }
            } else {
                Log.e("TtsManager", "TTS initialization failed: $status")
                initDeferred.complete(false)
            }
        }
    }

    suspend fun waitForInit(): Boolean {
        if (isInitialized.get()) return true
        return initDeferred.await()
    }

    fun speak(text: String, pitch: Float = 1.0f, speechRate: Float = 1.0f) {
        val tts = ttsRef.get()
        if (tts != null && isInitialized.get()) {
            tts.pitch = pitch
            tts.speechRate = speechRate
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "speak_${System.currentTimeMillis()}")
        }
    }

    fun speakSlow(text: String, pitch: Float = 1.0f) {
        speak(text, pitch, 0.75f)
    }

    fun stop() {
        val tts = ttsRef.get()
        tts?.stop()
    }

    fun shutdown() {
        val tts = ttsRef.getAndSet(null)
        tts?.shutdown()
        isInitialized.set(false)
    }

    fun isReady(): Boolean {
        return isInitialized.get() && ttsRef.get() != null
    }
}