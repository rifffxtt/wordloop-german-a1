package com.wordloop.germana1.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wordloop.germana1.data.entity.StudyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: StudyLogEntity)

    @Update
    suspend fun update(log: StudyLogEntity)

    @Query("SELECT * FROM study_logs WHERE date = :date")
    suspend fun getLogByDate(date: String): StudyLogEntity?

    @Query("SELECT * FROM study_logs ORDER BY date DESC LIMIT 30")
    fun getRecentLogs(): Flow<List<StudyLogEntity>>

    @Query("SELECT COUNT(*) FROM study_logs WHERE goalCompleted = 1")
    suspend fun getTotalCompletedDays(): Int

    @Query("SELECT date FROM study_logs WHERE goalCompleted = 1 ORDER BY date DESC")
    suspend fun getCompletedDates(): List<String>
}