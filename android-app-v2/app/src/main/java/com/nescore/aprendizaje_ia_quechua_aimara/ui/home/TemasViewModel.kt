package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.GetPalabrasPorTemaUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.GetTemasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemasViewModel @Inject constructor(
    private val getTemasUseCase: GetTemasUseCase,
    private val getPalabrasPorTemaUseCase: GetPalabrasPorTemaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TemasUiState())
    val uiState: StateFlow<TemasUiState> = _uiState.asStateFlow()

    init {
        loadTemas()
    }

    fun onEvent(event: TemasUiEvent) {
        when (event) {
            is TemasUiEvent.LoadTemas -> loadTemas()
            is TemasUiEvent.SelectTema -> {
                _uiState.update { it.copy(temaSeleccionado = event.tema) }
            }
            is TemasUiEvent.LoadPalabras -> loadPalabras(event.nombreTema)
            is TemasUiEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
            }
        }
    }

    private fun loadTemas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getTemasUseCase().onSuccess { temas ->
                _uiState.update { it.copy(temas = temas, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadPalabras(nombreTema: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getPalabrasPorTemaUseCase(nombreTema).onSuccess { palabras ->
                _uiState.update { it.copy(palabras = palabras, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    val palabrasFiltradas = _uiState.map { state ->
        if (state.searchQuery.isBlank()) {
            state.palabras
        } else {
            state.palabras.filter {
                it.espanol.contains(state.searchQuery, ignoreCase = true) ||
                it.quechua.contains(state.searchQuery, ignoreCase = true) ||
                it.aimara.contains(state.searchQuery, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
