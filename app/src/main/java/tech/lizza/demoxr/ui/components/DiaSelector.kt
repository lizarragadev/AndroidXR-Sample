package tech.lizza.demoxr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaSelector(
    diaSeleccionado: Int,
    onDiaSeleccionado: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Título del selector
            Text(
                text = "Selecciona el día del evento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Botones de días con información
            SingleChoiceSegmentedButtonRow {
                (1..3).forEach { dia ->
                    val tipoEvento = when (dia) {
                        1 -> "Presencial"
                        else -> "Virtual"
                    }
                    
                    val isPresencial = dia == 1
                    val isSelected = diaSeleccionado == dia
                    
                    // Colores personalizados para presencial vs virtual
                    val backgroundColor = when {
                        isSelected && isPresencial -> androidx.compose.ui.graphics.Color(0xFF2E7D32) // Verde oscuro
                        isSelected && !isPresencial -> androidx.compose.ui.graphics.Color(0xFF1976D2) // Azul oscuro
                        !isSelected && isPresencial -> androidx.compose.ui.graphics.Color(0xFFE8F5E8) // Verde claro
                        else -> androidx.compose.ui.graphics.Color(0xFFE3F2FD) // Azul claro
                    }
                    
                    val textColor = when {
                        isSelected -> androidx.compose.ui.graphics.Color.White
                        isPresencial -> androidx.compose.ui.graphics.Color(0xFF1B5E20) // Verde oscuro
                        else -> androidx.compose.ui.graphics.Color(0xFF0D47A1) // Azul oscuro
                    }
                    
                    val tipoColor = when {
                        isSelected -> androidx.compose.ui.graphics.Color.White
                        isPresencial -> androidx.compose.ui.graphics.Color(0xFF2E7D32) // Verde
                        else -> androidx.compose.ui.graphics.Color(0xFF1976D2) // Azul
                    }
                    
                    SegmentedButton(
                        onClick = { onDiaSeleccionado(dia) },
                        selected = isSelected,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        shape = SegmentedButtonDefaults.itemShape(
                            index = dia - 1,
                            count = 3
                        ),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = backgroundColor,
                            inactiveContainerColor = backgroundColor,
                            activeContentColor = textColor,
                            inactiveContentColor = textColor
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Día $dia",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = textColor
                            )
                            Text(
                                text = tipoEvento,
                                style = MaterialTheme.typography.bodySmall,
                                color = tipoColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            // Información adicional con colores
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Indicador presencial
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = "Presencial",
                        style = MaterialTheme.typography.bodySmall,
                        color = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Indicador virtual
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = androidx.compose.ui.graphics.Color(0xFF1976D2),
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = "Virtual",
                        style = MaterialTheme.typography.bodySmall,
                        color = androidx.compose.ui.graphics.Color(0xFF1976D2),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
