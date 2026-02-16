package com.haven.app.data.model

import com.haven.app.data.entity.EntryType

data class EntryWithDetails(
    val id: Long,
    val entryTypeId: Long,
    val entryType: EntryType?,
    val sourceType: String,
    val timestamp: String,
    val createdAt: String,
    val numericValue: Double?,
    val notes: String?,
    val labelNames: String?
)
