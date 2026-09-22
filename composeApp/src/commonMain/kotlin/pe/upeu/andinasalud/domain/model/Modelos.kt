package pe.upeu.andinasalud.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class Paciente(
    val id: String,
    val nombre: String,
    val documento: String,
    val correo: String,
    val telefono: String,
)

data class Sede(val id: String, val nombre: String)

data class Especialidad(val id: String, val nombre: String)

data class Medico(
    val id: String,
    val nombre: String,
    val especialidadId: String,
    val sedeIds: Set<String>,
)

data class Cita(
    val id: Int,
    val pacienteId: String,
    val especialidad: Especialidad,
    val medico: Medico,
    val sede: Sede,
    val fecha: LocalDate,
    val hora: LocalTime,
    val motivo: String,
    val estado: EstadoCita,
)

data class Catalogo(
    val especialidades: List<Especialidad>,
    val sedes: List<Sede>,
    val medicos: List<Medico>,
)

data class ResumenCitas(val paciente: Paciente, val citas: List<Cita>)

