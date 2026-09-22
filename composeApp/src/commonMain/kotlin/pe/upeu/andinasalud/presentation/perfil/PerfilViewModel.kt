package pe.upeu.andinasalud.presentation.perfil

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.usecase.ObtenerPacienteUseCase
import pe.upeu.andinasalud.presentation.UiState

class PerfilViewModel(private val obtenerPaciente: ObtenerPacienteUseCase) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _uiState = MutableStateFlow<UiState<Paciente>>(UiState.Cargando)
    val uiState: StateFlow<UiState<Paciente>> = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        scope.launch {
            _uiState.value = UiState.Cargando
            try { _uiState.value = UiState.Contenido(obtenerPaciente()) }
            catch (error: Exception) {
                _uiState.value = UiState.Error(error.message ?: "No se pudo cargar el perfil")
            }
        }
    }

    fun cerrar() { scope.cancel() }
}

