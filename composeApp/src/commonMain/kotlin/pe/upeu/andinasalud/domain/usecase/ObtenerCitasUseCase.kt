package pe.upeu.andinasalud.domain.usecase

import kotlinx.coroutines.delay
import pe.upeu.andinasalud.domain.model.Catalogo
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.model.ResumenCitas
import pe.upeu.andinasalud.domain.repository.CitaRepository

private const val RETARDO_CARGA_MS = 800L

class ObtenerCitasUseCase(private val repositorio: CitaRepository) {
    suspend operator fun invoke(): ResumenCitas {
        delay(RETARDO_CARGA_MS)
        return ResumenCitas(repositorio.obtenerPaciente(), repositorio.obtenerCitas())
    }
}

class ObtenerDetalleCitaUseCase(private val repositorio: CitaRepository) {
    suspend operator fun invoke(id: Int): Cita? {
        delay(RETARDO_CARGA_MS)
        return repositorio.obtenerCitas().firstOrNull { it.id == id }
    }
}

class ObtenerCatalogoUseCase(private val repositorio: CitaRepository) {
    suspend operator fun invoke(): Catalogo {
        delay(RETARDO_CARGA_MS)
        return repositorio.obtenerCatalogo()
    }
}

class ObtenerPacienteUseCase(private val repositorio: CitaRepository) {
    suspend operator fun invoke(): Paciente {
        delay(RETARDO_CARGA_MS)
        return repositorio.obtenerPaciente()
    }
}

