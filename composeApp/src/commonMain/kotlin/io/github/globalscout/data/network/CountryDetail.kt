package io.github.globalscout.data.network

import kotlinx.serialization.Serializable

@Serializable
data class CountryDetail(
    val name: CountryName,
    val cca3: String,
    val capital: List<String> = emptyList(),
    val region: String,
    val subregion: String? = null,
    val population: Long,
    val flags: Flags,
    val timezones: List<String>,
    val currencies: Map<String, Currency>? = null,
    val languages: Map<String, String>? = null
)



@Serializable
data class Flags(
    val png: String,
    val svg: String,
    val alt: String? = null
)

@Serializable
data class Currency(
    val name: String,
    val symbol: String? = null
)
