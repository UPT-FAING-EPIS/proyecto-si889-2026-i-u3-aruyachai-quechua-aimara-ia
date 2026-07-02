package com.nescore.aprendizaje_ia_quechua_aimara.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nescore.aprendizaje_ia_quechua_aimara.domain.model.User
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.GetCurrentUserUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.LoginAnonymouslyUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.LoginWithGoogleUseCase
import com.nescore.aprendizaje_ia_quechua_aimara.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val loginAnonymouslyUseCase: LoginAnonymouslyUseCase,
    private val logoutUseCase: LogoutUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    val currentUser: StateFlow<User?> = getCurrentUserUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun signIn() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = loginWithGoogleUseCase()
            _uiState.update { it.copy(isLoading = false) }
            
            result.onSuccess {
                _uiState.update { it.copy(isLoggedIn = true) }
                _uiEvent.emit(LoginUiEvent.Success)
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
                _uiEvent.emit(LoginUiEvent.Error(e.message ?: "Error desconocido"))
            }
        }
    }

    fun signInAsGuest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = loginAnonymouslyUseCase()
            _uiState.update { it.copy(isLoading = false) }

            result.onSuccess {
                _uiState.update { it.copy(isLoggedIn = true) }
                _uiEvent.emit(LoginUiEvent.Success)
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
                _uiEvent.emit(LoginUiEvent.Error(e.message ?: "Error al entrar como invitado"))
            }
        }
    }

    fun signOut(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { LoginUiState() }
            onComplete()
        }
    }

    fun isGuest(): Boolean {
        return currentUser.value?.isAnonymous ?: false
    }

    fun resetState() {
        _uiState.update { it.copy(error = null) }
    }
}
