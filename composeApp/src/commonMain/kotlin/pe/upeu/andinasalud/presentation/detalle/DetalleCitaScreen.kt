package pe.upeu.andinasalud.presentation.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.presentation.UiState
import pe.upeu.andinasalud.presentation.componentes.EstadoPanel
import pe.upeu.andinasalud.presentation.componentes.etiquetaEstado

@Composable
fun DetalleCitaScreen(
    estado: UiState<pe.upeu.andinasalud.domain.model.Cita>,
    mensaje: String?,
    onCancelar: (Int) -> Unit,
    onReintentar: () -> Unit,
    onLimpiarMensaje: () -> Unit,
) {
    when (estado) {
        UiState.Cargando -> EstadoPanel("Cargando detalle", "Consultando la cita…", cargando = true)
        UiState.Vacio -> EstadoPanel("Cita no encontrada", "La cita ya no está disponible")
        is UiState.Error -> EstadoPanel("Error al cargar", estado.mensaje, onReintentar = onReintentar)
        is UiState.Contenido -> {
            val cita = estado.valor
            var confirmar by remember(cita.id) { mutableStateOf(false) }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Text(cita.especialidad.nombre, style = MaterialTheme.typography.headlineSmall)
                    Text(etiquetaEstado(cita.estado), color = MaterialTheme.colorScheme.secondary)
                }
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            DatoDetalle("Médico", cita.medico.nombre)
                            DatoDetalle("Sede", cita.sede.nombre)
                            DatoDetalle("Fecha", cita.fecha.toString())
                            DatoDetalle("Hora", cita.hora.toString())
                            DatoDetalle("Motivo", cita.motivo)
                            when (val citaEstado = cita.estado) {
                                is EstadoCita.Programada -> DatoDetalle("Indicaciones", if (citaEstado.recordatorioActivo) "Recordatorio activo" else "Sin recordatorio")
                                is EstadoCita.Atendida -> DatoDetalle("Indicaciones", citaEstado.indicaciones)
                                is EstadoCita.Cancelada -> DatoDetalle("Motivo de cancelación", citaEstado.motivo)
                            }
                        }
                    }
                }
                if (cita.estado is EstadoCita.Programada) {
                    item {
                        OutlinedButton(onClick = { confirmar = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Cancelar cita")
                        }
                    }
                }
                if (mensaje != null) {
                    item {
                        Text(mensaje, color = MaterialTheme.colorScheme.primary)
                        Button(onClick = onLimpiarMensaje) { Text("Entendido") }
                    }
                }
            }
            if (confirmar) {
                AlertDialog(
                    onDismissRequest = { confirmar = false },
                    title = { Text("¿Cancelar esta cita?") },
                    text = { Text("Solo se permite cancelar una cita programada con más de 24 horas de anticipación.") },
                    confirmButton = {
                        Button(onClick = { confirmar = false; onCancelar(cita.id) }) { Text("Confirmar") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { confirmar = false }) { Text("Volver") }
                    },
                )
            }
        }
    }
}

@Composable
private fun DatoDetalle(etiqueta: String, valor: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
