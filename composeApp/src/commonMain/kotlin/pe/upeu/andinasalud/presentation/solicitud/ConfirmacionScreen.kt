package pe.upeu.andinasalud.presentation.solicitud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmacionScreen(id: Int, onDetalle: () -> Unit, onCitas: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Solicitud registrada", style = MaterialTheme.typography.headlineMedium)
        Text("Tu cita N.º $id ya figura en Mis citas", modifier = Modifier.padding(vertical = 16.dp))
        Button(onClick = onDetalle, modifier = Modifier.fillMaxWidth()) { Text("Ver detalle") }
        OutlinedButton(onClick = onCitas, modifier = Modifier.fillMaxWidth()) { Text("Ir a Mis citas") }
    }
}

