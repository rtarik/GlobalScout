package io.github.globalscout.data

import io.github.globalscout.data.network.CountryService
import io.github.globalscout.domain.Country
import io.github.globalscout.data.network.Country as NetworkCountry

class DefaultCountryRepository(private val service: CountryService) : CountryRepository {
    override suspend fun getAllCountries(): List<Country> {
        return service.fetchAll().map { networkCountry ->
            Country(
                name = networkCountry.name.common,
                capital = networkCountry.capital,
                region = networkCountry.region,
                flag = networkCountry.flag
            )
        }
    }
}
