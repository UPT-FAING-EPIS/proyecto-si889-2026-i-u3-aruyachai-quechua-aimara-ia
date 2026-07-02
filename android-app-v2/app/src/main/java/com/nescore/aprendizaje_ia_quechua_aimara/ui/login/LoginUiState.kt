package com.nescore.aprendizaje_ia_quechua_aimara.ui.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

sealed class LoginUiEvent {
    object Success : LoginUiEvent()
    data class Error(val message: String) : LoginUiEvent()
}
