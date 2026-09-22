package pe.upeu.andinasalud.data.local

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import pe.upeu.andinasalud.domain.repository.Reloj

class RelojSistema : Reloj {
    override fun ahora(): Instant = Clock.System.now()
    override fun zona(): TimeZone = TimeZone.currentSystemDefault()
}

