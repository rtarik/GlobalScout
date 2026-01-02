package io.github.globalscout.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


const val ALL_COUNTRIES_URL = "https://restcountries.com/v3.1/all?fields=name,capital,region,flag,cca3"

class CountryService {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    suspend fun fetchAll(): List<Country> {
        return client.get(ALL_COUNTRIES_URL).body<List<Country>>()
    }

    suspend fun getCountry(cca3: String): List<CountryDetail> {
        return client.get("https://restcountries.com/v3.1/alpha/$cca3").body()
    }
}