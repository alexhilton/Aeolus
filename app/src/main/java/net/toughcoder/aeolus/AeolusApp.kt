package net.toughcoder.aeolus

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import net.toughcoder.aeolus.ui.theme.AeolusTheme

@Composable
fun AeolusApp(modifier: Modifier = Modifier) {
    AeolusTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background
        ) {
            val viewModel: WeatherViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(key1 = Unit) {
                delay(1000)
                viewModel.refresh()
            }
            AeolusScreen(modifier, uiState, viewModel::refresh)
        }
    }
}

@Preview(widthDp = 480)
@Composable
fun AeolusScreenPreview() {
    AeolusTheme {
        AeolusApp()
    }
}