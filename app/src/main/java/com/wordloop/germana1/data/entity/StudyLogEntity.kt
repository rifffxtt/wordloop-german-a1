package com.wordloop.germana1.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_logs")
data class StudyLogEntity(
    @PrimaryKey val date: String,
    val cardsReviewed: Int,
    val goalCompleted: Boolean
)