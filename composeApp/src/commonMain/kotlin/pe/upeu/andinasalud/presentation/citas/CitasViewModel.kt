package pe.upeu.andinasalud.presentation.citas

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.ResumenCitas
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase

class CitasViewModel(private val obtenerCitas: ObtenerCitasUseCase) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var carga: Job? = null
    private var resumen: ResumenCitas? = null
    private var filtro = FiltroEstado.TODAS
    private var busqueda = ""
    private val _uiState = MutableStateFlow<CitasUiState>(CitasUiState.Cargando)
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        carga?.cancel()
        carga = scope.launch {
            _uiState.value = CitasUiState.Cargando
            try {
                resumen = obtenerCitas()
                publicar()
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _uiState.value = CitasUiState.Error(error.message ?: "No se pudieron cargar las citas")
            }
        }
    }

    fun filtrarPor(estado: FiltroEstado) {
        filtro = estado
        publicar()
    }

    fun buscar(texto: String) {
        busqueda = texto
        publicar()
    }

    // Hace visible el estado de error durante la demostración sin depender de una API.
    fun demostrarError() {
        carga?.cancel()
        carga = scope.launch {
            _uiState.value = CitasUiState.Cargando
            delay(800)
            _uiState.value = CitasUiState.Error("Error simulado de carga. Pulsa Reintentar.")
        }
    }

    private fun publicar() {
        val fuente = resumen ?: return
        val ahora = Clock.System.now()
        val zona = TimeZone.currentSystemDefault()
        val ordenadas = ordenarCitasPorProximidad(fuente.citas, ahora, zona)
        val visibles = ordenadas.filter { cita ->
            val coincideEstado = when (filtro) {
                FiltroEstado.TODAS -> true
                FiltroEstado.PROGRAMADA -> cita.estado is EstadoCita.Programada
                FiltroEstado.ATENDIDA -> cita.estado is EstadoCita.Atendida
                FiltroEstado.CANCELADA -> cita.estado is EstadoCita.Cancelada
            }
            val termino = busqueda.normalizada()
            val coincideBusqueda = termino.isBlank() ||
                cita.especialidad.nombre.normalizada().contains(termino) ||
                cita.medico.nombre.normalizada().contains(termino)
            coincideEstado && coincideBusqueda
        }
        val proxima = ordenadas.firstOrNull {
            it.estado is EstadoCita.Programada &&
                LocalDateTime(it.fecha, it.hora).toInstant(zona) > ahora
        }
        val datos = DatosCitas(
            paciente = fuente.paciente,
            visibles = visibles,
            proxima = proxima,
            filtro = filtro,
            busqueda = busqueda,
            totalProgramadas = fuente.citas.count { it.estado is EstadoCita.Programada },
        )
        _uiState.value = if (visibles.isEmpty()) CitasUiState.Vacia(datos)
        else CitasUiState.Contenido(datos)
    }

    fun cerrar() { scope.cancel() }
}

internal fun ordenarCitasPorProximidad(citas: List<Cita>, ahora: Instant, zona: TimeZone): List<Cita> =
    citas.map { cita -> cita to LocalDateTime(cita.fecha, cita.hora).toInstant(zona) }
        .sortedWith(
            compareBy<Pair<Cita, Instant>> { (_, instante) -> if (instante >= ahora) 0 else 1 }
                .thenBy { (_, instante) ->
                    if (instante >= ahora) (instante - ahora).inWholeSeconds
                    else (ahora - instante).inWholeSeconds
                }
                .thenBy { (cita, _) -> cita.id },
        )
        .map { (cita, _) -> cita }

private fun String.normalizada(): String = lowercase()
    .replace('á', 'a').replace('é', 'e').replace('í', 'i')
    .replace('ó', 'o').replace('ú', 'u').replace('ü', 'u')
    .replace('ñ', 'n')

