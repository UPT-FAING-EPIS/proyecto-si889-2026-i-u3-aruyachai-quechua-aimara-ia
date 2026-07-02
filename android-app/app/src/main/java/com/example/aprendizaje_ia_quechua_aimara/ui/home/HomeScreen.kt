package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * HomeScreen: Orquestador principal de la interfaz tras la autenticación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedLanguage: String,
    isGuest: Boolean,
    userPhotoUrl: String?,
    onSignOut: () -> Unit,
    onTemaClick: (String) -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    
    val tabs = listOf(
        TabItem("Temas", Icons.Default.MenuBook),
        TabItem("Chat", Icons.Default.Chat),
        TabItem("Wordle", Icons.Default.Games)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aprende") },
                actions = {
                    UserAvatar(isGuest = isGuest, photoUrl = userPhotoUrl)
                    TextButton(onClick = onSignOut) {
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
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> TemasScreen(onTemaClick = onTemaClick)
                1 -> ChatScreen()
                2 -> WordleScreen()
            }
        }
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

data class TabItem(val title: String, val icon: ImageVector)
