package tech.lizza.demoxr.xr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import tech.lizza.demoxr.data.Speaker

@Composable
fun SpeakerPhotoPanel(
    speaker: Speaker,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(350.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.size(350.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
            shape = CircleShape
        ) {
            val context = LocalContext.current
            val imageModel = if (speaker.imageUrl.startsWith("@drawable/")) {
                context.resources.getIdentifier(
                    speaker.imageUrl.substringAfter("@drawable/"),
                    "drawable",
                    context.packageName
                )
            } else {
                speaker.imageUrl
            }
            
            AsyncImage(
                model = ImageRequest.Builder(context).data(imageModel).build(),
                contentDescription = "Foto de ${speaker.name}",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(id = android.R.drawable.ic_menu_gallery)
            )
        }
        
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.error, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = Color.White
            )
        }
    }
}
