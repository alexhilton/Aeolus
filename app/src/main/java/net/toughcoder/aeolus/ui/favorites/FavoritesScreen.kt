package net.toughcoder.aeolus.ui.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.ui.CityState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel,
    onBack: () -> Unit,
    onSearch: () -> Unit
) {
    val favoriteCities by viewModel.getAllFavorites().collectAsStateWithLifecycle(initialValue = listOf())

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = stringResource(R.string.favorites_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search a city"
                        )
                    }
                }
            )
        }
    ) { it ->
        Box(
            modifier = Modifier.padding(it),
            contentAlignment = Alignment.Center
        ) {
            if (favoriteCities.isEmpty()) {
                Text(
                    text = "No favorites yet, go to Search page to add favorite city.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                FavoriteList(modifier, favoriteCities) { city ->
                    viewModel.setDefaultCity(city)
                    onBack()
                }
            }
        }
    }
}

@Composable
fun FavoriteList(
    modifier: Modifier = Modifier,
    favorites: List<FavoriteState>,
    onFavoriteClick: (CityState) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(favorites) {
            FavoriteItem(modifier, it, onFavoriteClick)
        }
    }
}

@Composable
fun FavoriteItem(
    modifier: Modifier = Modifier,
    item: FavoriteState,
    onClick: (CityState) -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(item.city) },
        shape = MaterialTheme.shapes.extraSmall,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier.padding(8.dp)) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    text = "${item.city.name}, ${item.city.admin}"
                )

                if (!item.snapshot.isEmpty()) {
                    Spacer(Modifier.height(8.dp))

                    WeatherSnapshot(modifier, item.snapshot)
                }
            }

            if (item.selected) {
                Checkbox(
                    checked = true, onCheckedChange = {}
                )
            }
        }
    }
}


@Composable
fun WeatherSnapshot(
    modifier: Modifier = Modifier,
    snapshot: DayWeatherUiState
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = snapshot.text,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(16.dp))

            Image(
                modifier = Modifier
                    .size(36.dp),
                painter = painterResource(snapshot.icon),
                contentScale = ContentScale.Fit,
                contentDescription = ""
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = "${snapshot.high} / ${snapshot.low}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = snapshot.windDir,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.width(16.dp))

            Image(
                modifier = Modifier
                    .size(24.dp)
                    .rotate(snapshot.windDegree),
                painter = painterResource(snapshot.iconDir),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = snapshot.windScale,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview
@Composable
fun FavoriteItemPreview() {
    FavoriteItem(
        Modifier.fillMaxWidth(),
        FavoriteState(
            CityState("Beijing", "", "China"),
            DayWeatherUiState("19", "1", R.drawable.ic_100, "Cloudy", 123f, R.drawable.ic_nav, "South", "1")
        )
    ) {}
}