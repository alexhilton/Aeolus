package net.toughcoder.aeolus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataContainer = (application as AeolusAndroidApp).dataContainer
        setContent {
            AeolusApp(dataContainer, Modifier.fillMaxWidth())
        }
    }
}