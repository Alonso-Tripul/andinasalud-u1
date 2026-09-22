package pe.upeu.andinasalud.presentation.detalle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerDetalleCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ResultadoCancelacion
import pe.upeu.andinasalud.presentation.UiState

class DetalleCitaViewModel(
    private val obtenerDetalle: ObtenerDetalleCitaUseCase,
    private val cancelarCita: CancelarCitaUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var carga: Job? = null
    private val _uiState = MutableStateFlow<UiState<Cita>>(UiState.Cargando)
    val uiState: StateFlow<UiState<Cita>> = _uiState.asStateFlow()
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun cargar(id: Int) {
        carga?.cancel()
        carga = scope.launch {
            _uiState.value = UiState.Cargando
            try {
                val cita = obtenerDetalle(id)
                _uiState.value = if (cita == null) UiState.Vacio else UiState.Contenido(cita)
            } catch (error: Exception) {
                _uiState.value = UiState.Error(error.message ?: "No se pudo cargar la cita")
            }
        }
    }

    fun cancelar(id: Int, alCancelar: () -> Unit) {
        scope.launch {
            when (val resultado = cancelarCita(id)) {
                is ResultadoCancelacion.Cancelada -> {
                    _uiState.value = UiState.Contenido(resultado.cita)
                    _mensaje.value = "Cita cancelada correctamente"
                    alCancelar()
                }
                is ResultadoCancelacion.Rechazada -> _mensaje.value = resultado.motivo
            }
        }
    }

    fun limpiarMensaje() { _mensaje.value = null }
    fun cerrar() { scope.cancel() }
}

