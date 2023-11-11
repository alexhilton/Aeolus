package net.toughcoder.aeolus.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val hotCities = listOf("beijing", "shanghai", "shenzhen", "guangzhou", "nanjing", "suchou", "hangzhou")
    var searchResults = remember { mutableStateListOf<String>() }

    Column(
        modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        SearchBar(
            query = query,
            onQueryChange = { query = it},
            onSearch = {
                active = false
                searchResults.add(it)
                query = ""
            },
            active = active,
            onActiveChange = { active = it },
            modifier = modifier,
            enabled = true,
            placeholder = { Text("Search a city") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        ) {
            HotCities(modifier, hotCities)
        }

        Spacer(Modifier.height(16.dp))

        SearchResults(modifier, searchResults)
    }
}

@Composable
fun HotCities(
    modifier: Modifier = Modifier,
    cities: List<String>
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cities) {
            HotCityItem(modifier = modifier, city = it)
        }
    }
}

@Composable
fun HotCityItem(
    modifier: Modifier,
    city: String
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = city,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun SearchResults(
    modifier: Modifier = Modifier,
    results: List<String>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(results) {
            SearchResultItem(modifier = modifier, result = it)
        }
    }
}

@Composable
fun SearchResultItem(
    modifier: Modifier,
    result: String
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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
    HotCityItem(modifier = Modifier, city = "Beijing")
}

@Preview
@Composable
fun SearchResultItemPreview() {
    SearchResultItem(modifier = Modifier, result = "Francisco")
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen(onBack = { /*TODO*/ }, onAddLocation = {})
}