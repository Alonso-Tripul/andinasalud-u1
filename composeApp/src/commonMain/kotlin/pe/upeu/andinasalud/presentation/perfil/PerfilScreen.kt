package pe.upeu.andinasalud.presentation.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.presentation.UiState
import pe.upeu.andinasalud.presentation.componentes.EstadoPanel

@Composable
fun PerfilScreen(
    estado: UiState<Paciente>,
    oscuro: Boolean,
    onTema: (Boolean) -> Unit,
    onReintentar: () -> Unit,
) {
    when (estado) {
        UiState.Cargando -> EstadoPanel("Cargando perfil", "Preparando tus datos…", cargando = true)
        UiState.Vacio -> EstadoPanel("Sin perfil", "No hay datos del paciente")
        is UiState.Error -> EstadoPanel("Error al cargar perfil", estado.mensaje, onReintentar = onReintentar)
        is UiState.Contenido -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Text("Mi perfil", style = MaterialTheme.typography.headlineSmall) }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DatoPerfil("Nombre", estado.valor.nombre)
                        DatoPerfil("Documento", estado.valor.documento)
                        DatoPerfil("Correo", estado.valor.correo)
                        DatoPerfil("Teléfono", estado.valor.telefono)
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Tema oscuro", style = MaterialTheme.typography.titleMedium)
                    Switch(checked = oscuro, onCheckedChange = onTema)
                }
            }
        }
    }
}

@Composable
private fun DatoPerfil(etiqueta: String, valor: String) {
    Column {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
