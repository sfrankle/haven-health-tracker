package com.haven.app.data.repository

import com.haven.app.data.dao.EntryDao
import com.haven.app.data.model.EntryWithDetails
import com.haven.app.data.entity.Entry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryRepository @Inject constructor(
    private val entryDao: EntryDao
) {
    suspend fun insertWithLabels(entry: Entry, labelIds: List<Long>): Long =
        entryDao.insertWithLabels(entry, labelIds)

    fun getAllWithDetails(): Flow<List<EntryWithDetails>> = entryDao.getAllWithDetails()

    fun getByTypeWithDetails(entryTypeId: Long): Flow<List<EntryWithDetails>> =
        entryDao.getByTypeWithDetails(entryTypeId)

    fun getDailyTotal(entryTypeId: Long, dayStart: String, dayEnd: String): Flow<Double> =
        entryDao.getDailyTotal(entryTypeId, dayStart, dayEnd)
}
