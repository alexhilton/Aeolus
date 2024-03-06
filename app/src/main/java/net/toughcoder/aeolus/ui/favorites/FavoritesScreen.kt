package net.toughcoder.aeolus.ui.favorites

import android.annotation.SuppressLint
import android.view.animation.AlphaAnimation
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.ui.AeolusSnackbarHost
import net.toughcoder.aeolus.ui.CenteredLoadingContainer
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

    val snackbarHostState = remember { SnackbarHostState() }

    val itemToRemove = remember {
        mutableStateOf<FavoriteUiState?>(null)
    }

    val coroutineScope = rememberCoroutineScope()

    var alphaAnimation = remember { Animatable(0f) }

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
        },
        snackbarHost = { AeolusSnackbarHost(snackbarHostState, modifier) }
    ) { it ->
        CenteredLoadingContainer(
            modifier = Modifier.padding(it),
            uiState.loading
        ) {
            if (uiState.favorites.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_favorites),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                FavoriteList(
                    alphaAnimation = alphaAnimation,
                    favorites = uiState.favorites,
                    onFavoriteRemove = { item -> itemToRemove.value = item }
                ) { city ->
                    viewModel.setDefaultCity(city)
                    onBack()
                }
            }
        }

        itemToRemove.value?.let { item ->
            AlertDialog(
                icon = { Icon(Icons.Default.Info, contentDescription = null) },
                title = { Text(stringResource(R.string.favorite_delete_title)) },
                text = { Text(stringResource(R.string.favorite_delete_message, item.city.name)) },
                onDismissRequest = { itemToRemove.value = null },
                confirmButton = {
                    val deleteNotify = stringResource(R.string.favorite_delete_notify, item.city.name)
                    TextButton(
                        onClick = {
                            if (item.selected) {
                                coroutineScope.launch {
                                    alphaAnimation.snapTo(0f)
                                }
                            }

                            viewModel.removeFavorite(item) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(deleteNotify)
                                }
                            }

                            itemToRemove.value = null
                        }
                    ) {
                        Text(stringResource(R.string.favorite_delete_confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { itemToRemove.value = null }
                    ) {
                        Text(stringResource(R.string.button_text_cancel))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteList(
    modifier: Modifier = Modifier,
    alphaAnimation: Animatable<Float, AnimationVector1D>,
    favorites: List<FavoriteUiState>,
    onFavoriteRemove: (FavoriteUiState) -> Unit,
    onFavoriteClick: (CityState) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            favorites,
            key = { it.city.id }
        ) {
            FavoriteItem(
                modifier = modifier.animateItemPlacement(),
                alphaAnimation = alphaAnimation,
                item = it,
                onRemove = onFavoriteRemove,
                onClick = onFavoriteClick
            )
        }
    }
}

@Composable
fun FavoriteItem(
    modifier: Modifier = Modifier,
    alphaAnimation: Animatable<Float, AnimationVector1D>,
    item: FavoriteUiState,
    onRemove: (FavoriteUiState) -> Unit,
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
                if (item.current()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            tint = colorResource(R.color.teal_700),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            text = item.city.fullname()
                        )
                    }
                } else {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        text = item.city.fullname()
                    )
                }

                if (!item.snapshot.isEmpty()) {
                    Spacer(Modifier.height(8.dp))

                    WeatherSnapshot(modifier, item.snapshot)
                }
            }

            if (item.selected || !item.current()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.selected) {
                        LaunchedEffect(item.city.id) {
                            alphaAnimation.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(300)
                            )
                        }
                        Checkbox(
                            modifier = Modifier.graphicsLayer { alpha = alphaAnimation.value },
                            checked = true,
                            onCheckedChange = {}
                        )
                    }

                    if (!item.current()) {
                        IconButton(
                            onClick = { onRemove(item) }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Delete item")
                        }
                    }
                }
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

@SuppressLint("UnrememberedAnimatable")
@Preview
@Composable
fun FavoriteItemPreview() {
    FavoriteItem(
        Modifier.fillMaxWidth(),
        Animatable(1f),
        FavoriteUiState(
            CityState("Beijing", "", "China"),
            DailyUiState(
                "2023-11-19", "19", 19f,
                "1", 1f,"06:07", "18:00",
                net.toughcoder.qweather.R.drawable.ic_100, "Cloudy",
                "12", "Sun",
                R.drawable.ic_nav, 123f, "South", "1"
            )
        ),
        { _ -> 4 }
    ) { _ -> 3 }
}