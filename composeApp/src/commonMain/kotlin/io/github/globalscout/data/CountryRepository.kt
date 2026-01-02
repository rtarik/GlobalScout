package io.github.globalscout.data

import io.github.globalscout.domain.Country
import io.github.globalscout.domain.CountryDetail

interface CountryRepository {
    suspend fun getAllCountries(): List<Country>
    suspend fun getCountry(cca3: String): CountryDetail?
}