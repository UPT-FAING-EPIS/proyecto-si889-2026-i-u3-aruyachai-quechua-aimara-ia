package com.nescore.aprendizaje_ia_quechua_aimara

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.FileProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nescore.aprendizaje_ia_quechua_aimara.ui.home.DetalleTemaScreen
import com.nescore.aprendizaje_ia_quechua_aimara.ui.home.HomeScreen
import com.nescore.aprendizaje_ia_quechua_aimara.ui.home.WordleScreen
import com.nescore.aprendizaje_ia_quechua_aimara.ui.login.LoginScreen
import com.nescore.aprendizaje_ia_quechua_aimara.ui.practice.ExamScreen
import com.nescore.aprendizaje_ia_quechua_aimara.ui.practice.PracticeListScreen
import com.nescore.aprendizaje_ia_quechua_aimara.ui.practice.ResultScreen
import com.nescore.aprendizaje_ia_quechua_aimara.ui.theme.Aprendizaje_IA_Qechua_AimaraTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Aprendizaje_IA_Qechua_AimaraTheme {
                val navController = rememberNavController()
                val rootView = LocalView.current
                var selectedLanguage by remember { mutableStateOf("Quechua") }

                NavHost(navController = navController, startDestination = "login") {

                    composable("login") {
                        LoginScreen(
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
                            onSignOut = {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onTemaClick = { tema ->
                                if (tema == "Wordle") {
                                    navController.navigate("wordle")
                                } else {
                                    navController.navigate("detalle_tema/$tema")
                                }
                            },
                            onPracticeCategorySelected = { language ->
                                navController.navigate("practice_list/$language")
                            }
                        )
                    }

                    composable("wordle") {
                        WordleScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = "detalle_tema/{temaNombre}",
                        arguments = listOf(navArgument("temaNombre") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val temaNombre = backStackEntry.arguments?.getString("temaNombre") ?: ""
                        DetalleTemaScreen(
                            temaNombre = temaNombre,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // Rutas de Prácticas
                    composable("practice_list/{language}") { backStackEntry ->
                        val language = backStackEntry.arguments?.getString("language") ?: ""
                        PracticeListScreen(
                            language = language,
                            onPracticeSelected = { lang, level, title ->
                                navController.navigate("practice_exam/$lang/$level/$title")
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("practice_exam/{language}/{level}/{examTitle}") { backStackEntry ->
                        val language = backStackEntry.arguments?.getString("language") ?: ""
                        val level = backStackEntry.arguments?.getString("level") ?: ""
                        val examTitle = backStackEntry.arguments?.getString("examTitle") ?: ""
                        ExamScreen(
                            language = language,
                            level = level,
                            examTitle = examTitle,
                            onBack = { navController.popBackStack() },
                            onFinish = { score, total, achievement, shareMessage ->
                                navController.navigate("practice_results/$score/$total/$achievement/$shareMessage") {
                                    popUpTo("practice_list/$language") { inclusive = false }
                                }
                            }
                        )
                    }

                    composable(
                        route = "practice_results/{score}/{total}/{achievement}/{shareMessage}",
                        arguments = listOf(
                            navArgument("score") { type = NavType.IntType },
                            navArgument("total") { type = NavType.IntType },
                            navArgument("achievement") { type = NavType.StringType },
                            navArgument("shareMessage") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        ResultScreen(
                            score = backStackEntry.arguments?.getInt("score") ?: 0,
                            totalQuestions = backStackEntry.arguments?.getInt("total") ?: 5,
                            achievementName = backStackEntry.arguments?.getString("achievement") ?: "",
                            shareMessage = backStackEntry.arguments?.getString("shareMessage") ?: "",
                            onRestart = { navController.popBackStack() },
                            onBackToHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onShare = { message ->
                                shareAchievementWithImage(rootView, message)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun shareAchievementWithImage(view: View, message: String) {
        try {
            // 1. Crear un bitmap de la vista actual (Captura de pantalla)
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)

            // 2. Guardar el bitmap en el directorio de caché
            val cachePath = File(cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "logro_aprende.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            // 3. Obtener el URI a través del FileProvider
            val contentUri: Uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )

            // 4. Iniciar el Intent de compartir con texto e imagen
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_SUBJECT, "¡Mi Logro en Aprende!")
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Compartir Logro"))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
