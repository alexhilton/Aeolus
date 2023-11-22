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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.ui.DailyUiState
import net.toughcoder.aeolus.ui.favorites.DayWeatherUiState
import net.toughcoder.aeolus.ui.home.GeneralInfo
import net.toughcoder.aeolus.ui.home.GeneralText

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
                        text = stringResource(
                            R.string.daily_screen_title,
                            "${uiState.city?.name},${uiState.city?.admin}"
                        ),
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
                }
            )
        }
    ) {
        if (uiState.dailyWeathers.isEmpty()) {
            Text(
                modifier = Modifier.padding(it),
                text = stringResource(R.string.daily_screen_placeholder)
            )
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
            GeneralText(weather.weekday)
            GeneralText(weather.date)
            Image(
                modifier = Modifier
                    .size(36.dp),
                painter = painterResource(weather.iconDay),
                contentScale = ContentScale.Fit,
                contentDescription = ""
            )
            GeneralText(weather.textDay)
            GeneralText(weather.tempHigh)

            Spacer(Modifier.height(36.dp))

            GeneralText(weather.tempLow)
            Image(
                modifier = Modifier
                    .size(36.dp),
                painter = painterResource(weather.iconNight),
                contentScale = ContentScale.Fit,
                contentDescription = ""
            )
            GeneralText(weather.textNight)
            GeneralText(weather.windDir)
            Image(
                modifier = Modifier
                    .size(24.dp)
                    .rotate(weather.windDegree),
                painter = painterResource(weather.iconDir),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )
            GeneralText(weather.windScale)
            GeneralText("紫外线 ${weather.uvIndex}")
            GeneralText("湿度 ${weather.humidity}")
            GeneralText("气压 ${weather.pressure}")
            GeneralText("能见度 ${weather.visibility}")
        }
    }
}