package io.github.globalscout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.globalscout.data.CountryRepository
import io.github.globalscout.data.network.CountryService
import io.github.globalscout.ui.HomeViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val viewModel = koinViewModel<HomeViewModel>()
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var text by remember { mutableStateOf("Loading") }
            LaunchedEffect(true) {
                text = try {
                    viewModel.getAllCountries().first().toString()
                } catch (e: Exception) {
                    e.message ?: "error"
                }
            }
            GreetingView(text)
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MaterialTheme {
        GreetingView("Hello, Android!")
    }
}