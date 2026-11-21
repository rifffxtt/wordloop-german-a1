package com.wordloop.germana1.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["deckId"])]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val deckId: Int,
    val frontText: String,
    val article: String?,
    val pronunciationNote: String?,
    val meaningEn: String,
    val meaningBn: String,
    val exampleSentence: String?,
    val exampleTranslation: String?,
    val grammarTip: String?,
    var masteryLevel: Int = 0,
    var reviewCount: Int = 0,
    var nextReviewAt: Long = 0
)