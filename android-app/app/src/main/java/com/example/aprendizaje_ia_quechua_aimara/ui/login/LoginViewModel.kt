package com.nescore.aprendizaje_ia_quechua_aimara.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nescore.aprendizaje_ia_quechua_aimara.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * LoginViewModel: Gestiona el estado de la pantalla de inicio de sesión.
 * Actúa como intermediario entre la UI y el repositorio de autenticación.
 */
class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    // _uiState: Estado interno del flujo de login (privado)
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    // uiState: Estado expuesto a la UI de forma reactiva (solo lectura)
    val uiState: StateFlow<LoginUiState> = _uiState

    // currentUser: Mantiene la referencia al usuario actual de Firebase usando State de Compose
    var currentUser by mutableStateOf(repository.currentUser)
        private set

    /**
     * Verifica si el usuario actual es un invitado.
     */
    fun isGuest() = repository.isUserAnonymous()

    /**
     * Proceso de inicio de sesión con Google.
     * Cambia el estado a Loading mientras espera la respuesta del repositorio.
     */
    fun signIn() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = repository.signInWithGoogle()
            if (result.isSuccess) {
                // Si es exitoso, actualizamos el usuario y notificamos éxito
                currentUser = repository.currentUser
                _uiState.value = LoginUiState.Success
            } else {
                // Si falla, capturamos el mensaje de error
                _uiState.value = LoginUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Proceso de inicio de sesión anónimo (Invitado).
     */
    fun signInAsGuest() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = repository.signInAnonymously()
            if (result.isSuccess) {
                currentUser = repository.currentUser
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error(result.exceptionOrNull()?.message ?: "Error al entrar como invitado")
            }
        }
    }

    /**
     * Cierra la sesión del usuario y resetea el estado a Idle (inicial).
     * Ahora acepta un callback para ejecutar tras completar la operación asíncrona.
     */
    fun signOut(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.signOut()
            currentUser = null
            _uiState.value = LoginUiState.Idle
            onComplete()
        }
    }

    /**
     * Limpia el estado de la UI (por ejemplo, después de una navegación exitosa).
     */
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}

/**
 * Representa los estados posibles de la interfaz de Login.
 */
sealed class LoginUiState {
    object Idle : LoginUiState()      // Estado inicial, sin acciones pendientes
    object Loading : LoginUiState()   // Proceso de autenticación en curso (se muestra spinner)
    object Success : LoginUiState()   // Autenticación completada con éxito
    data class Error(val message: String) : LoginUiState() // Fallo en la autenticación con detalle del error
}
