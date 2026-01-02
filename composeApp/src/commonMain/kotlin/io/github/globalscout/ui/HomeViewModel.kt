package io.github.globalscout.ui

import androidx.lifecycle.ViewModel
import io.github.globalscout.data.CountryRepository
import io.github.globalscout.data.network.Country

class HomeViewModel(val countryRepository: CountryRepository) : ViewModel() {

    suspend fun getAllCountries(): List<Country> {
        return countryRepository.getAllCountries()
    }
}