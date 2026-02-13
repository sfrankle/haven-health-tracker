package com.haven.app.data.repository

import com.haven.app.data.dao.EntryTypeDao
import com.haven.app.data.entity.EntryType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryTypeRepository @Inject constructor(
    private val entryTypeDao: EntryTypeDao
) {
    fun getEnabled(): Flow<List<EntryType>> = entryTypeDao.getEnabled()

    suspend fun getById(id: Long): EntryType? = entryTypeDao.getById(id)

    suspend fun getByName(name: String): EntryType? = entryTypeDao.getByName(name)
}
