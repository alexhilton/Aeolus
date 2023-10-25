package net.toughcoder.aeolus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.toughcoder.aeolus.ui.theme.AeolusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataContainer = (application as AeolusAndroidApp).dataContainer
        setContent {
            AeolusApp(dataContainer, Modifier.fillMaxWidth())
//            PullRefresh()
        }
    }
}