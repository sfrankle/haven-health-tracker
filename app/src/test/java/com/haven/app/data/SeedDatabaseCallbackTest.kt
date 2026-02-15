package com.haven.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.sqlite.db.SupportSQLiteDatabase
import org.junit.Before
import org.junit.Test
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
}
