package pe.upeu.andinasalud.presentation

sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data object Vacio : UiState<Nothing>
    data class Contenido<T>(val valor: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}

