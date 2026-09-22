package pe.upeu.andinasalud.presentation.solicitud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Catalogo
import pe.upeu.andinasalud.domain.usecase.CampoSolicitud
import pe.upeu.andinasalud.presentation.UiState
import pe.upeu.andinasalud.presentation.componentes.EstadoPanel

@Composable
fun SolicitudScreen(
    estado: UiState<Catalogo>,
    formulario: FormularioCita,
    onActualizar: (CampoSolicitud, String) -> Unit,
    onEnviar: () -> Unit,
    onReintentar: () -> Unit,
) {
    when (estado) {
        UiState.Cargando -> EstadoPanel("Cargando formulario", "Buscando sedes y especialidades…", cargando = true)
        UiState.Vacio -> EstadoPanel("Sin opciones", "No hay sedes o especialidades disponibles", onReintentar = onReintentar)
        is UiState.Error -> EstadoPanel("Error al cargar", estado.mensaje, onReintentar = onReintentar)
        is UiState.Contenido -> {
            val catalogo = estado.valor
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item { Text("Nueva cita", style = MaterialTheme.typography.headlineSmall) }
                item {
                    Selector(
                        etiqueta = "Especialidad",
                        valor = catalogo.especialidades.firstOrNull { it.id == formulario.especialidadId }?.nombre ?: "Seleccionar",
                        opciones = catalogo.especialidades.map { it.id to it.nombre },
                        error = formulario.errores[CampoSolicitud.ESPECIALIDAD],
                        onSeleccionar = { onActualizar(CampoSolicitud.ESPECIALIDAD, it) },
                    )
                }
                item {
                    Selector(
                        etiqueta = "Sede",
                        valor = catalogo.sedes.firstOrNull { it.id == formulario.sedeId }?.nombre ?: "Seleccionar",
                        opciones = catalogo.sedes.map { it.id to it.nombre },
                        error = formulario.errores[CampoSolicitud.SEDE],
                        onSeleccionar = { onActualizar(CampoSolicitud.SEDE, it) },
                    )
                }
                item {
                    OutlinedTextField(
                        value = formulario.fecha,
                        onValueChange = { onActualizar(CampoSolicitud.FECHA, it) },
                        label = { Text("Fecha (AAAA-MM-DD)") },
                        isError = formulario.errores.containsKey(CampoSolicitud.FECHA),
                        supportingText = formulario.errores[CampoSolicitud.FECHA]?.let { mensaje -> { Text(mensaje) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    OutlinedTextField(
                        value = formulario.hora,
                        onValueChange = { onActualizar(CampoSolicitud.HORA, it) },
                        label = { Text("Hora (HH:MM)") },
                        isError = formulario.errores.containsKey(CampoSolicitud.HORA),
                        supportingText = formulario.errores[CampoSolicitud.HORA]?.let { mensaje -> { Text(mensaje) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    OutlinedTextField(
                        value = formulario.motivo,
                        onValueChange = { onActualizar(CampoSolicitud.MOTIVO, it) },
                        label = { Text("Motivo de consulta") },
                        isError = formulario.errores.containsKey(CampoSolicitud.MOTIVO),
                        supportingText = formulario.errores[CampoSolicitud.MOTIVO]?.let { mensaje -> { Text(mensaje) } },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (formulario.errorGeneral != null) {
                    item { Text(formulario.errorGeneral, color = MaterialTheme.colorScheme.error) }
                }
                item {
                    Button(
                        onClick = onEnviar,
                        enabled = !formulario.enviando,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(if (formulario.enviando) "Guardando…" else "Confirmar solicitud") }
                }
            }
        }
    }
}

@Composable
private fun Selector(
    etiqueta: String,
    valor: String,
    opciones: List<Pair<String, String>>,
    error: String?,
    onSeleccionar: (String) -> Unit,
) {
    var expandido by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(etiqueta, style = MaterialTheme.typography.labelLarge)
        Box {
            OutlinedButton(onClick = { expandido = true }, modifier = Modifier.fillMaxWidth()) { Text(valor) }
            DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                opciones.forEach { (id, nombre) ->
                    DropdownMenuItem(
                        text = { Text(nombre) },
                        onClick = { expandido = false; onSeleccionar(id) },
                    )
                }
            }
        }
        if (error != null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}

