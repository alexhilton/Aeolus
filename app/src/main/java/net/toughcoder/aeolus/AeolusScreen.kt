package net.toughcoder.aeolus

import android.util.Log
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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AeolusScreen(
    modifier: Modifier = Modifier,
    uiState: NowUiState,
    onRefresh: () -> kotlin.Unit
) {
    val weatherState = uiState as NowUiState.WeatherNowUiState
    val state = rememberPullRefreshState(uiState.isLoading, onRefresh)
    val snackbarHostState = remember { SnackbarHostState() }

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
                        text = weatherState.city,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
        ) {
            WeatherScreen(
                uiState = weatherState,
                modifier = modifier
                    .padding(
                        horizontal = 8.dp,
                        vertical = it.calculateTopPadding() + if (uiState.isLoading) 40.dp else 8.dp
                    )
            )
            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = state,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = colorResource(R.color.teal_700),
            )

            if (uiState.errorMessage.isNotEmpty()) {
                val message = remember(uiState) { uiState.errorMessage }
                LaunchedEffect(key1 = message, key2 = snackbarHostState) {
                    snackbarHostState.showSnackbar(uiState.errorMessage)
                }
            }
        }
    }
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