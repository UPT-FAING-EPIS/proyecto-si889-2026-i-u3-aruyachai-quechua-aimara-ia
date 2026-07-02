package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nescore.aprendizaje_ia_quechua_aimara.data.TemasRepository
import com.nescore.aprendizaje_ia_quechua_aimara.data.model.Palabra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * TemasViewModel: Encargado de la lógica de negocio para la gestión de vocabulario por temas.
 * Utiliza StateFlow para exponer un estado reactivo a la UI, permitiendo filtrado en tiempo real.
 */
class TemasViewModel(private val repository: TemasRepository) : ViewModel() {

    // Backing property para la lista de palabras obtenida de Firestore.
    private val _palabras = MutableStateFlow<List<Palabra>>(emptyList())
    val palabras: StateFlow<List<Palabra>> = _palabras.asStateFlow()
    
    // Estado del buscador: almacena el texto ingresado por el usuario para el filtrado.
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Estado de carga: permite a la UI mostrar un progreso visual durante las peticiones de red.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Variable para evitar recargas si el tema ya está cargado.
    private var temaCargado: String? = null

    /**
     * palabrasFiltradas: Flujo de datos reactivo que combina la lista base y el criterio de búsqueda.
     */
    val palabrasFiltradas: StateFlow<List<Palabra>> = combine(_palabras, _searchQuery) { lista, query ->
        if (query.isEmpty()) {
            lista
        } else {
            lista.filter { 
                it.espanol.contains(query, ignoreCase = true) ||
                it.quechua.contains(query, ignoreCase = true) ||
                it.aimara.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    /**
     * Carga el vocabulario desde el repositorio (Firestore).
     * Se ha optimizado para evitar re-consultas innecesarias al rotar pantalla.
     * @param tema Nombre identificador del tema a cargar.
     */
    fun cargarPalabras(tema: String) {
        // Validación técnica: Si el tema ya está en memoria, no consultamos Firestore de nuevo.
        if (tema == temaCargado && _palabras.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _searchQuery.value = "" // Reset del buscador al cambiar de tema real.
            
            val resultado = repository.getPalabrasPorTema(tema)
            _palabras.value = resultado
            temaCargado = tema // Marcamos el tema como cargado satisfactoriamente.
            _isLoading.value = false
        }
    }
}
