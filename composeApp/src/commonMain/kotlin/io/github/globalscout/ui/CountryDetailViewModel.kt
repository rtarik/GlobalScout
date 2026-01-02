package io.github.globalscout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.globalscout.data.CountryRepository
import io.github.globalscout.ui.model.CountryDetailUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CountryDetailUiState {
    data object Loading : CountryDetailUiState
    data class Success(val detail: CountryDetailUi) : CountryDetailUiState
    data class Error(val message: String) : CountryDetailUiState
}

class CountryDetailViewModel(
    private val repository: CountryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CountryDetailUiState>(CountryDetailUiState.Loading)
    val uiState: StateFlow<CountryDetailUiState> = _uiState.asStateFlow()

    fun loadCountry(cca3: String) {
        viewModelScope.launch {
            _uiState.value = CountryDetailUiState.Loading
            try {
                val country = repository.getCountry(cca3)
                if (country != null) {
                    val uiModel = CountryDetailUi(
                        name = country.name,
                        officialName = country.officialName,
                        nativeName = country.nativeName,
                        cca3 = country.cca3,
                        capital = country.capital.joinToString(", "),
                        region = "${country.region}, ${country.subregion}",
                        population = formatPopulation(country.population),
                        flagUrl = country.flagUrl,
                        currencies = country.currencies.joinToString(", "),
                        languages = country.languages,
                        timezones = country.timezones
                    )
                    _uiState.value = CountryDetailUiState.Success(uiModel)
                } else {
                    _uiState.value = CountryDetailUiState.Error("Country not found")
                }
            } catch (e: Exception) {
                _uiState.value = CountryDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun formatPopulation(population: Long): String {
        return population.toString().reversed().chunked(3).joinToString(",").reversed()
    }
}
