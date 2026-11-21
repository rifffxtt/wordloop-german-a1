package com.wordloop.germana1.data

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wordloop.germana1.data.entity.CardEntity
import com.wordloop.germana1.data.entity.DeckEntity
import com.wordloop.germana1.data.entity.QuizQuestionEntity
import com.wordloop.germana1.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedDataLoader @Inject constructor(
    private val context: Context,
    private val database: WordLoopDatabase,
    private val preferencesManager: PreferencesManager
) {

    private val gson = Gson()

    fun loadSeedDataIfNeeded() {
        if (preferencesManager.isSeedDataLoaded()) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val seedData = parseSeedData()
                insertAllData(seedData)
                preferencesManager.setSeedDataLoaded(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun parseSeedData(): SeedData {
        val inputStream = context.assets.open("seed_data.json")
        val reader = InputStreamReader(inputStream)
        val type: Type = object : TypeToken<SeedData>() {}.type
        return gson.fromJson(reader, type)
    }

    private fun insertAllData(seedData: SeedData) {
        database.runInTransaction {
            // Insert decks
            database.deckDao().insertAll(seedData.decks)

            // Insert cards
            database.cardDao().insertAll(seedData.cards)

            // Insert quiz questions
            database.quizQuestionDao().insertAll(seedData.quizQuestions)
        }
    }

    data class SeedData(
        val decks: List<DeckEntity>,
        val cards: List<CardEntity>,
        val quizQuestions: List<QuizQuestionEntity>,
        val studyLogs: List<com.wordloop.germana1.data.entity.StudyLogEntity> = emptyList()
    )
}