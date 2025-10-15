package tech.lizza.demoxr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.lizza.demoxr.navigation.EventNavigation
import tech.lizza.demoxr.ui.theme.DemoXRTheme
import tech.lizza.demoxr.viewmodel.EventViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: EventViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val windowSizeClass = calculateWindowSizeClass(this)

            DemoXRTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EventNavigation(
                        viewModel = viewModel,
                        windowSizeClass = windowSizeClass
                    )
                }
            }
        }
    }
}
