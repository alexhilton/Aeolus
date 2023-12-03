package net.toughcoder.aeolus.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun WeatherIndexSection(
    modifier: Modifier = Modifier.fillMaxWidth(),
    indices: List<IndexUiState>
) {
    if (indices.isNotEmpty()) {
        Spacer(Modifier.height(8.dp))

        WeatherIndexGrid(modifier, indices)
    }
}

@Composable
fun WeatherIndexGrid(
    modifier: Modifier,
    indices: List<IndexUiState>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shadowElevation = 6.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WeatherIndexRow(left = indices[0], right = indices[1])
            WeatherIndexRow(left = indices[2], right = indices[3])
            WeatherIndexRow(left = indices[4], right = indices[5])
        }
    }
}

@Composable
fun WeatherIndexRow(
    left: IndexUiState,
    right: IndexUiState
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WeatherIndexItem(Modifier.weight(1f), left)
        WeatherIndexItem(Modifier.weight(1f), right)
    }
}

@Composable
fun WeatherIndexItem(
    modifier: Modifier,
    item: IndexUiState
) {
    Column(modifier.padding(6.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(48.dp),
                painter = painterResource(item.icon),
                contentDescription = item.name
            )
            Column {
                ValueLabel(text = item.name)
                TitleLabel(text = item.category)
            }
        }

        Spacer(Modifier.height(8.dp))

        GeneralText(text = item.text)
    }
}