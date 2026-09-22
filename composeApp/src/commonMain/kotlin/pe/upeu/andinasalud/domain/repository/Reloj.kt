package pe.upeu.andinasalud.domain.repository

import kotlin.time.Instant
import kotlinx.datetime.TimeZone

interface Reloj {
    fun ahora(): Instant
    fun zona(): TimeZone
}

