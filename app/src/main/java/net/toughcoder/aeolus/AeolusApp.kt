package net.toughcoder.aeolus

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import net.toughcoder.aeolus.data.DataContainer
import net.toughcoder.aeolus.ui.AeolusScreen
import net.toughcoder.aeolus.ui.WeatherViewModel
import net.toughcoder.aeolus.ui.theme.AeolusTheme

@Composable
fun AeolusApp(
    dataContainer: DataContainer,
    modifier: Modifier = Modifier
) {
    AeolusTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background
        ) {
            val viewModel: WeatherViewModel = viewModel(
                factory = WeatherViewModel.provideFactory(
                    dataContainer.locationRepository,
                    dataContainer.weatherNowRepository
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AeolusScreen(modifier, uiState, viewModel::refresh)
        }
    }
}