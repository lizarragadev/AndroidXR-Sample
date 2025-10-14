package tech.lizza.demoxr.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import tech.lizza.demoxr.R
import tech.lizza.demoxr.data.Talk
import tech.lizza.demoxr.ui.components.TalkCard
import tech.lizza.demoxr.ui.components.TalkDetailContent
import tech.lizza.demoxr.ui.components.DiaSelector
import tech.lizza.demoxr.ui.components.EmptyDetailState
import tech.lizza.demoxr.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveTalkScreen(
    viewModel: EventViewModel,
    selectedTalk: Talk?,
    onTalkSelected: (Talk?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val talks by viewModel.talks.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val showLocationDialog by viewModel.showLocationDialog.collectAsState()
    val showVirtualInfoDialog by viewModel.showVirtualInfoDialog.collectAsState()
    
    val configuration = LocalConfiguration.current
    var isLargeScreen by remember { mutableStateOf(configuration.screenWidthDp >= 840) }
    
    // Actualizar isLargeScreen cuando cambie la configuración
    LaunchedEffect(configuration) {
        isLargeScreen = configuration.screenWidthDp >= 840
        android.util.Log.d("AdaptiveTalkScreen", "Configuration changed: screenWidthDp=${configuration.screenWidthDp}, isLargeScreen=$isLargeScreen")
    }
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "DevFest 2025 Sureste de México",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        // Botón de ubicación presencial
                        IconButton(onClick = { viewModel.showLocationDialog() }) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Ubicación del evento presencial"
                            )
                        }

                        // Botón de información virtual
                        IconButton(onClick = { viewModel.showVirtualInfoDialog() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_live),
                                contentDescription = "Información de eventos virtuales"
                            )
                        }
                        
                        // Botón de tema (Sol/Luna)
                        IconButton(onClick = { viewModel.toggleTheme() }) {
                            Icon(
                                painter = painterResource(
                                    id = if (isDarkTheme) R.drawable.ic_sun else R.drawable.ic_moon
                                ),
                                contentDescription = if (isDarkTheme) "Cambiar a tema claro" else "Cambiar a tema oscuro"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            if (isLargeScreen) {
                // Layout de dos paneles para pantallas grandes
                TwoPaneLayout(
                    talks = talks,
                    selectedDay = selectedDay,
                    isLoading = isLoading,
                    error = error,
                    onTalkSelected = onTalkSelected,
                    selectedTalk = selectedTalk,
                    viewModel = viewModel,
                    paddingValues = paddingValues
                )
            } else {
                // Layout de un solo panel para pantallas pequeñas
                SinglePaneLayout(
                    talks = talks,
                    selectedDay = selectedDay,
                    isLoading = isLoading,
                    error = error,
                    onTalkSelected = onTalkSelected,
                    viewModel = viewModel,
                    paddingValues = paddingValues
                )
            }
        }
        
        // Diálogos fuera del Scaffold para que aparezcan por encima
        // Diálogo de ubicación presencial con overlay
        if (showLocationDialog) {
            // Overlay oscuro de fondo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { viewModel.hideLocationDialog() },
                contentAlignment = Alignment.Center
            ) {
                // Diálogo personalizado
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp)
                        .clickable { /* No hacer nada al hacer clic en el diálogo */ },
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Título
                        Text(
                            text = "📍 Ubicación del Evento Presencial",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Contenido
                        Text(
                            text = "Día 1 - DevFest 2025 Sureste de México",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "🏢 Universidad Tecnológica de Tabasco",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "📍 Carretera Villahermosa - Teapa Km. 14.5",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "🏙️ Villahermosa, Tabasco, México",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "⏰ Horario: 12:00 PM - 4:00 PM",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "📅 Fecha: 15 de Noviembre, 2024",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Mapa embebido
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "🗺️ Ubicación en Google Maps",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Haz clic en el enlace para ver la ubicación exacta:",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "https://maps.app.goo.gl/RnS2jz3FdvSVbn1G9",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.app.goo.gl/RnS2jz3FdvSVbn1G9"))
                                            context.startActivity(intent)
                                        }
                                )
                                Text(
                                    text = "📍 Coordenadas: 17.9875° N, 92.9250° O",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Botón de cerrar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { viewModel.hideLocationDialog() },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Cerrar")
                            }
                        }
                    }
                }
            }
        }

        // Diálogo de información virtual con overlay
        if (showVirtualInfoDialog) {
            // Overlay oscuro de fondo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { viewModel.hideVirtualInfoDialog() },
                contentAlignment = Alignment.Center
            ) {
                // Diálogo personalizado
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp)
                        .clickable { /* No hacer nada al hacer clic en el diálogo */ },
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Título
                        Text(
                            text = "💻 Eventos Virtuales",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Contenido
                        Text(
                            text = "Días 2 y 3 - DevFest 2025 Sureste de México Virtual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "🌐 Plataforma: Google Meet",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "📅 Día 2: 16 de Noviembre, 2024",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "⏰ Horario: 11:00 AM - 3:00 PM",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "📅 Día 3: 17 de Noviembre, 2024",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "⏰ Horario: 11:00 AM - 4:00 PM",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "🔗 Enlaces de acceso se enviarán por email",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Video de YouTube embebido
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "📺 Transmisión en Vivo - YouTube",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Sigue la transmisión en vivo en nuestro canal de YouTube:",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "https://www.youtube.com/@gdgvillahermosa/streams",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@gdgvillahermosa/streams"))
                                            context.startActivity(intent)
                                        }
                                )
                                Text(
                                    text = "🎥 Canal: GDG Villahermosa",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Botón de cerrar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { viewModel.hideVirtualInfoDialog() },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Cerrar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TwoPaneLayout(
    talks: List<Talk>,
    selectedDay: Int,
    isLoading: Boolean,
    error: String?,
    onTalkSelected: (Talk?) -> Unit,
    selectedTalk: Talk?,
    viewModel: EventViewModel,
    paddingValues: PaddingValues
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxWidth()
                .padding(end = 8.dp)
        ) {
            DiaSelector(
                diaSeleccionado = selectedDay,
                onDiaSeleccionado = viewModel::selectDay,
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
                val talksDelDia = viewModel.getTalksByDay(selectedDay)
                if (talksDelDia.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay charlas programadas para el Día $selectedDay",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(talksDelDia) { talk ->
                            val speaker = viewModel.getSpeakerById(talk.speakerId)
                            TalkCard(
                                talk = talk,
                                speakerName = speaker?.name ?: "Speaker not found",
                                onClick = { onTalkSelected(talk) }
                            )
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth()
        ) {
            if (selectedTalk != null) {
                val speaker = viewModel.getSpeakerById(selectedTalk.speakerId)
                TalkDetailContent(
                    talk = selectedTalk,
                    speaker = speaker,
                    onBackClick = { onTalkSelected(null) } // En pantallas grandes, solo deseleccionar
                )
            } else {
                EmptyDetailState()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SinglePaneLayout(
    talks: List<Talk>,
    selectedDay: Int,
    isLoading: Boolean,
    error: String?,
    onTalkSelected: (Talk?) -> Unit,
    viewModel: EventViewModel,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        DiaSelector(
            diaSeleccionado = selectedDay,
            onDiaSeleccionado = viewModel::selectDay,
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
            val talksDelDia = viewModel.getTalksByDay(selectedDay)

            if (talksDelDia.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay charlas programadas para el Día $selectedDay",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(talksDelDia) { talk ->
                        val speaker = viewModel.getSpeakerById(talk.speakerId)
                        TalkCard(
                            talk = talk,
                            speakerName = speaker?.name ?: "Speaker not found",
                            onClick = { onTalkSelected(talk) }
                        )
                    }
                }
            }
        }
    }
}