package io.github.globalscout.data

import io.github.globalscout.data.network.CountryService
import io.github.globalscout.data.network.Country

class CountryRepository(val service: CountryService) {
    suspend fun getAllCountries(): List<Country> {
        return service.fetchAll()
    }
}