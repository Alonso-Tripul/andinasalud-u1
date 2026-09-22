package pe.upeu.andinasalud.domain.usecase

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toInstant
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.repository.Reloj

enum class CampoSolicitud { ESPECIALIDAD, SEDE, FECHA, HORA, MOTIVO }

data class SolicitudCita(
    val especialidadId: String,
    val sedeId: String,
    val fecha: String,
    val hora: String,
    val motivo: String,
)

sealed interface ResultadoSolicitud {
    data class Creada(val cita: Cita) : ResultadoSolicitud
    data class Invalida(
        val errores: Map<CampoSolicitud, String>,
        val errorGeneral: String? = null,
    ) : ResultadoSolicitud
}

class SolicitarCitaUseCase(
    private val repositorio: CitaRepository,
    private val reloj: Reloj,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(solicitud: SolicitudCita): ResultadoSolicitud = mutex.withLock {
        val catalogo = repositorio.obtenerCatalogo()
        val errores = mutableMapOf<CampoSolicitud, String>()
        val especialidad = catalogo.especialidades.firstOrNull { it.id == solicitud.especialidadId }
            ?: run { errores[CampoSolicitud.ESPECIALIDAD] = "Selecciona una especialidad"; null }
        val sede = catalogo.sedes.firstOrNull { it.id == solicitud.sedeId }
            ?: run { errores[CampoSolicitud.SEDE] = "Selecciona una sede"; null }
        val fecha = runCatching { LocalDate.parse(solicitud.fecha.trim()) }.getOrNull()
            ?: run { errores[CampoSolicitud.FECHA] = "Usa una fecha válida: AAAA-MM-DD"; null }
        val hora = runCatching { LocalTime.parse(solicitud.hora.trim()) }.getOrNull()
            ?: run { errores[CampoSolicitud.HORA] = "Usa una hora válida: HH:MM"; null }
        val motivo = solicitud.motivo.trim()
        if (motivo.length !in 10..200) {
            errores[CampoSolicitud.MOTIVO] = "El motivo debe tener entre 10 y 200 caracteres"
        }
        if (fecha != null && hora != null &&
            LocalDateTime(fecha, hora).toInstant(reloj.zona()) <= reloj.ahora()
        ) {
            errores[CampoSolicitud.FECHA] = "La cita debe ser posterior al momento actual"
            errores[CampoSolicitud.HORA] = "Elige una fecha y hora futuras"
        }
        if (errores.isNotEmpty()) return@withLock ResultadoSolicitud.Invalida(errores)

        val paciente = repositorio.obtenerPaciente()
        val programadas = repositorio.obtenerCitas().filter {
            it.pacienteId == paciente.id && it.estado is EstadoCita.Programada
        }
        if (programadas.size >= 3) {
            return@withLock ResultadoSolicitud.Invalida(
                emptyMap(), "Ya tienes tres citas programadas; cancela una antes de solicitar otra",
            )
        }
        if (programadas.any { it.fecha == fecha && it.hora == hora }) {
            return@withLock ResultadoSolicitud.Invalida(
                mapOf(CampoSolicitud.HORA to "Ya tienes una cita programada ese día y a esa hora"),
            )
        }
        val medico = catalogo.medicos.firstOrNull {
            it.especialidadId == especialidad!!.id && sede!!.id in it.sedeIds
        } ?: return@withLock ResultadoSolicitud.Invalida(
            mapOf(CampoSolicitud.SEDE to "No hay médico de esa especialidad en esta sede"),
        )
        val cita = repositorio.guardarCita(
            Cita(
                id = 0,
                pacienteId = paciente.id,
                especialidad = especialidad!!,
                medico = medico,
                sede = sede!!,
                fecha = fecha!!,
                hora = hora!!,
                motivo = motivo,
                estado = EstadoCita.Programada(recordatorioActivo = true),
            ),
        )
        ResultadoSolicitud.Creada(cita)
    }
}

class PuedeSolicitarCitaUseCase(private val repositorio: CitaRepository) {
    suspend operator fun invoke(): Boolean {
        val pacienteId = repositorio.obtenerPaciente().id
        return repositorio.obtenerCitas().count {
            it.pacienteId == pacienteId && it.estado is EstadoCita.Programada
        } < 3
    }
}

