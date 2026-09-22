package pe.upeu.andinasalud.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.core.context.GlobalContext
import pe.upeu.andinasalud.presentation.citas.CitasScreen
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaScreen
import pe.upeu.andinasalud.presentation.detalle.DetalleCitaViewModel
import pe.upeu.andinasalud.presentation.inicio.InicioScreen
import pe.upeu.andinasalud.presentation.perfil.PerfilScreen
import pe.upeu.andinasalud.presentation.perfil.PerfilViewModel
import pe.upeu.andinasalud.presentation.solicitud.ConfirmacionScreen
import pe.upeu.andinasalud.presentation.solicitud.SolicitudScreen
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel

@Composable
fun AppNavHost(oscuro: Boolean, onTema: (Boolean) -> Unit) {
    val citasVm = remember { GlobalContext.get().get<CitasViewModel>() }
    val citasEstado by citasVm.uiState.collectAsState()
    DisposableEffect(citasVm) { onDispose { citasVm.cerrar() } }

    val pila = remember { mutableStateListOf<Destino>(Destino.Inicio) }
    val actual = pila.last()
    fun ir(destino: Destino) { pila.add(destino) }
    fun pestaña(destino: Destino) { pila.clear(); pila.add(destino) }
    fun volver() { if (pila.size > 1) pila.removeAt(pila.lastIndex) }
    PlatformBackHandler(enabled = pila.size > 1, onBack = ::volver)

    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (pila.size > 1) IconButton(onClick = ::volver) { Text("‹", style = MaterialTheme.typography.headlineMedium) }
                    Text(
                        when (actual) {
                            Destino.Inicio -> "AndinaSalud"
                            Destino.Citas -> "Mis citas"
                            Destino.Perfil -> "Perfil y ajustes"
                            Destino.Solicitud -> "Solicitar cita"
                            is Destino.Detalle -> "Detalle de cita"
                            is Destino.Confirmacion -> "Confirmación"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar {
                listOf(Triple(Destino.Inicio, "Inicio", "⌂"), Triple(Destino.Citas, "Citas", "▦"), Triple(Destino.Perfil, "Perfil", "◉"))
                    .forEach { (destino, etiqueta, simbolo) ->
                        NavigationBarItem(
                            selected = actual == destino,
                            onClick = { pestaña(destino) },
                            icon = { Text(simbolo) },
                            label = { Text(etiqueta) },
                        )
                    }
            }
        },
    ) { relleno ->
        Box(Modifier.fillMaxSize().padding(relleno)) {
            when (actual) {
                Destino.Inicio -> InicioScreen(
                    estado = citasEstado,
                    onCitas = { pestaña(Destino.Citas) },
                    onSolicitar = { ir(Destino.Solicitud) },
                    onDetalle = { ir(Destino.Detalle(it)) },
                    onReintentar = citasVm::cargar,
                )
                Destino.Citas -> CitasScreen(
                    estado = citasEstado,
                    onFiltro = citasVm::filtrarPor,
                    onBusqueda = citasVm::buscar,
                    onDetalle = { ir(Destino.Detalle(it)) },
                    onReintentar = citasVm::cargar,
                    onDemostrarError = citasVm::demostrarError,
                )
                Destino.Perfil -> PerfilRuta(oscuro, onTema)
                Destino.Solicitud -> SolicitudRuta(
                    alCrear = { id -> citasVm.cargar(); ir(Destino.Confirmacion(id)) },
                )
                is Destino.Detalle -> DetalleRuta(actual.id, alCancelar = citasVm::cargar)
                is Destino.Confirmacion -> ConfirmacionScreen(
                    id = actual.id,
                    onDetalle = { ir(Destino.Detalle(actual.id)) },
                    onCitas = { pestaña(Destino.Citas) },
                )
            }
        }
    }
}

@Composable
private fun DetalleRuta(id: Int, alCancelar: () -> Unit) {
    val vm = remember(id) { GlobalContext.get().get<DetalleCitaViewModel>() }
    val estado by vm.uiState.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    LaunchedEffect(vm, id) { vm.cargar(id) }
    DisposableEffect(vm) { onDispose { vm.cerrar() } }
    DetalleCitaScreen(
        estado = estado,
        mensaje = mensaje,
        onCancelar = { vm.cancelar(it, alCancelar) },
        onReintentar = { vm.cargar(id) },
        onLimpiarMensaje = vm::limpiarMensaje,
    )
}

@Composable
private fun SolicitudRuta(alCrear: (Int) -> Unit) {
    val vm = remember { GlobalContext.get().get<SolicitudViewModel>() }
    val estado by vm.uiState.collectAsState()
    val formulario by vm.formulario.collectAsState()
    DisposableEffect(vm) { onDispose { vm.cerrar() } }
    SolicitudScreen(
        estado = estado,
        formulario = formulario,
        onActualizar = vm::actualizar,
        onEnviar = { vm.enviar(alCrear) },
        onReintentar = vm::cargar,
    )
}

@Composable
private fun PerfilRuta(oscuro: Boolean, onTema: (Boolean) -> Unit) {
    val vm = remember { GlobalContext.get().get<PerfilViewModel>() }
    val estado by vm.uiState.collectAsState()
    DisposableEffect(vm) { onDispose { vm.cerrar() } }
    PerfilScreen(estado, oscuro, onTema, vm::cargar)
}
