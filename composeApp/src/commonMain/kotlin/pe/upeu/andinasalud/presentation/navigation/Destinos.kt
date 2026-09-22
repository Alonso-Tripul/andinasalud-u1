package pe.upeu.andinasalud.presentation.navigation

sealed interface Destino {
    data object Inicio : Destino
    data object Citas : Destino
    data object Perfil : Destino
    data object Solicitud : Destino
    data class Detalle(val id: Int) : Destino
    data class Confirmacion(val id: Int) : Destino
}

