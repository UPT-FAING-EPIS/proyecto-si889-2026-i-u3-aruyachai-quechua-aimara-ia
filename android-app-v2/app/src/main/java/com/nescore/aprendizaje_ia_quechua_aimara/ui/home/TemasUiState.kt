package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import com.nescore.aprendizaje_ia_quechua_aimara.data.model.Palabra
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.Tema

data class TemasUiState(
    val temas: List<Tema> = emptyList(),
    val temaSeleccionado: Tema? = null,
    val palabras: List<Palabra> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

sealed class TemasUiEvent {
    object LoadTemas : TemasUiEvent()
    data class SelectTema(val tema: Tema) : TemasUiEvent()
    data class LoadPalabras(val nombreTema: String) : TemasUiEvent()
    data class SearchQueryChanged(val query: String) : TemasUiEvent()
}
