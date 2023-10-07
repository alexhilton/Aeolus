package net.toughcoder.aeolus

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import net.toughcoder.aeolus.ui.theme.AeolusTheme

@Composable
fun AeolusApp(modifier: Modifier = Modifier) {
    AeolusTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.background
        ) {
            AeolusScreen(
                modifier,
                viewModel()
            )
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