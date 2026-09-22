package pe.upeu.andinasalud.presentation.citas

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import pe.upeu.andinasalud.data.local.CitasSimuladas

class OrdenCitasTest {
    @Test
    fun `muestra primero las futuras mas proximas y despues el historial reciente`() {
        val citas = CitasSimuladas().citas

        val orden = ordenarCitasPorProximidad(
            citas = citas,
            ahora = Clock.System.now(),
            zona = TimeZone.currentSystemDefault(),
        )

        assertEquals(listOf(1, 2, 3, 6, 5, 4), orden.map { it.id })
    }
}

