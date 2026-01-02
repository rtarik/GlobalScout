package io.github.globalscout.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.globalscout.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object ListDestination

@Serializable
data class DetailDestination(val countryName: String)

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    AppTheme {
        NavHost(
            navController = navController,
            startDestination = ListDestination
        ) {
            composable<ListDestination> {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CountryListScreen(
                        state = state,
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onCountryClick = { country ->
                            navController.navigate(DetailDestination(countryName = country.name))
                        },
                        modifier = Modifier.safeContentPadding()
                    )
                }
            }

            composable<DetailDestination> { backStackEntry ->
                val detail: DetailDestination = backStackEntry.toRoute()
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CountryDetailScreen(
                        countryName = detail.countryName,
                        modifier = Modifier.safeContentPadding()
                    )
                }
            }
        }
    }
}