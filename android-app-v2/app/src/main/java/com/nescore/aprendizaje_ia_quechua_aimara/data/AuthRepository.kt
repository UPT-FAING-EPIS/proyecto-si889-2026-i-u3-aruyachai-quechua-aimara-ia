package com.nescore.aprendizaje_ia_quechua_aimara.data

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

/**
 * AuthRepository: Maneja la lógica de autenticación de la aplicación.
 * Actúa como la fuente única de verdad para los datos de sesión del usuario.
 */
class AuthRepository(private val context: Context) {
    // Instancia de Firebase Auth para interactuar con los servicios de autenticación de Firebase
    private val firebaseAuth = FirebaseAuth.getInstance()
    
    // CredentialManager: API moderna de Android para gestionar credenciales (Google, Passkeys)
    private val credentialManager = CredentialManager.create(context)

    // Expone el usuario actual de Firebase (null si no hay sesión iniciada)
    val currentUser get() = firebaseAuth.currentUser

    /**
     * Inicia sesión con Google utilizando la API de Credential Manager.
     * 1. Solicita una credencial de Google al sistema.
     * 2. Obtiene el ID Token del resultado.
     * 3. Autentica en Firebase usando dicho token.
     */
    suspend fun signInWithGoogle(): Result<Unit> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                // ID de cliente web configurado en la consola de Firebase
                .setServerClientId("1017057138451-va739qp8lrh3ivdbqd13cl12q8n6jgd1.apps.googleusercontent.com")
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // Lanza el selector de cuentas nativo de Android
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            // Procesa el token de Google y crea la credencial de Firebase
            val googleIdToken = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken.idToken, null)
            
            // Firma en Firebase
            firebaseAuth.signInWithCredential(firebaseCredential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión de forma anónima (Invitado) en Firebase.
     * No requiere datos del usuario y permite acceso temporal a la app.
     */
    suspend fun signInAnonymously(): Result<Unit> {
        return try {
            firebaseAuth.signInAnonymously().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión activa en Firebase y limpia el estado del Credential Manager.
     */
    suspend fun signOut() {
        firebaseAuth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    /**
     * Verifica si el usuario actual ha entrado bajo la modalidad de invitado.
     */
    fun isUserAnonymous(): Boolean {
        return firebaseAuth.currentUser?.isAnonymous ?: false
    }
}
