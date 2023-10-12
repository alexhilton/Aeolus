package net.toughcoder.aeolus

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
        }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .pullRefresh(state)
                .verticalScroll(rememberScrollState())) {
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
        }
    }
}