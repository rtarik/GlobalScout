package io.github.globalscout.ui

import io.github.globalscout.data.CountryRepository
import io.github.globalscout.domain.Country
import io.github.globalscout.domain.CountryDetail
import io.github.globalscout.ui.model.CountryDetailUi
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
class CountryDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeCountryDetailRepository
    private lateinit var viewModel: CountryDetailViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCountryDetailRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        viewModel = CountryDetailViewModel(repository)
        assertEquals(CountryDetailUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loadCountry success maps domain model to ui model correctly`() = runTest(testDispatcher) {
        // Given
        val domainDetail = CountryDetail(
            cca3 = "EGY",
            name = "Egypt",
            officialName = "Arab Republic of Egypt",
            nativeName = "Miṣr",
            capital = listOf("Cairo"),
            region = "Africa",
            subregion = "Northern Africa",
            population = 100000000L,
            flagUrl = "url",
            currencies = listOf("EGP"),
            languages = listOf("Arabic"),
            timezones = listOf("UTC+2")
        )
        repository.detail = domainDetail
        viewModel = CountryDetailViewModel(repository)

        // When
        viewModel.loadCountry("EGY")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is CountryDetailUiState.Success)
        val uiDetail = (state as CountryDetailUiState.Success).detail
        assertEquals("Egypt", uiDetail.name)
        assertEquals("100,000,000", uiDetail.population) // Check formatting
        assertEquals("Cairo", uiDetail.capital)
        assertEquals("Africa, Northern Africa", uiDetail.region) // Check concatenation
        assertEquals(listOf("UTC+2"), uiDetail.timezones)
        assertEquals(listOf("Arabic"), uiDetail.languages)
    }

    @Test
    fun `loadCountry returns error when country not found`() = runTest(testDispatcher) {
        // Given
        repository.detail = null // Not found
        viewModel = CountryDetailViewModel(repository)

        // When
        viewModel.loadCountry("XYZ")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is CountryDetailUiState.Error)
        assertEquals("Country not found", (state as CountryDetailUiState.Error).message)
    }

    @Test
    fun `loadCountry returns error on exception`() = runTest(testDispatcher) {
        // Given
        repository.shouldFail = true
        repository.errorMsg = "Network Error"
        viewModel = CountryDetailViewModel(repository)

        // When
        viewModel.loadCountry("EGY")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is CountryDetailUiState.Error)
        assertEquals("Network Error", (state as CountryDetailUiState.Error).message)
    }
}

class FakeCountryDetailRepository : CountryRepository {
    var detail: CountryDetail? = null
    var shouldFail: Boolean = false
    var errorMsg: String = "Error"

    override suspend fun getAllCountries(): List<Country> = emptyList()

    override suspend fun getCountry(cca3: String): CountryDetail? {
        if (shouldFail) throw Exception(errorMsg)
        return detail
    }
}
