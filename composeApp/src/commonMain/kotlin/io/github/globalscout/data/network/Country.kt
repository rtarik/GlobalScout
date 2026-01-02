package io.github.globalscout.data.network

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val cca3: String,
    val name: CountryName,
    val capital: List<String>,
    val region: String,
    val flag: String
)

@Serializable
data class CountryName(
    val common: String,
    val official: String,
    val nativeName: Map<String, Translation>? = null
)

@Serializable
data class Translation(
    val common: String,
    val official: String
)