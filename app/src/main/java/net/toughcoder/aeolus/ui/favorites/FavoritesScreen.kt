package net.toughcoder.aeolus.ui.favorites

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import net.toughcoder.aeolus.ui.DailyUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel,
    onBack: () -> Unit,
    onSearch: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                            contentDescription = stringResource(R.string.description_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.description_search)
                        )
                    }
                }
            )
        }
    ) { it ->
        Box(
            modifier = Modifier.padding(it).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                modifier = modifier,
                targetState = uiState.loading,
                label = "crossfade"
            ) { loading ->
                if (loading) {
                    CircularProgressIndicator(Modifier.size(88.dp))
                } else if (uiState.favorites.isEmpty()) {
                    Text(
                        text = stringResource(R.string.empty_favorites),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    FavoriteList(favorites = uiState.favorites) { city ->
                        viewModel.setDefaultCity(city)
                        onBack()
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteList(
    modifier: Modifier = Modifier,
    favorites: List<FavoriteUiState>,
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
    item: FavoriteUiState,
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
                    text = item.city.fullname()
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
    snapshot: DailyUiState
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = snapshot.textDay,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(16.dp))

            Image(
                modifier = Modifier
                    .size(36.dp),
                painter = painterResource(snapshot.iconDay),
                contentScale = ContentScale.Fit,
                contentDescription = ""
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = "${snapshot.tempHigh} / ${snapshot.tempLow}",
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
        FavoriteUiState(
            CityState("Beijing", "", "China"),
            DailyUiState(
                "2023-11-19", "19", "1", "06:07", "18:00",
                R.drawable.ic_100, "Cloudy",
                "12", "Sun",
                R.drawable.ic_nav, 123f, "South", "1"
            )
        )
    ) {}
}