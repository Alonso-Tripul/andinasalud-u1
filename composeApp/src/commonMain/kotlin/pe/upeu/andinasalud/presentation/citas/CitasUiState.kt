package pe.upeu.andinasalud.presentation.citas

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Paciente

enum class FiltroEstado(val etiqueta: String) {
    TODAS("Todas"), PROGRAMADA("Programadas"), ATENDIDA("Atendidas"), CANCELADA("Canceladas")
}

data class DatosCitas(
    val paciente: Paciente,
    val visibles: List<Cita>,
    val proxima: Cita?,
    val filtro: FiltroEstado,
    val busqueda: String,
    val totalProgramadas: Int,
)

sealed interface CitasUiState {
    data object Cargando : CitasUiState
    data class Contenido(val datos: DatosCitas) : CitasUiState
    data class Vacia(val datos: DatosCitas) : CitasUiState
    data class Error(val mensaje: String) : CitasUiState
}

