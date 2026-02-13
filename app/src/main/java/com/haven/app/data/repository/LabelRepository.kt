package com.haven.app.data.repository

import com.haven.app.data.dao.LabelDao
import com.haven.app.data.entity.Label
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LabelRepository @Inject constructor(
    private val labelDao: LabelDao
) {
    fun getByEntryType(entryTypeId: Long): Flow<List<Label>> = labelDao.getByEntryType(entryTypeId)

    fun getTopLevel(entryTypeId: Long): Flow<List<Label>> = labelDao.getTopLevel(entryTypeId)

    fun getChildren(parentId: Long): Flow<List<Label>> = labelDao.getChildren(parentId)
}
