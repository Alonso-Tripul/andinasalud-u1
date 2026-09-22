package pe.upeu.andinasalud.presentation.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita

@Composable
fun EstadoPanel(
    titulo: String,
    descripcion: String,
    cargando: Boolean = false,
    onReintentar: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (cargando) CircularProgressIndicator()
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Text(descripcion, style = MaterialTheme.typography.bodyMedium)
        if (onReintentar != null) Button(onClick = onReintentar) { Text("Reintentar") }
    }
}

@Composable
fun CitaCard(cita: Cita, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(cita.especialidad.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(etiquetaEstado(cita.estado), color = MaterialTheme.colorScheme.secondary)
            }
            Text(cita.medico.nombre, style = MaterialTheme.typography.bodyMedium)
            Text("${cita.sede.nombre} · ${cita.fecha} · ${cita.hora}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

fun etiquetaEstado(estado: EstadoCita): String = when (estado) {
    is EstadoCita.Programada -> "Programada"
    is EstadoCita.Atendida -> "Atendida"
    is EstadoCita.Cancelada -> "Cancelada"
}

@Composable
fun Seccion(titulo: String, modifier: Modifier = Modifier) {
    Text(titulo, modifier = modifier, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(4.dp))
}

