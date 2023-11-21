package net.toughcoder.aeolus.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.toughcoder.aeolus.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: NowUiState,
    onRefresh: () -> Unit,
    navToDaily: (String) -> Unit,
    navToFavorites: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state = rememberPullRefreshState(uiState.isLoading, onRefresh)

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
                        text = "${uiState.city?.name}, ${uiState.city?.admin}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navToFavorites) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Go to favorites"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            AeolusSnackbarHost(snackbarHostState, modifier)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(state)
                .verticalScroll(rememberScrollState())
                .padding(vertical = it.calculateTopPadding())
        ) {
            if (uiState.isEmpty()) {
                EmptyScreen(
                    modifier = Modifier.align(Alignment.Center),
                    message = uiState.errorMessage
                )
            } else {
                val visible by remember { mutableStateOf(true) }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    WeatherScreen(
                        modifier = Modifier
                            .padding(8.dp),
                        uiState = uiState as NowUiState.WeatherNowUiState,
                        gotoDaily = { uiState.city?.let { city -> navToDaily(city.id) } }
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = state,
                modifier = Modifier
                    .align(Alignment.TopCenter),
                contentColor = colorResource(R.color.teal_700),
                scale = true
            )
        }

        if (!uiState.isEmpty() && !uiState.isLoading && uiState.errorMessage.isNotEmpty()) {
            val message = remember(uiState) { uiState.errorMessage }
            LaunchedEffect(key1 = message, key2 = snackbarHostState) {
                snackbarHostState.showSnackbar(uiState.errorMessage)
            }
        }
    }
}

@Composable
fun EmptyScreen(
    modifier: Modifier = Modifier,
    message: String
) {
    Text(
        modifier = modifier.padding(16.dp),
        text = message,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun AeolusSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
            .systemBarsPadding()
            // Limit the Snackbar width for large screens
            .wrapContentWidth(align = Alignment.Start)
            .widthIn(max = 550.dp),
        snackbar = { Snackbar(it) }
    )
}

@Preview
@Composable
fun EmptyScreenPreview() {
    Box(Modifier.fillMaxSize()) {
        EmptyScreen(
            modifier = Modifier.align(Alignment.Center),
            message = "The quick brown fox jumps over the lazy dog!"
        )
    }
}