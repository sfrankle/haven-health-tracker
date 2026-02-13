package com.haven.app.ui.tend

import app.cash.turbine.test
import com.haven.app.data.entity.EntryType
import com.haven.app.data.repository.EntryTypeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class TendViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var entryTypeRepository: EntryTypeRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        entryTypeRepository = mock(EntryTypeRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `entryTypes emits enabled entry types from repository`() = runTest {
        val types = listOf(
            EntryType(id = 1, name = "Food", measurementTypeId = 2, icon = "food", sortOrder = 1),
            EntryType(id = 3, name = "Hydration", measurementTypeId = 1, icon = "hydration", sortOrder = 3),
        )
        `when`(entryTypeRepository.getEnabled()).thenReturn(flowOf(types))

        val viewModel = TendViewModel(entryTypeRepository)

        viewModel.entryTypes.test {
            assertEquals(types, awaitItem())
            awaitComplete()
        }
    }
}
