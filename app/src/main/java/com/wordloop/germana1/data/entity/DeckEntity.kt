package com.wordloop.germana1.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val isLocked: Boolean = false
)