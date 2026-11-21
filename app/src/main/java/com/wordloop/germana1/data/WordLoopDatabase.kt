package com.wordloop.germana1.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wordloop.germana1.data.dao.CardDao
import com.wordloop.germana1.data.dao.DeckDao
import com.wordloop.germana1.data.dao.QuizQuestionDao
import com.wordloop.germana1.data.dao.StudyLogDao
import com.wordloop.germana1.data.entity.CardEntity
import com.wordloop.germana1.data.entity.DeckEntity
import com.wordloop.germana1.data.entity.QuizQuestionEntity
import com.wordloop.germana1.data.entity.StudyLogEntity

@Database(
    entities = [
        DeckEntity::class,
        CardEntity::class,
        QuizQuestionEntity::class,
        StudyLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WordLoopDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
    abstract fun quizQuestionDao(): QuizQuestionDao
    abstract fun studyLogDao(): StudyLogDao

    companion object {
        @volatile private var INSTANCE: WordLoopDatabase? = null
        private const val DATABASE_NAME = "wordloop.db"

        fun getDatabase(context: Context): WordLoopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordLoopDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    // Add type converters if needed for complex types
}