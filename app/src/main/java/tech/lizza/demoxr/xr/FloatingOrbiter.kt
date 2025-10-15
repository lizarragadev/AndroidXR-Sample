package tech.lizza.demoxr.xr

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import tech.lizza.demoxr.data.Speaker

/**
 * Orbiter flotante independiente que puede moverse libremente por la pantalla
 */
@Composable
fun FloatingSpeakerOrbiter(
    speaker: Speaker?,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    onClose: () -> Unit = {},
    onMove: (Float, Float) -> Unit = { _, _ -> }
) {
    if (!isVisible || speaker == null) return

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightDp = configuration.screenHeightDp.toFloat()
    
    // Calcular posición inicial un poquito más abajo
    val initialY = screenHeightDp - 240f
    
    var isExpanded by remember { mutableStateOf(false) }
    var positionX by remember { mutableStateOf(700f) }
    var positionY by remember { mutableStateOf(initialY) }
    var isDragging by remember { mutableStateOf(false) }
    
    // Calcular si el Orbiter está fuera de la pantalla
    val isOutOfScreen = positionX < 0 || positionY < 0 || positionX > 800 || positionY > 1200

    // Animación de entrada
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "orbiter_alpha"
    )

    // Animación de expansión
    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 1.1f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "orbiter_scale"
    )

    LaunchedEffect(speaker) {
        isExpanded = true
    }

    Box(
        modifier = modifier
            .offset(x = positionX.dp, y = positionY.dp)
            .alpha(alpha)
            .scale(if (isDragging) 1.05f else scale)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { 
                        isDragging = true
                        isExpanded = true
                    },
                    onDragEnd = { 
                        isDragging = false
                    },
                    onDrag = { change, _ ->
                        // Permitir movimiento completamente libre - sin límites
                        positionX += change.position.x / density.density
                        positionY += change.position.y / density.density
                        onMove(positionX, positionY)
                    }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .width(if (isExpanded) 300.dp else 200.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header con icono de arrastre y botón de cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Arrastrar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "👤 Speaker Flotante",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Información del speaker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Foto del speaker
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        if (speaker.imageUrl.startsWith("@drawable/")) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Speaker photo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(speaker.imageUrl)
                                    .build(),
                                contentDescription = "Photo of ${speaker.name}",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = speaker.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = speaker.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Información de la empresa
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Company",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = speaker.company,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Biografía resumida
                    Text(
                        text = speaker.biography.take(150) + if (speaker.biography.length > 150) "..." else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        // Indicador visual cuando está fuera de pantalla
        if (isOutOfScreen) {
            val indicatorX = when {
                positionX < 0 -> 0f
                positionX > 800 -> 800f
                else -> positionX
            }
            val indicatorY = when {
                positionY < 0 -> 0f
                positionY > 1200 -> 1200f
                else -> positionY
            }
            
            Box(
                modifier = Modifier
                    .offset(x = indicatorX.dp, y = indicatorY.dp)
                    .size(12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Orbiter flotante para información de charla
 */
@Composable
fun FloatingTalkOrbiter(
    talkTitle: String,
    time: String,
    room: String,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    onClose: () -> Unit = {},
    onMove: (Float, Float) -> Unit = { _, _ -> }
) {
    if (!isVisible) return

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightDp = configuration.screenHeightDp.toFloat()
    
    // Calcular posición inicial un poco más abajo que el speaker
    val initialY = screenHeightDp - 150f
    
    var positionX by remember { mutableStateOf(420f) }
    var positionY by remember { mutableStateOf(initialY) }
    var isExpanded by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }
    
    // Calcular si el Orbiter está fuera de la pantalla
    val isOutOfScreen = positionX < 0 || positionY < 0 || positionX > 800 || positionY > 1200

    // Animación de entrada
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "talk_orbiter_alpha"
    )

    LaunchedEffect(talkTitle) {
        isExpanded = true
    }

    Box(
        modifier = modifier
            .offset(x = positionX.dp, y = positionY.dp)
            .alpha(alpha)
            .scale(if (isDragging) 1.05f else 1f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { 
                        isDragging = true
                        isExpanded = true
                    },
                    onDragEnd = { 
                        isDragging = false
                    },
                    onDrag = { change, _ ->
                        // Permitir movimiento completamente libre - sin límites
                        positionX += change.position.x / density.density
                        positionY += change.position.y / density.density
                        onMove(positionX, positionY)
                    }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .width(if (isExpanded) 280.dp else 200.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header con icono de arrastre y botón de cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Arrastrar",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "📅 Charla Flotante",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = talkTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⏰ $time",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "📍 $room",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Indicador visual cuando está fuera de pantalla
        if (isOutOfScreen) {
            val indicatorX = when {
                positionX < 0 -> 0f
                positionX > 800 -> 800f
                else -> positionX
            }
            val indicatorY = when {
                positionY < 0 -> 0f
                positionY > 1200 -> 1200f
                else -> positionY
            }
            
            Box(
                modifier = Modifier
                    .offset(x = indicatorX.dp, y = indicatorY.dp)
                    .size(12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    )
            )
        }
    }
}
