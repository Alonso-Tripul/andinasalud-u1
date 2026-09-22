package pe.upeu.andinasalud.presentation.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.citas.CitasUiState
import pe.upeu.andinasalud.presentation.componentes.CitaCard
import pe.upeu.andinasalud.presentation.componentes.EstadoPanel
import pe.upeu.andinasalud.presentation.componentes.Seccion

@Composable
fun InicioScreen(
    estado: CitasUiState,
    onCitas: () -> Unit,
    onSolicitar: () -> Unit,
    onDetalle: (Int) -> Unit,
    onReintentar: () -> Unit,
) {
    when (estado) {
        CitasUiState.Cargando -> EstadoPanel("Cargando inicio", "Preparando tus citas…", cargando = true)
        is CitasUiState.Error -> EstadoPanel("No se pudo cargar", estado.mensaje, onReintentar = onReintentar)
        is CitasUiState.Contenido, is CitasUiState.Vacia -> {
            val datos = when (estado) {
                is CitasUiState.Contenido -> estado.datos
                is CitasUiState.Vacia -> estado.datos
                else -> error("Estado inesperado")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Hola, ${datos.paciente.nombre.substringBefore(' ')}", style = MaterialTheme.typography.headlineMedium)
                        Text("Tu salud, más cerca de ti", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                item { Seccion("Próxima cita") }
                item {
                    if (datos.proxima == null) Text("No tienes una cita próxima programada")
                    else CitaCard(datos.proxima, onClick = { onDetalle(datos.proxima.id) })
                }
                item { Seccion("Accesos rápidos") }
                item {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onSolicitar, modifier = Modifier.fillMaxWidth()) { Text("Solicitar cita") }
                        OutlinedButton(onClick = onCitas, modifier = Modifier.fillMaxWidth()) { Text("Mis citas") }
                    }
                }
            }
        }
    }
}

