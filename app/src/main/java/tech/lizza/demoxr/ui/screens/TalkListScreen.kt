package tech.lizza.demoxr.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.lizza.demoxr.data.Talk
import tech.lizza.demoxr.ui.components.TalkCard
import tech.lizza.demoxr.ui.components.TabSelector
import tech.lizza.demoxr.ui.components.TabType
import tech.lizza.demoxr.ui.components.ExpositorCard
import tech.lizza.demoxr.ui.components.SponsorCard
import tech.lizza.demoxr.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalkListScreen(
    viewModel: EventViewModel,
    onTalkClick: (Talk) -> Unit,
    modifier: Modifier = Modifier
) {
    val talks by viewModel.talks.collectAsState()
    val speakers by viewModel.speakers.collectAsState()
    val sponsors by viewModel.sponsors.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DevFest El Alto 2025",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabSelector(
                selectedTab = selectedTab,
                onTabSelected = viewModel::selectTab,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    when (selectedTab) {
                        TabType.EXPOSITORES -> {
                            items(speakers) { speaker ->
                                val talk = viewModel.getTalksBySpeaker(speaker.id)
                                ExpositorCard(
                                    speaker = speaker,
                                    talk = talk,
                                    onClick = { talk?.let { onTalkClick(it) } }
                                )
                            }
                        }
                        TabType.SPONSORS -> {
                            items(sponsors) { sponsor ->
                                SponsorCard(sponsor = sponsor)
                            }
                        }
                    }
                }
            }
        }
    }
}