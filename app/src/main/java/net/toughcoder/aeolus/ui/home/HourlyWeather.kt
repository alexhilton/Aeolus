package net.toughcoder.aeolus.ui.home

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.ui.GeneralText
import net.toughcoder.aeolus.ui.WeatherSectionContainer

@Composable
fun HourlyWeatherSection(
    modifier: Modifier = Modifier,
    hourlyUiStates: List<HourlyUiState>
) {
    if (hourlyUiStates.isNotEmpty()) {
        Spacer(Modifier.height(16.dp))

        val max = hourlyUiStates.maxOfOrNull { it.tempValue }!!
        val min = hourlyUiStates.minOfOrNull { it.tempValue }!!
        WeatherSectionContainer(modifier, R.string.hourly_forecast_title) {
            LazyRow(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hourlyUiStates) {
                    HourlyListItem(modifier, max, min, it)
                }
            }
        }
    }
}

@Composable
fun HourlyListItem(
    modifier: Modifier = Modifier,
    max: Float,
    min: Float,
    hourlyItem: HourlyUiState
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            modifier = Modifier
                .size(36.dp),
            painter = painterResource(hourlyItem.icon),
            contentScale = ContentScale.Fit,
            contentDescription = ""
        )
        GeneralText(hourlyItem.text)

        TempBar(
            text = hourlyItem.temp,
            max = max,
            min = min,
            high = hourlyItem.tempValue.coerceAtLeast(0f),
            low = hourlyItem.tempValue.coerceAtMost(0f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(18.dp)
                    .rotate(hourlyItem.windDegree),
                painter = painterResource(hourlyItem.iconDir),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )
            GeneralText(hourlyItem.windScale)
        }

        GeneralText(hourlyItem.time)
    }
}

@Composable
fun TempBar(
    modifier: Modifier = Modifier,
    text: String,
    max: Float,
    min: Float,
    high: Float,
    low: Float
) {
    val height = (high - low) / (max - min) * HEIGHT
    val y0 = (max - high) / (max - min) * HEIGHT
    val cw = with(LocalDensity.current) {
        WIDTH.toDp()
    }
    val ch = with(LocalDensity.current) {
        height.toDp()
    }
    val margin = with(LocalDensity.current) {
        y0.toDp()
    }
    val columnHeight = with(LocalDensity.current) {
        HEIGHT.toDp()
    }
    Column(
        modifier = Modifier.height(columnHeight.times(1.6f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(margin))

        GeneralText(text)

        Canvas(
            modifier = Modifier
                .width(cw)
                .height(ch)
                .align(Alignment.CenterHorizontally),
            onDraw = {
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset((size.width - WIDTH) / 2f, 0f),
                    size = Size(WIDTH, height),
                    cornerRadius = CornerRadius(8.dp.value, 8.dp.value)
                )
            }
        )
    }
}
val brush = Brush.verticalGradient(
    colors = listOf(Color.Green, Color.Blue),
)
const val WIDTH = 40f
const val HEIGHT = 120f