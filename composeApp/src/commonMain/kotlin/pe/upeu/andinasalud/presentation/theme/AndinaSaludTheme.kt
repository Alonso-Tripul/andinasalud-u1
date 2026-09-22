package pe.upeu.andinasalud.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Claro = lightColorScheme(
    primary = AzulAndino,
    onPrimary = Color.White,
    secondary = VerdeSalud,
    onSecondary = Color.White,
    background = FondoClaro,
    surface = Color.White,
    onBackground = AzulOscuro,
    error = NaranjaAviso,
)

private val Oscuro = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = AzulOscuro,
    secondary = VerdeClaro,
    background = FondoOscuro,
    surface = AzulOscuro,
    onBackground = Color(0xFFE9F2F2),
    error = Color(0xFFFFB68A),
)

@Composable
fun AndinaSaludTheme(oscuro: Boolean, contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (oscuro) Oscuro else Claro,
        typography = TipografiaAndina,
        content = contenido,
    )
}

