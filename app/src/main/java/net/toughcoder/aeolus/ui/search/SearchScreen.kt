package net.toughcoder.aeolus.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onAddLocation: (String) -> Unit
) {
    val hotCities = listOf("beijing", "shanghai", "shenzhen", "guangzhou", "nanjing", "suchou", "hangzhou")
    var searchResults = remember { mutableStateListOf<String>() }

    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        SearchComponent(modifier, onBack) {
            searchResults.add(it)
        }

        Spacer(Modifier.height(16.dp))

        HotCities(modifier, hotCities) {
            onAddLocation(it)
            onBack()
        }

        Spacer(Modifier.height(16.dp))

        SearchResults(modifier, searchResults) {
            onAddLocation(it)
            onBack()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchComponent(
    modifier: Modifier,
    onBack: () -> Unit,
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var searchHistories = remember { mutableStateListOf<String>() }

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
                    searchHistories.add(it)
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

@Composable
fun HotCities(
    modifier: Modifier = Modifier,
    cities: List<String>,
    onHotClick: (String) -> Unit
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cities) {
            HotCityItem(modifier, it, onHotClick)
        }
    }
}

@Composable
fun HotCityItem(
    modifier: Modifier,
    city: String,
    onHotClick: (String) -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = { onHotClick(city) }
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = city,
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
fun SearchResults(
    modifier: Modifier = Modifier,
    results: List<String>,
    onResultClick: (String) -> Unit
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
    result: String,
    onResultClick: (String) -> Unit
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
                text = result
            )
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                text = "Other info of $result"
            )
        }
    }
}

@Preview
@Composable
fun HotCityItemPreview() {
    HotCityItem(modifier = Modifier, city = "Beijing") {}
}

@Preview
@Composable
fun SearchResultItemPreview() {
    SearchResultItem(modifier = Modifier, result = "Francisco") {}
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen(onBack = { /*TODO*/ }, onAddLocation = {})
}