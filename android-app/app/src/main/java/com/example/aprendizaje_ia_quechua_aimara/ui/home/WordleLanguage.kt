package com.nescore.aprendizaje_ia_quechua_aimara.ui.home

/**
 * WordleLanguage: Enumeración que define los idiomas disponibles para el juego Wordle.
 * 
 * Este Enum es central para la arquitectura multilingüe de la aplicación, ya que:
 * 1. Permite al ViewModel cambiar la palabra objetivo según el idioma seleccionado.
 * 2. Facilita al Repositorio el filtrado de palabras en Firestore que tengan contenido para dicho idioma.
 * 3. Ayuda a la UI a mostrar etiquetas y longitudes de cuadrícula correctas.
 */
enum class WordleLanguage {
    /** Modo de juego en idioma Quechua */
    QUECHUA,
    
    /** Modo de juego en idioma Aimara */
    AIMARA
}
