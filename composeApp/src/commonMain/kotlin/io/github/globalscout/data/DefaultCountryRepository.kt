package io.github.globalscout.data

import io.github.globalscout.data.network.CountryService
import io.github.globalscout.domain.Country
import io.github.globalscout.domain.CountryDetail
import io.github.globalscout.data.network.Country as NetworkCountry
import io.github.globalscout.data.network.CountryDetail as NetworkCountryDetail

class DefaultCountryRepository(private val service: CountryService) : CountryRepository {
    override suspend fun getAllCountries(): List<Country> {
        return service.fetchAll().map { networkCountry ->
            Country(
                cca3 = networkCountry.cca3,
                name = networkCountry.name.common,
                capital = networkCountry.capital,
                region = networkCountry.region,
                flag = networkCountry.flag
            )
        }
    }

    override suspend fun getCountry(cca3: String): CountryDetail? {
        val networkDetail = service.getCountry(cca3).firstOrNull() ?: return null
        return CountryDetail(
            name = networkDetail.name.common,
            officialName = networkDetail.name.official,
            nativeName = networkDetail.name.nativeName?.values?.firstOrNull()?.common ?: networkDetail.name.common,
            cca3 = networkDetail.cca3,
            capital = networkDetail.capital,
            region = networkDetail.region,
            subregion = networkDetail.subregion ?: "",
            population = networkDetail.population,
            flagUrl = networkDetail.flags.png,
            languages = networkDetail.languages?.values?.toList() ?: emptyList(),
            currencies = networkDetail.currencies?.values?.map { "${it.name} (${it.symbol ?: ""})" } ?: emptyList(),
            timezones = networkDetail.timezones
        )
    }
}
