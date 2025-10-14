package tech.lizza.demoxr.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.delay
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tech.lizza.demoxr.data.Talk
import tech.lizza.demoxr.ui.screens.AdaptiveTalkScreen
import tech.lizza.demoxr.ui.screens.TalkDetailScreen
import tech.lizza.demoxr.viewmodel.EventViewModel

@Composable
fun EventNavigation(
    viewModel: EventViewModel,
    navController: NavHostController = rememberNavController()
) {
    val selectedTalk by viewModel.selectedTalk.collectAsState()
    val talks by viewModel.talks.collectAsState()
    val configuration = LocalConfiguration.current
    var isLargeScreen by remember { mutableStateOf(configuration.screenWidthDp >= 840) }
    var isNavigating by remember { mutableStateOf(false) }
    
    // Actualizar isLargeScreen cuando cambie la configuración
    LaunchedEffect(configuration) {
        val newIsLargeScreen = configuration.screenWidthDp >= 840
        Log.d("EventNavigation", "Configuration changed: screenWidthDp=${configuration.screenWidthDp}, isLargeScreen=$isLargeScreen -> $newIsLargeScreen")
        
        isLargeScreen = newIsLargeScreen
    }
    
    // Resetear flag de navegación después de un delay
    LaunchedEffect(isNavigating) {
        if (isNavigating) {
            delay(1000)
            isNavigating = false
            Log.d("EventNavigation", "Navigation flag reset")
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = "talk_list"
    ) {
        composable("talk_list") {
            AdaptiveTalkScreen(
                viewModel = viewModel,
                selectedTalk = selectedTalk,
                onTalkSelected = { talk ->
                    Log.d("EventNavigation", "Talk selected: ${talk?.title}")
                    Log.d("EventNavigation", "Current screen size: ${configuration.screenWidthDp}dp, isLargeScreen: $isLargeScreen")
                    Log.d("EventNavigation", "Current destination: ${navController.currentDestination?.route}")
                    
                    try {
                        if (isNavigating) {
                            Log.d("EventNavigation", "Already navigating, ignoring click")
                            return@AdaptiveTalkScreen
                        }
                        
                        viewModel.selectTalk(talk)

                        // Solo navegar en pantallas pequeñas
                        if (talk != null && !isLargeScreen) {
                            Log.d("EventNavigation", "Navigating to detail (small screen): ${talk.id}")
                            isNavigating = true
                            navController.navigate("talk_detail/${talk.id}")
                        } else if (talk != null && isLargeScreen) {
                            Log.d("EventNavigation", "Showing detail in right panel (large screen)")
                        } else if (talk == null) {
                            Log.d("EventNavigation", "Talk deselected")
                        }
                    } catch (e: Exception) {
                        Log.e("EventNavigation", "Error in onTalkSelected: ${e.message}", e)
                        isNavigating = false
                    }
                }
            )
        }

        composable("talk_detail/{talkId}") { backStackEntry ->
            val talkId = backStackEntry.arguments?.getString("talkId") ?: ""
            val talk = talks.find { it.id == talkId }
            val speaker = talk?.let { viewModel.getSpeakerById(it.speakerId) }

            if (talk != null) {
                TalkDetailScreen(
                    talk = talk,
                    speaker = speaker,
                    onBackClick = {
                        Log.d("EventNavigation", "Back button clicked, going back to list")
                        viewModel.selectTalk(null)
                        navController.popBackStack()
                    }
                )
            } else {
                Log.e("EventNavigation", "Talk not found for ID: $talkId")
            }
        }
    }
}
