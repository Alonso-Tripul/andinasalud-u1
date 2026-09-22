package pe.upeu.andinasalud.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.Catalogo
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.repository.CitaRepository

class CitaRepositoryFake(private val semilla: CitasSimuladas) : CitaRepository {
    private val mutex = Mutex()
    private val citas = semilla.citas.toMutableList()

    override suspend fun obtenerPaciente(): Paciente = semilla.paciente
    override suspend fun obtenerCatalogo(): Catalogo = semilla.catalogo
    override suspend fun obtenerCitas(): List<Cita> = mutex.withLock { citas.toList() }

    override suspend fun guardarCita(cita: Cita): Cita = mutex.withLock {
        val nueva = cita.copy(id = (citas.maxOfOrNull { it.id } ?: 0) + 1)
        citas.add(nueva)
        nueva
    }

    override suspend fun actualizarCita(cita: Cita) = mutex.withLock {
        val indice = citas.indexOfFirst { it.id == cita.id }
        require(indice >= 0) { "La cita no existe" }
        citas[indice] = cita
    }
}

