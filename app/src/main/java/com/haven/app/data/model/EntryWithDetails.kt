package com.haven.app.data.model

import com.haven.app.data.entity.EntryTypeIcon

data class EntryWithDetails(
    val id: Long,
    val entryTypeId: Long,
    val entryTypeName: String,
    val entryTypeIcon: EntryTypeIcon?,
    val sourceType: String,
    val timestamp: String,
    val createdAt: String,
    val numericValue: Double?,
    val notes: String?,
    val labelNames: String?
)
