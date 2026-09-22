package pe.upeu.andinasalud.domain.usecase

import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.repository.Reloj

sealed interface ResultadoCancelacion {
    data class Cancelada(val cita: Cita) : ResultadoCancelacion
    data class Rechazada(val motivo: String) : ResultadoCancelacion
}

class CancelarCitaUseCase(
    private val repositorio: CitaRepository,
    private val reloj: Reloj,
) {
    suspend operator fun invoke(id: Int): ResultadoCancelacion {
        val cita = repositorio.obtenerCitas().firstOrNull { it.id == id }
            ?: return ResultadoCancelacion.Rechazada("La cita no existe")
        if (cita.estado !is EstadoCita.Programada) {
            return ResultadoCancelacion.Rechazada("Solo se puede cancelar una cita programada")
        }
        val faltante = LocalDateTime(cita.fecha, cita.hora).toInstant(reloj.zona()) - reloj.ahora()
        if (faltante <= 24.hours) {
            return ResultadoCancelacion.Rechazada("Se requieren más de 24 horas de anticipación")
        }
        val cancelada = cita.copy(
            estado = EstadoCita.Cancelada("Cancelada por el paciente", true),
        )
        repositorio.actualizarCita(cancelada)
        return ResultadoCancelacion.Cancelada(cancelada)
    }
}

