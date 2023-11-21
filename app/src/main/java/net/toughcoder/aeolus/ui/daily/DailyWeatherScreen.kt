package net.toughcoder.aeolus.ui.daily

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.toughcoder.aeolus.ui.DailyUiState
import net.toughcoder.aeolus.ui.favorites.DayWeatherUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: DailyWeatherViewModel,
    onBack: () -> Unit
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
                        text = "${uiState.city?.name},${uiState.city?.admin} 7天预报",
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
                }
            )
        }
    ) {
        if (uiState.dailyWeathers.isEmpty()) {
            Text(modifier = Modifier.padding(it), text = "Daily weather shows here")
        } else {
            DailyHorizontalList(modifier.padding(it), uiState.dailyWeathers)
        }
    }
}

@Composable
fun DailyHorizontalList(
    modifier: Modifier = Modifier,
    weathers: List<DailyUiState>
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        weathers.forEach {
            DailyDetailItem(weather = it)
        }
    }
}

@Composable
fun DailyDetailItem(
    modifier: Modifier = Modifier,
    weather: DailyUiState
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(weather.weekday)
            Text(weather.date)
            Image(
                modifier = Modifier
                    .size(36.dp),
                painter = painterResource(weather.iconDay),
                contentScale = ContentScale.Fit,
                contentDescription = ""
            )
            Text(weather.textDay)
            Text(weather.tempHigh)

            Spacer(Modifier.height(36.dp))

            Text(weather.tempLow)
            Image(
                modifier = Modifier
                    .size(36.dp),
                painter = painterResource(weather.iconNight),
                contentScale = ContentScale.Fit,
                contentDescription = ""
            )
            Text(weather.textNight)
            Text(weather.windDir)
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .rotate(weather.windDegree),
                painter = painterResource(weather.iconDir),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )
            Text(weather.windScale)
            Text("紫外线 ${weather.uvIndex}")
            Text("湿度 ${weather.humidity}")
            Text("气压 ${weather.pressure}")
            Text("能见度 ${weather.visibility}")
        }
    }
}