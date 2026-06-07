# Implementación de API externa REST y estrategia offline-first

Se agregó una implementación offline-first al proyecto Muro Académico.

## ¿Qué se agregó?

1. Permiso de internet en `AndroidManifest.xml`.
2. Dependencias de Retrofit y Gson en `app/build.gradle.kts`.
3. Consumo de API REST externa usando:
    - `data/remote/AvisoApiDto.kt`
    - `data/remote/AvisoApiService.kt`
    - `data/remote/RetrofitClient.kt`
4. Repositorio para sincronización offline-first:
    - `data/repository/AvisosRepository.kt`
5. Persistencia local usando la base SQLite existente:
    - `data/local/DatabaseHelper.kt`
6. Sincronización desde la pantalla principal:
    - `ui/screens/interfaz_UI.kt`
    - `ui/screens/MuroAvisosScreen.kt`

## API externa usada

Se usa la API pública:

`https://jsonplaceholder.typicode.com/posts`

Esta API devuelve publicaciones externas, que la app guarda como avisos académicos.

## ¿Cómo funciona offline-first?

La aplicación primero carga los avisos desde la base de datos local SQLite. Después intenta sincronizarse con la API externa REST. Si hay internet, descarga avisos externos y los guarda localmente. Si no hay conexión, la app sigue funcionando con los avisos ya guardados en la base local.

## Dónde se ve en la app

Al iniciar sesión, el muro intenta sincronizar automáticamente. En el menú lateral del muro también aparece la opción:

`Sincronizar API externa`

Si la sincronización funciona, se muestra:

`Sincronizado con API externa`

Si no hay internet, se muestra:

`Sin conexión: mostrando datos guardados localmente`

## Nota

El proyecto aprovecha la base local SQLite que ya existía. No se migró a Room para evitar cambiar toda la estructura del proyecto, pero se cumple el requisito de persistencia local y funcionamiento sin conexión.
