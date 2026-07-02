package com.nescore.aprendizaje_ia_quechua_aimara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nescore.aprendizaje_ia_quechua_aimara.data.AuthRepository
import com.nescore.aprendizaje_ia_quechua_aimara.data.TemasRepository
import com.nescore.aprendizaje_ia_quechua_aimara.ui.home.DetalleTemaScreen
import com.nescore.aprendizaje_ia_quechua_aimara.ui.home.HomeScreen
import com.nescore.aprendizaje_ia_quechua_aimara.ui.home.TemasViewModel
import com.nescore.aprendizaje_ia_quechua_aimara.ui.login.LoginScreen
import com.nescore.aprendizaje_ia_quechua_aimara.ui.login.LoginViewModel
import com.nescore.aprendizaje_ia_quechua_aimara.ui.theme.Aprendizaje_IA_Qechua_AimaraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val authRepository = AuthRepository(applicationContext)
        val loginViewModel = LoginViewModel(authRepository)
        val temasRepository = TemasRepository()
        val temasViewModel = TemasViewModel(temasRepository)

        enableEdgeToEdge()
        setContent {
            Aprendizaje_IA_Qechua_AimaraTheme {
                val navController = rememberNavController()
                var selectedLanguage by remember { mutableStateOf("Quechua") }

                NavHost(navController = navController, startDestination = "login") {
                    
                    composable("login") {
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    
                    composable("home") {
                        HomeScreen(
                            selectedLanguage = selectedLanguage,
                            isGuest = loginViewModel.isGuest(),
                            userPhotoUrl = loginViewModel.currentUser?.photoUrl?.toString(),
                            onSignOut = {
                                // Cerramos sesión de forma segura y navegamos al finalizar
                                loginViewModel.signOut {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            },
                            onTemaClick = { tema ->
                                navController.navigate("detalle_tema/$tema")
                            }
                        )
                    }

                    composable(
                        route = "detalle_tema/{temaNombre}",
                        arguments = listOf(navArgument("temaNombre") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val temaNombre = backStackEntry.arguments?.getString("temaNombre") ?: ""
                        DetalleTemaScreen(
                            temaNombre = temaNombre,
                            viewModel = temasViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
