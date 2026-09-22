package pe.upeu.andinasalud.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import pe.upeu.andinasalud.domain.model.Catalogo
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Especialidad
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.model.Sede
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.repository.Reloj
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.CampoSolicitud
import pe.upeu.andinasalud.domain.usecase.ResultadoCancelacion
import pe.upeu.andinasalud.domain.usecase.ResultadoSolicitud
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitudCita

class ReglasCitaTest {
    private val reloj = object : Reloj {
        override fun ahora(): Instant = Instant.parse("2026-09-22T12:00:00Z")
        override fun zona(): TimeZone = TimeZone.UTC
    }

    private fun solicitud(fecha: String = "2026-09-30", hora: String = "09:00", motivo: String = "Control preventivo") =
        SolicitudCita("general", "nana", fecha, hora, motivo)

    @Test
    fun fechaYHoraDebenSerFuturas() = runBlocking {
        val resultado = SolicitarCitaUseCase(RepositorioPrueba(), reloj)(solicitud("2026-09-21"))
        assertIs<ResultadoSolicitud.Invalida>(resultado)
        assertTrue(CampoSolicitud.FECHA in resultado.errores)
    }

    @Test
    fun maximoTresProgramadas() = runBlocking {
        val repositorio = RepositorioPrueba()
        repeat(3) { repositorio.citas.add(repositorio.cita(it + 1, "2026-09-${25 + it}", "10:00")) }
        val resultado = SolicitarCitaUseCase(repositorio, reloj)(solicitud())
        assertIs<ResultadoSolicitud.Invalida>(resultado)
        assertTrue(resultado.errorGeneral?.contains("tres citas") == true)
    }

    @Test
    fun motivoEntreDiezYDosCientosCaracteres() = runBlocking {
        val caso = SolicitarCitaUseCase(RepositorioPrueba(), reloj)
        for (motivo in listOf("corto", "x".repeat(201))) {
            val resultado = caso(solicitud(motivo = motivo))
            assertIs<ResultadoSolicitud.Invalida>(resultado)
            assertTrue(CampoSolicitud.MOTIVO in resultado.errores)
        }
    }

    @Test
    fun noPermiteMismaFechaYHora() = runBlocking {
        val repositorio = RepositorioPrueba()
        repositorio.citas.add(repositorio.cita(1, "2026-09-30", "09:00"))
        val resultado = SolicitarCitaUseCase(repositorio, reloj)(solicitud())
        assertIs<ResultadoSolicitud.Invalida>(resultado)
        assertTrue(CampoSolicitud.HORA in resultado.errores)
    }

    @Test
    fun cancelacionRequiereMasDeVeinticuatroHoras() = runBlocking {
        val repositorio = RepositorioPrueba()
        repositorio.citas.add(repositorio.cita(1, "2026-09-23", "12:00"))
        val resultado = CancelarCitaUseCase(repositorio, reloj)(1)
        assertIs<ResultadoCancelacion.Rechazada>(resultado)
        assertIs<EstadoCita.Programada>(repositorio.citas.single().estado)
    }

    @Test
    fun creaYCancelaUnaCitaValida() = runBlocking {
        val repositorio = RepositorioPrueba()
        val creacion = SolicitarCitaUseCase(repositorio, reloj)(solicitud())
        assertIs<ResultadoSolicitud.Creada>(creacion)
        assertEquals(1, repositorio.citas.size)
        val cancelacion = CancelarCitaUseCase(repositorio, reloj)(creacion.cita.id)
        assertIs<ResultadoCancelacion.Cancelada>(cancelacion)
        assertIs<EstadoCita.Cancelada>(repositorio.citas.single().estado)
    }
}

private class RepositorioPrueba : CitaRepository {
    private val paciente = Paciente("P1", "Paciente", "123", "correo@ejemplo.pe", "999999999")
    private val especialidad = Especialidad("general", "Medicina General")
    private val sede = Sede("nana", "Ñaña")
    private val medico = Medico("M1", "Dra. Prueba", "general", setOf("nana"))
    private val catalogo = Catalogo(listOf(especialidad), listOf(sede), listOf(medico))
    val citas = mutableListOf<Cita>()

    fun cita(id: Int, fecha: String, hora: String) = Cita(
        id, paciente.id, especialidad, medico, sede, LocalDate.parse(fecha), LocalTime.parse(hora),
        "Control preventivo", EstadoCita.Programada(true),
    )

    override suspend fun obtenerPaciente(): Paciente = paciente
    override suspend fun obtenerCatalogo(): Catalogo = catalogo
    override suspend fun obtenerCitas(): List<Cita> = citas.toList()
    override suspend fun guardarCita(cita: Cita): Cita {
        val guardada = cita.copy(id = (citas.maxOfOrNull { it.id } ?: 0) + 1)
        citas.add(guardada)
        return guardada
    }
    override suspend fun actualizarCita(cita: Cita) {
        citas[citas.indexOfFirst { it.id == cita.id }] = cita
    }
}
