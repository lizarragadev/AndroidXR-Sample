package tech.lizza.demoxr.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import tech.lizza.demoxr.ui.screens.AdaptiveTalkScreen
import tech.lizza.demoxr.ui.screens.TalkDetailScreen
import tech.lizza.demoxr.viewmodel.EventViewModel

@Composable
fun EventNavigation(
    viewModel: EventViewModel,
    navController: NavHostController = rememberNavController(),
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val selectedTalk by viewModel.selectedTalk.collectAsState()
    val talks by viewModel.talks.collectAsState()
    var isNavigating by remember { mutableStateOf(false) }
    val isLargeScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    LaunchedEffect(isNavigating) {
        if (isNavigating) {
            delay(1000)
            isNavigating = false
        }
    }

    NavHost(
        navController = navController,
        startDestination = "talk_list",
        modifier = modifier
    ) {
        composable("talk_list") {
            AdaptiveTalkScreen(
                viewModel = viewModel,
                selectedTalk = selectedTalk,
                onTalkSelected = { talk ->
                    if (isNavigating) return@AdaptiveTalkScreen
                    
                    viewModel.selectTalk(talk)
                    
                    if (talk != null && !isLargeScreen) {
                        isNavigating = true
                        navController.navigate("talk_detail/${talk.id}")
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
                        viewModel.selectTalk(null)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
