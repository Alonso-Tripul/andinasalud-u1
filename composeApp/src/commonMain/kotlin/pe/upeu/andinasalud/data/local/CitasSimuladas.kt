package pe.upeu.andinasalud.data.local

import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.upeu.andinasalud.domain.model.Catalogo
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Especialidad
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.model.Sede

class CitasSimuladas {
    val paciente = Paciente(
        id = "P-0417",
        nombre = "Rony",
        documento = "70154823",
        correo = "rony@correo.pe",
        telefono = "987 654 321",
    )

    private val sedes = listOf(
        Sede("nana", "Ñaña"),
        Sede("chosica", "Chosica"),
        Sede("chaclacayo", "Chaclacayo"),
        Sede("santa-anita", "Santa Anita"),
    )
    private val especialidades = listOf(
        Especialidad("general", "Medicina General"),
        Especialidad("odontologia", "Odontología"),
        Especialidad("pediatria", "Pediatría"),
        Especialidad("nutricion", "Nutrición"),
        Especialidad("psicologia", "Psicología"),
    )
    private val medicos = listOf(
        Medico("M01", "Dr. Iván Rojas", "general", setOf("nana", "chosica")),
        Medico("M02", "Dra. Elena Chávez", "general", setOf("chaclacayo", "santa-anita")),
        Medico("M03", "Dra. Rosa Flores", "odontologia", setOf("chosica", "nana")),
        Medico("M04", "Dr. Mario Salas", "odontologia", setOf("santa-anita", "chaclacayo")),
        Medico("M05", "Dra. Carla Núñez", "pediatria", setOf("chaclacayo", "nana")),
        Medico("M06", "Dr. Pablo Vega", "pediatria", setOf("chosica", "santa-anita")),
        Medico("M07", "Lic. Ana Bermúdez", "nutricion", setOf("santa-anita", "chosica")),
        Medico("M08", "Lic. Sofía Díaz", "nutricion", setOf("nana", "chaclacayo")),
        Medico("M09", "Ps. Luis Tapia", "psicologia", setOf("nana", "chosica")),
        Medico("M10", "Ps. Julia Medina", "psicologia", setOf("chaclacayo", "santa-anita")),
    )
    val catalogo = Catalogo(especialidades, sedes, medicos)

    private fun fechaEn(dias: Int): LocalDate =
        (Clock.System.now() + dias.days)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date

    private fun cita(
        id: Int,
        especialidadId: String,
        medicoId: String,
        sedeId: String,
        dias: Int,
        hora: String,
        estado: EstadoCita,
    ) = Cita(
        id = id,
        pacienteId = paciente.id,
        especialidad = especialidades.first { it.id == especialidadId },
        medico = medicos.first { it.id == medicoId },
        sede = sedes.first { it.id == sedeId },
        fecha = fechaEn(dias),
        hora = LocalTime.parse(hora),
        motivo = "Consulta de seguimiento",
        estado = estado,
    )

    val citas = listOf(
        cita(1, "general", "M01", "nana", 2, "09:00", EstadoCita.Programada(true)),
        cita(2, "odontologia", "M03", "chosica", 4, "16:30", EstadoCita.Programada(false)),
        cita(3, "nutricion", "M07", "santa-anita", 7, "11:15", EstadoCita.Programada(true)),
        cita(4, "pediatria", "M05", "chaclacayo", -21, "08:45", EstadoCita.Atendida("Control en tres meses")),
        cita(5, "psicologia", "M09", "nana", -12, "15:00", EstadoCita.Atendida("Continuar sesiones quincenales")),
        cita(6, "general", "M01", "chosica", -5, "10:30", EstadoCita.Cancelada("Viaje del paciente", true)),
    )
}

