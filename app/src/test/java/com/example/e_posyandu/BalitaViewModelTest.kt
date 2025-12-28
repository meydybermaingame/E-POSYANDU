package com.example.e_posyandu

import com.example.e_posyandu.data.model.Balita
import com.example.e_posyandu.ui.viewmodel.BalitaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BalitaViewModelTest {
    
    private lateinit var viewModel: BalitaViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BalitaViewModel()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `test search balita with empty query returns all balita`() = runTest {
        // Given
        val testBalita = listOf(
            Balita(nama = "Ahmad", usia = 3),
            Balita(nama = "Sari", usia = 2)
        )
        
        // When
        val result = viewModel.searchBalita("")
        
        // Then
        assertEquals(testBalita.size, result.size)
    }
    
    @Test
    fun `test search balita with valid query returns filtered results`() = runTest {
        // Given
        val testBalita = listOf(
            Balita(nama = "Ahmad Fadillah", usia = 3),
            Balita(nama = "Sari Putri", usia = 2)
        )
        
        // When
        val result = viewModel.searchBalita("Ahmad")
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Ahmad Fadillah", result[0].nama)
    }
    
    @Test
    fun `test search balita with non-existent query returns empty list`() = runTest {
        // Given
        val testBalita = listOf(
            Balita(nama = "Ahmad", usia = 3),
            Balita(nama = "Sari", usia = 2)
        )
        
        // When
        val result = viewModel.searchBalita("NonExistent")
        
        // Then
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `test search balita is case insensitive`() = runTest {
        // Given
        val testBalita = listOf(
            Balita(nama = "Ahmad Fadillah", usia = 3),
            Balita(nama = "Sari Putri", usia = 2)
        )
        
        // When
        val result = viewModel.searchBalita("ahmad")
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Ahmad Fadillah", result[0].nama)
    }
    
    @Test
    fun `test search balita with partial name match`() = runTest {
        // Given
        val testBalita = listOf(
            Balita(nama = "Ahmad Fadillah", usia = 3),
            Balita(nama = "Sari Putri", usia = 2)
        )
        
        // When
        val result = viewModel.searchBalita("Fadillah")
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Ahmad Fadillah", result[0].nama)
    }
} 