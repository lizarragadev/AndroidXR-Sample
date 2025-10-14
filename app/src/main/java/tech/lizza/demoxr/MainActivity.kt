package tech.lizza.demoxr

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.platform.LocalSpatialConfiguration
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.movable
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.width
import tech.lizza.demoxr.navigation.EventNavigation
import tech.lizza.demoxr.ui.theme.DemoXRTheme
import tech.lizza.demoxr.viewmodel.EventViewModel

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG, "Iniciando MainActivity")
        
        setContent {
            val spatialConfiguration = LocalSpatialConfiguration.current
            val spatialCapabilities = LocalSpatialCapabilities.current
            
            Log.d(TAG, "Spatial UI habilitado: ${spatialCapabilities.isSpatialUiEnabled}")
            
            if (spatialCapabilities.isSpatialUiEnabled) {
                Subspace {
                    MySpatialContent(
                        onRequestHomeSpaceMode = spatialConfiguration::requestHomeSpaceMode
                    )
                }
            } else {
                val viewModel: EventViewModel = viewModel()
                val isDarkTheme by viewModel.isDarkTheme.collectAsState()
                
                DemoXRTheme(darkTheme = isDarkTheme) {
                    EventNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun MySpatialContent(onRequestHomeSpaceMode: () -> Unit) {
    SpatialPanel(SubspaceModifier.width(1280.dp).height(800.dp).resizable().movable()) {
        MainContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp)
        )
        
        HomeSpaceModeIconButton(
            onClick = onRequestHomeSpaceMode,
            modifier = Modifier
                .size(56.dp)
                .padding(20.dp)
        )
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    Text(text = stringResource(R.string.hello_android_xr), modifier = modifier)
}

@Composable
fun HomeSpaceModeIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalIconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_home_space_mode_switch),
            contentDescription = stringResource(R.string.switch_to_home_space_mode)
        )
    }
}