package pe.upeu.andinasalud.presentation.solicitud

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Catalogo
import pe.upeu.andinasalud.domain.usecase.CampoSolicitud
import pe.upeu.andinasalud.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.andinasalud.domain.usecase.ResultadoSolicitud
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitudCita
import pe.upeu.andinasalud.presentation.UiState

data class FormularioCita(
    val especialidadId: String = "",
    val sedeId: String = "",
    val fecha: String = "",
    val hora: String = "",
    val motivo: String = "",
    val errores: Map<CampoSolicitud, String> = emptyMap(),
    val errorGeneral: String? = null,
    val enviando: Boolean = false,
)

class SolicitudViewModel(
    private val obtenerCatalogo: ObtenerCatalogoUseCase,
    private val solicitarCita: SolicitarCitaUseCase,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _uiState = MutableStateFlow<UiState<Catalogo>>(UiState.Cargando)
    val uiState: StateFlow<UiState<Catalogo>> = _uiState.asStateFlow()
    private val _formulario = MutableStateFlow(FormularioCita())
    val formulario: StateFlow<FormularioCita> = _formulario.asStateFlow()

    init { cargar() }

    fun cargar() {
        scope.launch {
            _uiState.value = UiState.Cargando
            try {
                val catalogo = obtenerCatalogo()
                _uiState.value = if (catalogo.especialidades.isEmpty() || catalogo.sedes.isEmpty())
                    UiState.Vacio else UiState.Contenido(catalogo)
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _uiState.value = UiState.Error(error.message ?: "No se cargó el formulario")
            }
        }
    }

    fun actualizar(campo: CampoSolicitud, valor: String) {
        val actual = _formulario.value
        _formulario.value = when (campo) {
            CampoSolicitud.ESPECIALIDAD -> actual.copy(especialidadId = valor)
            CampoSolicitud.SEDE -> actual.copy(sedeId = valor)
            CampoSolicitud.FECHA -> actual.copy(fecha = valor)
            CampoSolicitud.HORA -> actual.copy(hora = valor)
            CampoSolicitud.MOTIVO -> actual.copy(motivo = valor)
        }.copy(errores = actual.errores - campo, errorGeneral = null)
    }

    fun enviar(alCrear: (Int) -> Unit) {
        if (_formulario.value.enviando) return
        scope.launch {
            val actual = _formulario.value
            _formulario.value = actual.copy(enviando = true, errores = emptyMap(), errorGeneral = null)
            try {
                when (val resultado = solicitarCita(
                    SolicitudCita(actual.especialidadId, actual.sedeId, actual.fecha, actual.hora, actual.motivo),
                )) {
                    is ResultadoSolicitud.Creada -> alCrear(resultado.cita.id)
                    is ResultadoSolicitud.Invalida -> _formulario.value = actual.copy(
                        errores = resultado.errores,
                        errorGeneral = resultado.errorGeneral,
                    )
                }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _formulario.value = actual.copy(errorGeneral = error.message ?: "No se pudo solicitar la cita")
            } finally {
                _formulario.value = _formulario.value.copy(enviando = false)
            }
        }
    }

    fun cerrar() { scope.cancel() }
}
