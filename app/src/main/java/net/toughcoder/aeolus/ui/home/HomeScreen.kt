package net.toughcoder.aeolus.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.ui.AeolusSnackbarHost
import net.toughcoder.aeolus.ui.NO_ERROR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    navToDaily: (String) -> Unit,
    navToSettings: () -> Unit,
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
                    if (uiState.city?.current() == true) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                tint = colorResource(R.color.teal_700),
                                contentDescription = ""
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = uiState.city?.fullname() ?: "null",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Text(
                            text = uiState.city?.fullname() ?: "null",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navToFavorites) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.description_manage_city)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = navToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.description_settings)
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
                    message = stringResource(uiState.errorMessage)
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
                            .padding(4.dp),
                        uiState = uiState as HomeUiState.WeatherUiState,
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

        if (!uiState.isEmpty() && !uiState.isLoading && uiState.errorMessage != NO_ERROR) {
            val message = stringResource(uiState.errorMessage)
            LaunchedEffect(key1 = uiState.errorMessage, key2 = snackbarHostState) {
                snackbarHostState.showSnackbar(message)
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