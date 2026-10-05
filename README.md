# Mi Cámara

Aplicación nativa Android desarrollada con Kotlin.

## Funciones

- Abre la cámara nativa del dispositivo.
- Toma fotografías.
- Guarda las fotografías en el almacenamiento privado externo de la aplicación.
- Muestra una vista previa de la fotografía tomada.
- Registra fecha y hora.
- Obtiene la última ubicación GPS disponible.
- Se compila como APK Android.

## Compilar sin Android Studio

El proyecto incluye GitHub Actions.

1. Sube **todo el contenido** de este proyecto al repositorio `mi-camara`.
2. Asegúrate de que exista `.github/workflows/build-apk.yml`.
3. En GitHub abre la pestaña **Actions**.
4. Abre **Construir APK Mi Camara**.
5. Ejecuta **Run workflow** si no se inicia automáticamente.
6. Cuando termine en verde, abre la ejecución.
7. En **Artifacts**, descarga **MiCamara-APK**.
8. Descomprime el archivo y obtendrás `app-debug.apk`.

## URL pública para la entrega

Después puedes crear una Release en GitHub y adjuntar el APK.
La URL de esa Release puede usarse como URL pública de la aplicación nativa.
