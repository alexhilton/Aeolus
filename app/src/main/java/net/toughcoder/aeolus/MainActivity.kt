package net.toughcoder.aeolus

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataContainer = (application as AeolusAndroidApp).dataContainer
        setContent {
            AeolusApp(dataContainer, Modifier.fillMaxWidth())
        }
    }
}