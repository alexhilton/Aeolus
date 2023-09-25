package net.toughcoder.aeolus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AeolusScreen(modifier: Modifier = Modifier) {
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
                        text = "Beijing",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) {
        Column(modifier = modifier.padding(it)) {
            WeatherDetails(modifier)
            Weather24Hours(modifier)
            Weather15Days(modifier)
        }
    }
}

@Composable
fun WeatherDetails(modifier: Modifier = Modifier) {
    Text(text = "Shows now weather details. \n Scroll vertically")
}

@Composable
fun Weather24Hours(modifier: Modifier = Modifier) {
    Text(text = "show weather in 24 hours.\n Scroll horizontally.")
}

@Composable
fun Weather15Days(modifier: Modifier = Modifier) {
    Text(text = "Show weather in 15 days. \n Scroll horizontally.")
}

@Preview
@Composable
fun AeolusScreenPreview() {
    AeolusScreen()
}