package io.github.globalscout.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.TextButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.globalscout.ui.model.CountryDetailUi
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CountryDetailScreen(
    cca3: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CountryDetailViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(cca3) {
        viewModel.loadCountry(cca3)
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (val uiState = state) {
                is CountryDetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CountryDetailUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is CountryDetailUiState.Success -> {
                    CountryDetailContent(
                        detail = uiState.detail,
                        onBackClick = onBackClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CountryDetailContent(
    detail: CountryDetailUi,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        // Glassmorphic Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            AsyncImage(
                model = detail.flagUrl,
                contentDescription = "Flag of ${detail.name}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.6f to Color.Transparent,
                            1.0f to MaterialTheme.colorScheme.surface
                        )
                    )
            )

            // Content Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = detail.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = detail.nativeName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Bento Grid Content
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Row 1: Primary Row (Asymmetrical / Dynamic)
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                // Dynamic sizing:
                // > 20 chars (e.g. Sri Jayawardenepura Kotte): Capital gets 2/3 space.
                // > 10 chars (e.g. Washington, D.C.): Capital gets 1/2 space.
                // <= 10 chars: Capital gets 1/3 space (Standard).
                val capitalLength = detail.capital.length
                val (populationWeight, capitalWeight) = when {
                    capitalLength > 20 -> 1f to 2f
                    capitalLength > 10 -> 1f to 1f
                    else -> 2f to 1f
                }

                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left: Population
                    BentoCard(
                        icon = Icons.Default.Person,
                        label = "Population",
                        value = detail.population,
                        modifier = Modifier
                            .weight(populationWeight)
                            .fillMaxHeight()
                    )

                    // Right: Capital
                    BentoCard(
                        icon = Icons.Default.Home,
                        label = "Capital",
                        value = detail.capital,
                        modifier = Modifier
                            .weight(capitalWeight)
                            .fillMaxHeight(),
                        maxLines = 3
                    )
                }
            }

            // Row 2: Identity Card (Combined)
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Official Name
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Official Name",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = detail.officialName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                        // Currency
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Currency",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = if (detail.currencies.isNotEmpty()) detail.currencies else "N/A",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Row 3: Secondary Row (Balanced 50/50)
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                var showTimezoneDialog by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BentoCard(
                        icon = Icons.Default.Place,
                        label = "Region",
                        value = detail.region,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        maxLines = 3
                    )

                    // Smart Collapsible Timezone Card
                    val timezones = detail.timezones
                    val isCollapsible = timezones.size > 3
                    val timezoneDisplay = if (isCollapsible) {
                        "${timezones.take(3).joinToString(", ")} +${timezones.size - 3} more"
                    } else {
                        timezones.joinToString(", ")
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                         BentoCard(
                            icon = Icons.Default.Schedule,
                            label = "Timezone",
                            value = timezoneDisplay,
                            modifier = Modifier.fillMaxSize(), // Fill the Box
                            maxLines = 4 // Enough for the summary
                        )
                        // Overlay clickable surface if collapsible
                        if (isCollapsible) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = Color.Transparent,
                                onClick = { showTimezoneDialog = true }
                            ) {}
                        }
                    }
                }

                if (showTimezoneDialog) {
                    AlertDialog(
                        onDismissRequest = { showTimezoneDialog = false },
                        title = { Text("Timezones") },
                        text = {
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            ) {
                                detail.timezones.forEach { timezone ->
                                    Text(
                                        text = timezone,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showTimezoneDialog = false }) {
                                Text("Close")
                            }
                        }
                    )
                }
            }
            
            // Row 4: Languages (Chips)
            if (detail.languages.isNotEmpty()) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Languages",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            detail.languages.forEach { language ->
                                LanguageChip(language)
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun LanguageChip(language: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = language,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun BentoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 2
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Previews

private val sampleCountryDetail = CountryDetailUi(
    cca3 = "EGY",
    name = "Egypt",
    officialName = "Arab Republic of Egypt",
    nativeName = "Miṣr",
    capital = "Cairo",
    region = "Africa, Northern Africa",
    population = "104,258,327",
    flagUrl = "https://flagcdn.com/w320/eg.png",
    currencies = "Egyptian pound (£)",
    languages = listOf("Arabic"),
    timezones = listOf("UTC+02:00")
)

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun CountryDetailScreenSuccessPreview() {
    MaterialTheme {
        CountryDetailContent(
            detail = sampleCountryDetail,
            onBackClick = {}
        )
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
fun CountryDetailScreenLongDataPreview() {
    MaterialTheme {
        CountryDetailContent(
            detail = sampleCountryDetail.copy(
                capital = "Sri Jayawardenepura Kotte",
                timezones = listOf("UTC-05:00", "UTC-06:00", "UTC-07:00", "UTC-08:00", "UTC+10:00")
            ),
            onBackClick = {}
        )
    }
}
