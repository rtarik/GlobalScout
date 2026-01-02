package io.github.globalscout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.globalscout.data.CountryRepository
import io.github.globalscout.ui.model.CountryUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CountryUiState {
    data object Loading : CountryUiState
    data class Success(val countries: List<CountryUi>) : CountryUiState
    data class Error(val message: String) : CountryUiState
}

class HomeViewModel(private val countryRepository: CountryRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<CountryUiState>(CountryUiState.Loading)
    val uiState: StateFlow<CountryUiState> = _uiState.asStateFlow()

    init {
        fetchCountries()
    }

    private fun fetchCountries() {
        viewModelScope.launch {
            _uiState.value = CountryUiState.Loading
            try {
                val countries = countryRepository.getAllCountries().map { domainCountry ->
                    CountryUi(
                        name = domainCountry.name,
                        capital = domainCountry.capital.joinToString(", "),
                        region = domainCountry.region,
                        flag = domainCountry.flag
                    )
                }
                _uiState.value = CountryUiState.Success(countries)
            } catch (e: Exception) {
                _uiState.value = CountryUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}