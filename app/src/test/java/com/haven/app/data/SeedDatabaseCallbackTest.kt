package com.haven.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.sqlite.db.SupportSQLiteDatabase
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SeedDatabaseCallbackTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var db: SupportSQLiteDatabase

    @Before
    fun setup() {
        context = mock()
        prefs = mock()
        editor = mock()
        db = mock()

        whenever(context.getSharedPreferences(any(), any())).thenReturn(prefs)
        whenever(prefs.edit()).thenReturn(editor)
        whenever(editor.putInt(any(), any())).thenReturn(editor)
    }

    @Test
    fun `onOpen seeds database when no version stored`() {
        whenever(prefs.getInt(any(), eq(0))).thenReturn(0)

        val callback = SeedDatabaseCallback(context)
        callback.onOpen(db)

        verify(db).beginTransaction()
        verify(db).setTransactionSuccessful()
        verify(db).endTransaction()
        verify(editor).putInt(any(), eq(SeedData.VERSION))
        verify(editor).apply()
    }

    @Test
    fun `onOpen skips seeding when version matches`() {
        whenever(prefs.getInt(any(), eq(0))).thenReturn(SeedData.VERSION)

        val callback = SeedDatabaseCallback(context)
        callback.onOpen(db)

        verify(db, never()).beginTransaction()
        verify(editor, never()).apply()
    }

    @Test
    fun `onOpen seeds when stored version is older`() {
        whenever(prefs.getInt(any(), eq(0))).thenReturn(SeedData.VERSION - 1)

        val callback = SeedDatabaseCallback(context)
        callback.onOpen(db)

        verify(db).beginTransaction()
        verify(db).setTransactionSuccessful()
        verify(db).endTransaction()
    }

    @Test
    fun `onOpen inserts all entry types`() {
        whenever(prefs.getInt(any(), eq(0))).thenReturn(0)

        val callback = SeedDatabaseCallback(context)
        callback.onOpen(db)

        // 6 entry types should be inserted
        verify(db, org.mockito.kotlin.times(SeedData.entryTypes.size))
            .insert(eq("entry_type"), any(), any())
    }

    // -------------------------------------------------------------------------
    // SeedData content tests
    // -------------------------------------------------------------------------

    @Test
    fun `entryTypes uses Physical State not Symptom`() {
        val names = SeedData.entryTypes.map { it.name }
        assertTrue("Physical State must be present", names.contains("Physical State"))
        assertFalse("Symptom must not be present", names.contains("Symptom"))
    }

    @Test
    fun `emotion labels are non-empty`() {
        val emotionEntryTypeId = 2L
        val allEmotionLabels = SeedData.emotionParentLabels + SeedData.emotionChildLabels
        assertTrue(
            "Emotion labels must be non-empty",
            allEmotionLabels.all { it.entryTypeId == emotionEntryTypeId }
        )
        assertTrue(allEmotionLabels.isNotEmpty())
    }

    @Test
    fun `physical state labels are non-empty`() {
        val physicalStateEntryTypeId = 5L
        assertTrue(SeedData.physicalStateLabels.isNotEmpty())
        assertTrue(SeedData.physicalStateLabels.all { it.entryTypeId == physicalStateEntryTypeId })
    }

    @Test
    fun `activity labels are non-empty`() {
        val activityEntryTypeId = 6L
        assertTrue(SeedData.activityLabels.isNotEmpty())
        assertTrue(SeedData.activityLabels.all { it.entryTypeId == activityEntryTypeId })
    }

    @Test
    fun `emotion children reference valid parent ids`() {
        val parentIds = SeedData.emotionParentLabels.map { it.id }.toSet()
        SeedData.emotionChildLabels.forEach { child ->
            assertTrue(
                "Child emotion '${child.name}' has invalid parentId ${child.parentId}",
                child.parentId in parentIds
            )
        }
    }

    @Test
    fun `physical state includes both difficult and positive states`() {
        val names = SeedData.physicalStateLabels.map { it.name }
        // Spot-check difficult states
        assertTrue(names.contains("Headache"))
        assertTrue(names.contains("Bloating"))
        // Spot-check positive/neutral states
        assertTrue(names.contains("Well-rested"))
        assertTrue(names.contains("Settled stomach"))
    }

    @Test
    fun `label tag mappings reference valid label and tag ids`() {
        val allLabels = SeedData.foodLabels + listOf(SeedData.mealSourceLabel) +
            SeedData.mealSourceChildren + SeedData.emotionParentLabels +
            SeedData.emotionChildLabels + SeedData.physicalStateLabels + SeedData.activityLabels
        val allTags = SeedData.foodTags + SeedData.emotionTags + SeedData.symptomTags + SeedData.activityTags
        val labelIds = allLabels.map { it.id }.toSet()
        val tagIds = allTags.map { it.id }.toSet()

        val allLabelTags = SeedData.foodLabelTags + SeedData.emotionLabelTags +
            SeedData.physicalStateLabelTags + SeedData.activityLabelTags

        allLabelTags.forEach { lt ->
            assertTrue("LabelTag references unknown labelId ${lt.labelId}", lt.labelId in labelIds)
            assertTrue("LabelTag references unknown tagId ${lt.tagId}", lt.tagId in tagIds)
        }
    }

    @Test
    fun `seed version is 3`() {
        assertEquals(3, SeedData.VERSION)
    }
}
