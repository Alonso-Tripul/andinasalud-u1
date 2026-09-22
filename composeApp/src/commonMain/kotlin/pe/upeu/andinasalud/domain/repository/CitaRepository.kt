package pe.upeu.andinasalud.domain.repository

import pe.upeu.andinasalud.domain.model.Catalogo
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Paciente

interface CitaRepository {
    suspend fun obtenerPaciente(): Paciente
    suspend fun obtenerCatalogo(): Catalogo
    suspend fun obtenerCitas(): List<Cita>
    suspend fun guardarCita(cita: Cita): Cita
    suspend fun actualizarCita(cita: Cita)
}

