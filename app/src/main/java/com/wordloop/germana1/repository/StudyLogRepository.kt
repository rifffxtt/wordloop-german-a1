package com.wordloop.germana1.repository

import com.wordloop.germana1.data.dao.StudyLogDao
import com.wordloop.germana1.data.entity.StudyLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyLogRepository @Inject constructor(
    private val studyLogDao: StudyLogDao
) {
    suspend fun insert(log: StudyLogEntity) {
        studyLogDao.insert(log)
    }

    suspend fun update(log: StudyLogEntity) {
        studyLogDao.update(log)
    }

    suspend fun getLogByDate(date: String): StudyLogEntity? {
        return studyLogDao.getLogByDate(date)
    }

    fun getRecentLogs(): Flow<List<StudyLogEntity>> {
        return studyLogDao.getRecentLogs()
    }

    suspend fun getTotalCompletedDays(): Int {
        return studyLogDao.getTotalCompletedDays()
    }

    suspend fun getCompletedDates(): List<String> {
        return studyLogDao.getCompletedDates()
    }
}