package io.github.globalscout.ui.model

data class CountryDetailUi(
    val name: String,
    val officialName: String,
    val nativeName: String,
    val cca3: String,
    val capital: String,
    val region: String,
    val population: String,
    val flagUrl: String,
    val currencies: String,
    val languages: List<String>,
    val timezones: List<String>
)
