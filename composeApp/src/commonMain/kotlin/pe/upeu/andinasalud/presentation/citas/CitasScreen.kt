package pe.upeu.andinasalud.presentation.citas

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.presentation.componentes.CitaCard
import pe.upeu.andinasalud.presentation.componentes.EstadoPanel

@Composable
fun CitasScreen(
    estado: CitasUiState,
    onFiltro: (FiltroEstado) -> Unit,
    onBusqueda: (String) -> Unit,
    onDetalle: (Int) -> Unit,
    onReintentar: () -> Unit,
    onDemostrarError: () -> Unit,
) {
    when (estado) {
        CitasUiState.Cargando -> EstadoPanel("Cargando citas", "Espera un momento…", cargando = true)
        is CitasUiState.Error -> EstadoPanel("Error al cargar citas", estado.mensaje, onReintentar = onReintentar)
        is CitasUiState.Contenido, is CitasUiState.Vacia -> {
            val datos = when (estado) {
                is CitasUiState.Contenido -> estado.datos
                is CitasUiState.Vacia -> estado.datos
                else -> error("Estado inesperado")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    OutlinedTextField(
                        value = datos.busqueda,
                        onValueChange = onBusqueda,
                        label = { Text("Buscar especialidad o médico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FiltroEstado.entries.forEach { filtro ->
                            FilterChip(
                                selected = datos.filtro == filtro,
                                onClick = { onFiltro(filtro) },
                                label = { Text(filtro.etiqueta) },
                            )
                        }
                    }
                }
                if (datos.visibles.isEmpty()) {
                    item {
                        EstadoPanel("Sin citas", "No hay citas para este filtro o búsqueda")
                    }
                } else {
                    items(datos.visibles, key = { it.id }) { cita ->
                        CitaCard(cita, onClick = { onDetalle(cita.id) })
                    }
                }
                item {
                    TextButton(onClick = onDemostrarError) { Text("Probar estado de error") }
                }
            }
        }
    }
}

