package io.github.globalscout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.globalscout.data.CountryRepository
import io.github.globalscout.ui.model.CountryUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface CountryUiState {
    data object Loading : CountryUiState
    data class Success(val countries: List<CountryUi>) : CountryUiState
    data class Error(val message: String) : CountryUiState
}

class HomeViewModel(private val countryRepository: CountryRepository) : ViewModel() {

    private val _allCountries = MutableStateFlow<List<CountryUi>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<CountryUiState>(CountryUiState.Loading)
    val uiState: StateFlow<CountryUiState> = _uiState.asStateFlow()

    init {
        fetchCountries()
        viewModelScope.launch {
            combine(_allCountries, _searchQuery) { countries, query ->
                if (query.isBlank()) {
                    countries
                } else {
                    countries.filter {
                        it.name.contains(query, ignoreCase = true) ||
                        it.capital.contains(query, ignoreCase = true) ||
                        it.region.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filteredCountries ->
                if (_allCountries.value.isNotEmpty() || filteredCountries.isNotEmpty()) {
                     // Only update success state if we have data or filters result in empty data (but data was loaded)
                     // A better way is to track loading/error separately.
                     // Simplification: if _uiState is not Error, update Success.
                     if (_uiState.value !is CountryUiState.Error) {
                         _uiState.value = CountryUiState.Success(filteredCountries)
                     }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun fetchCountries() {
        viewModelScope.launch {
            _uiState.value = CountryUiState.Loading
            try {
                val countries = countryRepository.getAllCountries().map { domainCountry ->
                    CountryUi(
                        cca3 = domainCountry.cca3,
                        name = domainCountry.name,
                        capital = domainCountry.capital.joinToString(", "),
                        region = domainCountry.region,
                        flag = domainCountry.flag
                    )
                }
                _allCountries.value = countries
                // _uiState will be updated by the combine flow above
            } catch (e: Exception) {
                _uiState.value = CountryUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}