package com.haven.app.data.repository

import com.haven.app.data.dao.EntryDao
import com.haven.app.data.model.EntryWithDetails
import com.haven.app.data.model.LabelFrequency
import com.haven.app.data.entity.Entry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryRepository @Inject constructor(
    private val entryDao: EntryDao
) {
    suspend fun insert(entry: Entry): Long = entryDao.insert(entry)

    suspend fun insertWithLabels(entry: Entry, labelIds: List<Long>): Long =
        entryDao.insertWithLabels(entry, labelIds)

    fun getAllWithDetails(): Flow<List<EntryWithDetails>> = entryDao.getAllWithDetails()

    fun getByTypeWithDetails(entryTypeId: Long): Flow<List<EntryWithDetails>> =
        entryDao.getByTypeWithDetails(entryTypeId)

    suspend fun getAllWithDetailsPaged(limit: Int, offset: Int): List<EntryWithDetails> =
        entryDao.getAllWithDetailsPaged(limit, offset)

    suspend fun getByTypeWithDetailsPaged(entryTypeId: Long, limit: Int, offset: Int): List<EntryWithDetails> =
        entryDao.getByTypeWithDetailsPaged(entryTypeId, limit, offset)

    fun getDailyTotal(entryTypeId: Long, dayStart: String, dayEnd: String): Flow<Double> =
        entryDao.getDailyTotal(entryTypeId, dayStart, dayEnd)

    suspend fun getLabelFrequencyByTimeWindow(
        entryTypeId: Long,
        hourStart: Int,
        hourEnd: Int,
        limit: Int
    ): List<LabelFrequency> {
        return if (hourStart < hourEnd) {
            entryDao.getLabelFrequencyByTimeWindow(entryTypeId, hourStart, hourEnd, limit)
        } else {
            // Wraps midnight (e.g., 21-6): query both ranges and merge
            val evening = entryDao.getLabelFrequencyByTimeWindow(entryTypeId, hourStart, 24, limit)
            val morning = entryDao.getLabelFrequencyByTimeWindow(entryTypeId, 0, hourEnd, limit)
            (evening + morning)
                .groupBy { it.labelId }
                .map { (labelId, freqs) -> LabelFrequency(labelId, freqs.sumOf { it.count }) }
                .sortedByDescending { it.count }
                .take(limit)
        }
    }
}
