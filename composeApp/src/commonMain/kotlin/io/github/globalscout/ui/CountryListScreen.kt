package io.github.globalscout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.globalscout.ui.model.CountryUi
import io.github.globalscout.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CountryListScreen(
    state: CountryUiState,
    modifier: Modifier = Modifier
) {
    when (state) {
        is CountryUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is CountryUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        is CountryUiState.Success -> {
            CountryList(
                countries = state.countries,
                modifier = modifier
            )
        }
    }
}

@Composable
fun CountryList(
    countries: List<CountryUi>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(countries) { country ->
            CountryCard(country = country)
        }
    }
}

@Composable
fun CountryCard(
    country: CountryUi,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = country.flag,
                fontSize = 40.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = country.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (country.capital.isNotEmpty()) {
                    Text(
                        text = "Capital: ${country.capital}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Text(
                    text = "Region: ${country.region}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Previews

private val sampleCountry = CountryUi(
    name = "Egypt",
    capital = "Cairo",
    region = "Africa",
    flag = "🇪🇬"
)

@Preview
@Composable
fun CountryCardPreview() {
    AppTheme {
        CountryCard(country = sampleCountry)
    }
}

@Preview
@Composable
fun CountryListScreenSuccessPreview() {
    AppTheme {
        CountryListScreen(
            state = CountryUiState.Success(
                countries = listOf(
                    sampleCountry,
                    sampleCountry.copy(
                        name = "France",
                        capital = "Paris",
                        region = "Europe",
                        flag = "🇫🇷"
                    ),
                    sampleCountry.copy(
                        name = "Japan",
                        capital = "Tokyo",
                        region = "Asia",
                        flag = "🇯🇵"
                    )
                )
            )
        )
    }
}

@Preview
@Composable
fun CountryListScreenLoadingPreview() {
    AppTheme {
        CountryListScreen(state = CountryUiState.Loading)
    }
}

@Preview
@Composable
fun CountryListScreenErrorPreview() {
    AppTheme {
        CountryListScreen(state = CountryUiState.Error("Failed to fetch data"))
    }
}
