package io.github.globalscout.data

import io.github.globalscout.domain.Country

interface CountryRepository {
    suspend fun getAllCountries(): List<Country>
}