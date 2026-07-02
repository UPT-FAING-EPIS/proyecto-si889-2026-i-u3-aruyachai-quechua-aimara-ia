<center>

![./media/media/image1.png](./media/logo-upt.png)

**UNIVERSIDAD PRIVADA DE TACNA**

**FACULTAD DE INGENIERIA**

**Escuela Profesional de Ingeniería de Sistemas**

**Proyecto *Sistema móvil con inteligencia artificial para el aprendizaje de quechua y aimara***

Curso: *Patrones de Software*

Docente: *Mag. Ing. Patrick Cuadros Quiroga*

Integrantes:

***Mamani Estaña, Junior (2022075474)***
***Serrano Ibañez, Nestor Juice Yomar (2022075474)***

**Tacna - Perú**

***2026***

** **
</center>
<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

|CONTROL DE VERSIONES||||||
| :-: | :- | :- | :- | :- | :- |
|Versión|Hecha por|Revisada por|Aprobada por|Fecha|Motivo|
|1.0|JME, NSI|PCQ|PCQ|04/04/2026|Versión 1 [cite: 619, 623]|

**Sistema *Sistema móvil con inteligencia artificial para el aprendizaje de quechua y aimara* [cite: 611]**

**Documento de Visión [cite: 621]**

**Versión *1.0* [cite: 622]**
**

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

|CONTROL DE VERSIONES||||||
| :-: | :- | :- | :- | :- | :- |
|Versión|Hecha por|Revisada por|Aprobada por|Fecha|Motivo|
[cite_start]|1.0|JME, NSI|PCQ|PCQ|04/04/2026|Versión 1 [cite: 619, 623]|


<div style="page-break-after: always; visibility: hidden">\pagebreak</div>


**INDICE GENERAL**
#
[1. Introducción](#_Toc52661346)
1.1 Propósito
1.2 Alcance
1.3 Definiciones, Siglas y Abreviaturas
1.4 Referencias
1.5 Visión General

[2. Posicionamiento](#_Toc52661347)
2.1 Oportunidad de negocio
2.2 Definición del problema

[3. Descripción de los interesados y usuarios](#_Toc52661348)
3.1 Resumen de los interesados
3.2 Resumen de los usuarios
3.3 Entorno de usuario
3.4 Perfiles de los interesados
3.5 Perfiles de los Usuarios
3.6 Necesidades de los interesados y usuarios

[4. Vista General del Producto](#_Toc52661349)
4.1 Perspectiva del producto
4.2 Resumen de capacidades
4.3 Suposiciones y dependencias
4.4 Costos y precios
4.5 Licenciamiento e instalación

[5. Características del producto](#_Toc52661350)

[6. Restricciones](#_Toc52661351)

[7. Rangos de calidad](#_Toc52661352)

[8. Precedencia y Prioridad](#_Toc52661353)

[9. Otros requerimientos del producto](#_Toc52661354)

[CONCLUSIONES](#_Toc52661355)

[RECOMENDACIONES](#_Toc52661356)

[BIBLIOGRAFIA](#_Toc52661357)

[WEBGRAFIA](#_Toc52661358)


<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

**<u>Informe de Visión</u>**

1. <span id="_Toc52661346" class="anchor"></span>**Introducción**

    **1.1 Propósito**
    [cite_start]Desarrollar una aplicación móvil interactiva que facilite el aprendizaje de quechua y aimara de forma personalizada[cite: 655]. [cite_start]Para lograrlo, la plataforma integrará ejercicios prácticos, un chatbot asistente y herramientas de reconocimiento de voz que ayuden a conservar y difundir estas lenguas originarias[cite: 656].

    **1.2 Alcance**
    [cite_start]La aplicación comprende lecciones estructuradas por niveles, ejercicios prácticos, exámenes auto-evaluativos, registro de progreso, un chatbot de práctica conversacional y reconocimiento de voz[cite: 658]. [cite_start]El contenido se adapta de acuerdo al nivel del estudiante[cite: 659].

    **1.3 Definiciones, Siglas y Abreviaturas**
    * [cite_start]**SRS**: Documento de Especificación de Requisitos del Software[cite: 661].
    * **MVVM**: Model View ViewModel[cite: 662].
    * [cite_start]**Firebase**: Plataforma de servicios en la nube para desarrollo de aplicaciones[cite: 663].

    **1.4 Referencias**
    * [cite_start]Documentos de registros del proyecto[cite: 665, 666].
    * Informe de Factibilidad del Sistema[cite: 392].

    **1.5 Visión General**
    Buscamos crear una herramienta móvil accesible que acerque las lenguas originarias a más personas mediante tecnología interactiva, promoviendo el rescate cultural y ofreciendo una alternativa digital intuitiva y eficiente[cite: 668, 669].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

2. <span id="_Toc52661347" class="anchor"></span>**Posicionamiento**

    **2.1 Oportunidad de negocio**
    Hay pocas aplicaciones destinadas a la enseñanza del quechua y el aimara que usen tecnologías recientes. Esto abre una oportunidad para ofrecer una alternativa digital que combine el aprendizaje interactivo con asistentes de inteligencia artificial[cite: 672, 673].

    **2.2 Definición del problema**
    El estudio de estos idiomas carece de herramientas digitales modernas e interactivas[cite: 675]. Los métodos de enseñanza tradicionales no logran atraer a los usuarios más jóvenes, y no existen plataformas accesibles que empleen tecnología para practicar la pronunciación o la conversación[cite: 676].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

3. <span id="_Toc52661348" class="anchor"></span>**Descripción de los interesados y usuarios**

    **3.1 Resumen de los interesados**
    * [cite_start]**Nestor Serrano Ibañez**: Jefe del Proyecto / Programador / Analista[cite: 679].
    * [cite_start]**Junior Mamani Estaña**: Programador / Analista[cite: 679].
    * **Usuario del sistema**: Usuario final del aplicativo[cite: 679].

    **3.2 Resumen de los usuarios**
    * [cite_start]**Usuario general**: Persona interesada en aprender quechua o aimara[cite: 681].
    * [cite_start]**Administrador**: Encargado de gestionar contenido, usuarios y reportes[cite: 681].

    **3.3 Entorno de usuario**
    [cite_start]La aplicación se ejecuta en dispositivos Android y requiere conexión a internet[cite: 683]. [cite_start]Los estudiantes acceden a las lecciones y herramientas de práctica, mientras que los administradores pueden gestionar los contenidos y usuarios[cite: 684, 685].

    **3.4 Perfiles de los interesados**
    * **Equipo de Desarrollo**: Responsables de la construcción, integración de IA, pruebas y mantenimiento del sistema[cite: 688].
    * [cite_start]**Usuario del Sistema**: Persona externa interesada en un aprendizaje efectivo con facilidad de uso y contenido confiable[cite: 687].

    **3.5 Perfiles de los Usuarios**
    * [cite_start]**Perfil Usuario General**: Principal beneficiario encargado de interactuar con los módulos de aprendizaje y herramientas de IA para su progreso continuo[cite: 690].

    **3.6 Necesidades de los interesados y usuarios**
    * Plataforma segura y fácil de usar[cite: 692].
    * [cite_start]Aprendizaje interactivo y dinámico[cite: 693].
    * [cite_start]Seguimiento del progreso y mejora en la pronunciación por voz[cite: 694, 695].
    * Acceso a contenido educativo confiable[cite: 696].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

4. <span id="_Toc52661349" class="anchor"></span>**Vista General del Producto**

    **4.1 Perspectiva del producto**
    Es una aplicación móvil educativa orientada al autoaprendizaje. Integra herramientas de procesamiento de lenguaje y reconocimiento de voz para personalizar el avance de cada alumno[cite: 699, 700].

    **4.2 Resumen de capacidades**
    * [cite_start]Registro y autenticación de usuarios[cite: 702].
    * [cite_start]Módulos estructurados (vocabulario, frases, gramática) y ejercicios interactivos[cite: 703, 704].
    * Chatbot con IA y reconocimiento de voz para evaluación de pronunciación[cite: 705, 706].
    * [cite_start]Seguimiento de progreso y generación de reportes[cite: 707, 708].

    **4.3 Suposiciones y dependencias**
    * [cite_start]Los usuarios necesitan un teléfono Android compatible y conexión a internet[cite: 710].
    * El funcionamiento depende de la disponibilidad de Firebase y de las APIs de inteligencia artificial externas[cite: 711].
    * [cite_start]El contenido lingüístico debe ser revisado y aprobado por especialistas para garantizar su precisión[cite: 712].

    **4.4 Costos y precios**
    * **Costo Total del Proyecto**: S/. [cite_start]5,993.00[cite: 714].
    * **Costo Mensual Operativo**: S/. 1,997.67[cite: 714].

    **4.5 Licenciamiento e instalación**
    La instalación se realiza mediante un archivo APK en dispositivos Android con API 24 o superior[cite: 466]. El licenciamiento se ajusta a las condiciones de uso de Firebase y las APIs de terceros empleadas en la lógica del sistema[cite: 493].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

5. <span id="_Toc52661350" class="anchor"></span>**Características del producto**
* [cite_start]Registro y autenticación segura de usuarios[cite: 716].
* [cite_start]Módulos de aprendizaje y ejercicios de selección múltiple, completar oraciones y traducción[cite: 717, 718].
* Chatbot educativo con IA para práctica conversacional[cite: 719].
* [cite_start]Reconocimiento de voz con retroalimentación inmediata sobre la pronunciación[cite: 720].
* [cite_start]Interfaz amigable desarrollada con Jetpack Compose[cite: 723].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

6. <span id="_Toc52661351" class="anchor"></span>**Restricciones**
* Es obligatorio contar con conexión a internet para usar el chatbot y guardar el progreso[cite: 725].
* [cite_start]La aplicación requiere sistemas Android 8.0 (API 26) o superior[cite: 726].
* [cite_start]El presupuesto asignado limita la cantidad de consultas mensuales a las APIs externas de inteligencia artificial[cite: 728].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

7. <span id="_Toc52661352" class="anchor"></span>**Rangos de Calidad**
* **Disponibilidad**: El servicio debe mantenerse activo al menos el 99% del tiempo cada mes[cite: 730].
* [cite_start]**Rendimiento**: Las funciones comunes dentro de la app deben responder en un máximo de 3 segundos[cite: 731].
* [cite_start]**Seguridad**: Protegemos los perfiles mediante autenticación cifrada y almacenamiento seguro de datos[cite: 733].
* **Usabilidad**: La interfaz es sencilla y fácil de navegar, adaptada a usuarios de cualquier nivel tecnológico[cite: 732].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

8. <span id="_Toc52661353" class="anchor"></span>**Precedencia y Prioridad**
1. Registro, autenticación y gestión segura de accesos (Prioridad Alta)[cite: 736, 737].
2. Funcionamiento correcto de módulos de aprendizaje y ejercicios (Prioridad Alta)[cite: 738, 739].
3. Implementación de chatbot con IA y reconocimiento de voz (Prioridad Media)[cite: 740, 741].
4. Estabilidad, rapidez y escalabilidad del sistema (Prioridad Media)[cite: 743, 744].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

9. <span id="_Toc52661354" class="anchor"></span>**Otros requerimientos del producto**
* [cite_start]**Estándares Legales**: Cumplimos con la Ley N° 29733 de Protección de Datos Personales en el manejo de perfiles de usuario[cite: 746].
* [cite_start]**Estándares de Comunicación**: Los avisos e instrucciones dentro de la app son claros y fáciles de entender[cite: 747].
* **Estándares de Usabilidad**: La navegación está diseñada para personas que no están familiarizadas con las aplicaciones móviles[cite: 748].
* [cite_start]**Estándares de Calidad**: Buscamos un bajo margen de fallos durante el uso diario de la aplicación[cite: 750].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

<span id="_Toc52661355" class="anchor"></span>**CONCLUSIONES**
* [cite_start]La aplicación es una propuesta interactiva para mejorar la enseñanza de lenguas originarias a través de tecnología móvil[cite: 752].
* El proyecto tiene un propósito social enfocado en la conservación del quechua y el aimara[cite: 753].
* [cite_start]El diseño técnico con arquitectura MVVM permite añadir nuevas lecciones y herramientas de forma sencilla en el futuro[cite: 754].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

<span id="_Toc52661356" class="anchor"></span>**RECOMENDACIONES**
* [cite_start]Recomendamos revisar periódicamente las traducciones y audios con especialistas lingüísticos para asegurar la calidad[cite: 756].
* Sugerimos optimizar el consumo de las APIs de inteligencia artificial para controlar los costos mensuales de operación[cite: 757].
* [cite_start]Realizar pruebas con estudiantes reales ayudará a identificar problemas en la interfaz y mejorar la experiencia de uso[cite: 758].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

<span id="_Toc52661357" class="anchor"></span>**BIBLIOGRAFIA**
* [cite_start]IEEE Std 830-1998, Recommended Practice for Software Requirements Specifications[cite: 661].
* Estándar Internacional ISO/IEC 27001 sobre Seguridad de la Información[cite: 534].

<div style="page-break-after: always; visibility: hidden">\pagebreak</div>

<span id="_Toc52661358" class="anchor"></span>**WEBGRAFIA**
* [cite_start]Firebase Documentation: https://firebase.google.com/docs[cite: 663].
* [cite_start]Android Developers - Jetpack Compose: https://developer.android.com/jetpack/compose[cite: 723].
* Ley N° 29733 - Protección de Datos Personales (Perú)[cite: 514].
* [cite_start]Ley N° 29571 - Código de Protección y Defensa del Consumidor (Perú)[cite: 524].