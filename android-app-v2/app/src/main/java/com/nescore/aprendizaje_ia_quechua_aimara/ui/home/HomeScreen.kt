package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.filled.EmojiEvents
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import com.nescore.aprendizaje_ia_quechua_aimara.ui.login.LoginViewModel
import com.nescore.aprendizaje_ia_quechua_aimara.ui.practice.PracticeCategoryScreen
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * HomeScreen: Orquestador principal de la interfaz tras la autenticación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedLanguage: String,
    onSignOut: () -> Unit,
    onTemaClick: (String) -> Unit,
    onPracticeCategorySelected: (String) -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val currentUser by loginViewModel.currentUser.collectAsState()
    val isGuest = loginViewModel.isGuest()
    val userPhotoUrl = currentUser?.photoUrl
    
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showLeaderboard by rememberSaveable { mutableStateOf(false) }
    
    val tabs = listOf(
        TabItem("Temas", Icons.Default.MenuBook),
        TabItem("Guía", Icons.Default.School),
        TabItem("Chat", Icons.Default.Chat),
        TabItem("Prácticas", Icons.Default.Quiz),
        TabItem("Wordle", Icons.Default.Games)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aru Yachay") },
                actions = {
                    IconButton(onClick = { showLeaderboard = true }) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Clasificación",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    UserAvatar(isGuest = isGuest, photoUrl = userPhotoUrl)
                    TextButton(onClick = {
                        loginViewModel.signOut { onSignOut() }
                    }) {
                        Text("Salir", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(item.title) },
                        icon = { Icon(item.icon, contentDescription = item.title) }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background // Asegura que el fondo del Scaffold no sea negro
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                0 -> TemasScreen(onTemaClick = onTemaClick)
                1 -> StudyGuideScreen(selectedLanguage = selectedLanguage)
                2 -> ChatScreen()
                3 -> PracticeCategoryScreen(onCategorySelected = onPracticeCategorySelected)
                4 -> WordleScreen()
            }
        }
    }

    if (showLeaderboard) {
        LeaderboardDialog(onDismiss = { showLeaderboard = false })
    }
}

@Composable
fun UserAvatar(isGuest: Boolean, photoUrl: String?) {
    if (isGuest) {
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = "Invitado",
            modifier = Modifier.padding(horizontal = 8.dp).size(32.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Perfil",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                Icons.Default.Person,
                contentDescription = "Usuario",
                modifier = Modifier.padding(horizontal = 8.dp).size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LeaderboardDialog(
    onDismiss: () -> Unit
) {
    var leaderboardList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val database = FirebaseDatabase.getInstance()
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            if (currentUser != null) {
                try {
                    // Forzar la actualización del puntaje del usuario actual con sus datos en progreso
                    com.nescore.aprendizaje_ia_quechua_aimara.util.LeaderboardHelper.updateLeaderboard(currentUser.uid)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val snapshot = try {
                database.getReference("leaderboard")
                    .orderByChild("score")
                    .limitToLast(20)
                    .get()
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback: obtener todos los registros y ordenarlos en el cliente si falla la consulta ordenada
                database.getReference("leaderboard")
                    .get()
                    .await()
            }

            if (snapshot.exists()) {
                val list = mutableListOf<Map<String, Any>>()
                snapshot.children.forEach { userSnap ->
                    val data = userSnap.value as? Map<*, *>
                    if (data != null) {
                        val map = mutableMapOf<String, Any>()
                        data.forEach { (k, v) ->
                            if (k is String && v != null) {
                                map[k] = v
                            }
                        }
                        list.add(map)
                    }
                }
                leaderboardList = list.sortedByDescending { (it["score"] as? Number)?.toInt() ?: 0 }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tabla de Clasificación", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (leaderboardList.isEmpty()) {
                    Text("¡Sé el primero en comenzar a practicar y liderar el ranking!", textAlign = TextAlign.Center)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(leaderboardList) { index, user ->
                            val rank = index + 1
                            val name = user["displayName"] as? String ?: "Estudiante"
                            val score = (user["score"] as? Number)?.toInt() ?: 0
                            val words = (user["wordsCompleted"] as? Number)?.toInt() ?: 0
                            val exams = (user["examsPassed"] as? Number)?.toInt() ?: 0
                            val photoUrl = user["photoUrl"] as? String

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = when (rank) {
                                        1 -> Color(0xFFFFF9C4)
                                        2 -> Color(0xFFF5F5F5)
                                        3 -> Color(0xFFFFE0B2)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "#$rank",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = when (rank) {
                                            1 -> Color(0xFFF57F17)
                                            2 -> Color(0xFF616161)
                                            3 -> Color(0xFFE65100)
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.width(36.dp)
                                    )

                                    if (photoUrl != null) {
                                        AsyncImage(
                                            model = photoUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(
                                            text = "$words palabras | $exams prácticas",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }

                                    Text(
                                        text = "$score pts",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

data class TabItem(val title: String, val icon: ImageVector)
