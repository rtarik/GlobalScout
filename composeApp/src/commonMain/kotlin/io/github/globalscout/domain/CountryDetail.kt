package io.github.globalscout.domain

data class CountryDetail(
    val name: String,
    val officialName: String,
    val nativeName: String,
    val cca3: String,
    val capital: List<String>,
    val region: String,
    val subregion: String,
    val population: Long,
    val flagUrl: String,
    val languages: List<String>,
    val currencies: List<String>,
    val timezones: List<String>
)
