package net.toughcoder.aeolus.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel,
    onBack: () -> Unit,
    onAddLocation: (String) -> Unit
) {
    val hotCities by searchViewModel.getTopCities().collectAsStateWithLifecycle(initialValue = listOf())
    val searchResults by searchViewModel.searchResultState.collectAsStateWithLifecycle()
    val searchHistories by searchViewModel.getSearchHistories().collectAsStateWithLifecycle(
        initialValue = listOf()
    )

    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        SearchComponent(
            modifier,
            searchHistories,
            onBack,
            { searchViewModel.addSearchHistory(it) }
        ) {
            searchViewModel.searchCity(it)
        }

        Spacer(Modifier.height(16.dp))

        HotCities(modifier, hotCities) {
            onAddLocation(it.name)
            onBack()
        }

        Spacer(Modifier.height(16.dp))

        SearchResultComponent(modifier, searchResults) {
            onAddLocation(it.name)
            onBack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchComponent(
    modifier: Modifier,
    searchHistories: List<String>,
    onBack: () -> Unit,
    onAddHistory: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            query = query,
            onQueryChange = { query = it},
            onSearch = {
                active = false
                if (it.trim().isNotEmpty()) {
                    onAddHistory(it)
                    onSearch(it)
                }
            },
            active = active,
            onActiveChange = { active = it },
            modifier = Modifier.weight(1f),
            enabled = true,
            placeholder = { Text("Search a city") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = if (active && query.isNotEmpty()) {
                {
                    IconButton(onClick = { query = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            } else {
                null
            }
        ) {
            SearchHistories(modifier, searchHistories) {
                active = false
                onSearch(it)
            }
        }

        if (!active) {
            Spacer(Modifier.width(16.dp))

            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Done"
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HotCities(
    modifier: Modifier = Modifier,
    cities: List<TopCityState>,
    onHotClick: (TopCityState) -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        cities.forEach {
            HotCityItem(modifier, it, onHotClick)
        }
    }
}

@Composable
fun HotCityItem(
    modifier: Modifier,
    city: TopCityState,
    onHotClick: (TopCityState) -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = { onHotClick(city) }
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = city.name,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun SearchHistories(
    modifier: Modifier,
    histories: List<String>,
    onHistoryClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(histories) {
            HistoryItem(modifier, it, onHistoryClick)
        }
    }
}

@Composable
fun HistoryItem(
    modifier: Modifier,
    history: String,
    onHistoryClick: (String) -> Unit
) {
    Text(
        modifier = Modifier
            .padding(16.dp)
            .clickable { onHistoryClick(history) },
        text = history,
    )
}

@Composable
fun SearchResultComponent(
    modifier: Modifier = Modifier,
    result: SearchResultState,
    onResultClick: (TopCityState) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (result.loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (result.error.isNotEmpty()) {
            Text(
                text = result.error,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            SearchResultList(modifier, result.cities, onResultClick)
        }
    }
}

@Composable
fun SearchResultList(
    modifier: Modifier = Modifier,
    results: List<TopCityState>,
    onResultClick: (TopCityState) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(results) {
            SearchResultItem(modifier = modifier, result = it, onResultClick)
        }
    }
}

@Composable
fun SearchResultItem(
    modifier: Modifier,
    result: TopCityState,
    onResultClick: (TopCityState) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable { onResultClick(result) }
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                text = result.name
            )
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                text = result.admin
            )
        }
    }
}

@Preview
@Composable
fun HotCityItemPreview() {
    HotCityItem(modifier = Modifier, city = TopCityState("Bei Jing")) {}
}

@Preview
@Composable
fun SearchResultItemPreview() {
    SearchResultItem(modifier = Modifier, result = TopCityState("Francisco")) {}
}