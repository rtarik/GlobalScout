package io.github.globalscout.ui

import io.github.globalscout.data.CountryRepository
import io.github.globalscout.domain.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeCountryRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCountryRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        viewModel = HomeViewModel(repository)
        assertEquals(CountryUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `fetch countries success`() = runTest(testDispatcher) {
        // Given
        val countries = listOf(
            Country("Egypt", listOf("Cairo"), "Africa", "🇪🇬"),
            Country("France", listOf("Paris"), "Europe", "🇫🇷")
        )
        repository.countries = countries
        viewModel = HomeViewModel(repository)

        // Wait for coroutines to complete
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is CountryUiState.Success)
        assertEquals(2, state.countries.size)
        assertEquals("Egypt", state.countries[0].name)
    }

    @Test
    fun `search filters countries by name`() = runTest(testDispatcher) {
        // Given
       val countries = listOf(
            Country("Egypt", listOf("Cairo"), "Africa", "🇪🇬"),
            Country("France", listOf("Paris"), "Europe", "🇫🇷")
        )
        repository.countries = countries
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("Egypt")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is CountryUiState.Success)
        assertEquals(1, state.countries.size)
        assertEquals("Egypt", state.countries[0].name)
    }
    
    @Test
    fun `search filters countries by capital`() = runTest(testDispatcher) {
        // Given
       val countries = listOf(
            Country("Egypt", listOf("Cairo"), "Africa", "🇪🇬"),
            Country("France", listOf("Paris"), "Europe", "🇫🇷")
        )
        repository.countries = countries
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("Paris")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is CountryUiState.Success)
        assertEquals(1, state.countries.size)
        assertEquals("France", state.countries[0].name)
    }
    
    @Test
    fun `search filters countries by region`() = runTest(testDispatcher) {
        // Given
       val countries = listOf(
            Country("Egypt", listOf("Cairo"), "Africa", "🇪🇬"),
            Country("France", listOf("Paris"), "Europe", "🇫🇷")
        )
        repository.countries = countries
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("Africa")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is CountryUiState.Success)
        assertEquals(1, state.countries.size)
        assertEquals("Egypt", state.countries[0].name)
    }

    @Test
    fun `search with no results returns empty list`() = runTest(testDispatcher) {
        // Given
       val countries = listOf(
            Country("Egypt", listOf("Cairo"), "Africa", "🇪🇬"),
            Country("France", listOf("Paris"), "Europe", "🇫🇷")
        )
        repository.countries = countries
        viewModel = HomeViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("Japan")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is CountryUiState.Success)
        assertTrue(state.countries.isEmpty())
    }
}

class FakeCountryRepository : CountryRepository {
    var countries: List<Country> = emptyList()
    var shouldFail: Boolean = false
    var errorMsg: String = "Error"

    override suspend fun getAllCountries(): List<Country> {
        if (shouldFail) throw Exception(errorMsg)
        return countries
    }
}
