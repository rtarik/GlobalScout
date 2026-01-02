package io.github.globalscout.domain

data class Country(
    val cca3: String,
    val name: String,
    val capital: List<String>,
    val region: String,
    val flag: String
)
