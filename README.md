# 📱 Sistema móvil con inteligencia artificial para el aprendizaje de quechua y aimara

Video: https://drive.google.com/file/d/1OVgqt_HrqowAYTsH_m7L9sRa1uoVBE1T/view?usp=sharing

Proyecto de desarrollo de software para el autoaprendizaje de las lenguas originarias quechua y aimara en dispositivos móviles Android, integrando servicios de bases de datos en tiempo real y soporte conversacional con modelos de inteligencia artificial.

## 📚 Curso
Patrones de Software

## 👨‍🏫 Docente
Mag. Ing. Patrick Cuadros Quiroga

## 👥 Integrantes
- Serrano Ibañez, Nestor Juice Yomar (2022075474)
- Mamani Estaña, Junior (2022075474)
- Concha Llaca, Gerardo Alejandro (2017057849)

---

## 📌 Descripción del Proyecto

Este sistema es una aplicación móvil nativa para Android que permite a los usuarios aprender vocabulario, frases y gramática en quechua y aimara de forma dinámica. La aplicación cuenta con guías de aprendizaje interactivas, evaluaciones de lecciones, un chatbot conversacional bilingüe que aclara dudas, y un juego interactivo de tipo Wordle para la retención de términos, con soporte de pistas generadas mediante inteligencia artificial. Todo el progreso se sincroniza con una tabla de clasificación competitiva global para motivar el autoaprendizaje constante.

---

## 🚀 Requerimientos Funcionales Reales (12 RF)

1. **RF-01: Registrar e iniciar sesión:** Autenticación a través de cuentas de Google y opción de invitado anónimo.
2. **RF-02: Vincular cuentas:** Fusión de progreso guardado localmente de un invitado al asociar su cuenta de Google.
3. **RF-03: Visualizar temas:** Panel de lecciones ordenadas y filtradas para quechua y aimara.
4. **RF-04: Reproducir pronunciación:** Reproducción de audio nativo con el motor de síntesis local TTS.
5. **RF-05: Resolver prácticas:** Evaluaciones de opción múltiple y escritura para medir el aprendizaje de cada lección.
6. **RF-06: Mostrar indicadores de avance:** Resaltado visual en verde y badges de lecciones completadas con éxito.
7. **RF-07: Restringir duplicidad de puntos:** Control de seguridad para evitar sumar puntos repetidos al reintentar exámenes aprobados.
8. **RF-08: Conversar con chatbot:** Asistente conversacional de tutoría que responde y corrige oraciones escritas por el alumno.
9. **RF-09: Jugar Wordle bilingüe:** Minijuego diario de adivinar palabras con celdas de colores.
10. **RF-10: Solicitar pistas de IA:** Generación de sugerencias semánticas e iniciales en el Wordle mediante prompts de IA.
11. **RF-11: Consultar tabla de clasificación:** Ranking global con los puntajes de todos los estudiantes registrados.
12. **RF-12: Actualizar puntaje:** Recálculo en vivo de la puntuación en base a las lecciones resueltas y el Wordle.

---

## 🛠️ Tecnologías Utilizadas

### 📱 Frontend (Aplicación móvil)
- **Kotlin** y **Jetpack Compose** para el desarrollo nativo reactivo.
- **Arquitectura limpia** con patrón de diseño **MVVM**.
- Motores locales de Android para **TextToSpeech (TTS)** y **SpeechRecognizer (STT)**.

### ☁️ Backend y Servicios
- **Firebase Authentication** para la gestión de cuentas y accesos.
- **Firebase Realtime Database (RTDB)** para la sincronización inmediata del progreso de estudio y la clasificación global.
- **Google Cloud Functions** actuando como intermediario NodeJS para procesar de forma segura las credenciales de la IA.
- **OpenAI API (GPT-4o-mini)** para el procesamiento conversacional y pistas del Wordle.

---

## 📂 Documentos del Proyecto

La documentación de ingeniería del software comprende los siguientes informes consolidados:
* **[FD01-EPIS-Informe de Factibilidad](file:///c:/Users/Ryzen/Downloads/proyecto-si889-2026-i-u1-aprendizaje-ia-quechua-aimara/FD01-EPIS-Informe%20de%20Factibilidad.docx):** Análisis de viabilidad técnica, operativa, social, legal y financiera (VAN: S/. 2,047.54, TIR: 26%, B/C: 1.34).
* **[FD02-EPIS-Informe Vision](file:///c:/Users/Ryzen/Downloads/proyecto-si889-2026-i-u1-aprendizaje-ia-quechua-aimara/FD02-EPIS-Informe%20Vision.docx):** Definición de la visión del sistema y necesidades del público objetivo.
* **[FD03-EPIS-Informe Especificación Requerimientos](file:///c:/Users/Ryzen/Downloads/proyecto-si889-2026-i-u1-aprendizaje-ia-quechua-aimara/FD03-EPIS-Informe%20Especificaci%C3%B3n%20Requerimientos.docx):** SRS detallado con narrativa de casos de uso y 40 diagramas UML (casos de uso, actividades, secuencias).
* **[FD04-EPIS-Informe Arquitectura de Software](file:///c:/Users/Ryzen/Downloads/proyecto-si889-2026-i-u1-aprendizaje-ia-quechua-aimara/FD04-EPIS-Informe%20Arquitectura%20de%20Software.docx):** SAD estructurado en el modelo de vistas 4+1 (lógica, procesos, componentes, despliegue).
* **[FD05-EPIS-Informe ProyectoFinal](file:///c:/Users/Ryzen/Downloads/proyecto-si889-2026-i-u1-aprendizaje-ia-quechua-aimara/FD05-EPIS-Informe%20ProyectoFinal.docx):** Informe final que recopila los resultados de implementación, presupuesto y conclusiones de la solución.

---

## 📍 Ubicación
Tacna, Perú  
2026
