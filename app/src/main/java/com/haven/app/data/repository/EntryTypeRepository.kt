package com.haven.app.data.repository

import com.haven.app.data.dao.EntryTypeDao
import com.haven.app.data.entity.EntryTypeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryTypeRepository @Inject constructor(
    private val entryTypeDao: EntryTypeDao
) {
    fun getEnabled(): Flow<List<EntryTypeEntity>> = entryTypeDao.getEnabled()

    suspend fun getById(id: Long): EntryTypeEntity? = entryTypeDao.getById(id)

    suspend fun getByName(name: String): EntryTypeEntity? = entryTypeDao.getByName(name)
}
