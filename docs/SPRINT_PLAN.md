# 📋 Sprint Plan — ElAhora (Mi Sistema Personal)

> **Estado:** v1.0 — Pendiente de aprobación
> **Proyecto:** `c:\consciente\mi-sistema\ElAhora`
> **Namespace:** `com.example.elahora`
> **Fecha:** 2026-03-14

---

## Convenciones

- Cada tarea es una acción concreta y atómica (2-10 min de trabajo).
- Formato: `[ARCHIVO] → Acción`
- Prefijo `NUEVA` = archivo a crear desde cero.
- Prefijo `EDIT` = archivo existente a modificar.

---

## SPRINT 0 — Configuración del Proyecto (Prerequisitos)

### S0-01 · Renombrar namespace
> **EDIT** `app/build.gradle.kts`
- Cambiar `com.example.elahora` → `com.misistema.elahora`
- Cambiar `applicationId` a `com.misistema.elahora`

### S0-02 · Agregar dependencias al `build.gradle.kts`
> **EDIT** `app/build.gradle.kts`
Agregar en `dependencies {}`:
```kotlin
// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// DataStore
implementation("androidx.datastore:datastore-preferences:1.1.1")

// Retrofit + OkHttp
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Hilt
implementation("com.google.dagger:hilt-android:2.51.1")
ksp("com.google.dagger:hilt-android-compiler:2.51.1")
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// Compose Pager (swipe entre sistemas)
implementation("androidx.compose.foundation:foundation:1.7.0")

// Markwon (render Markdown en yo.md y reglas)
implementation("io.noties.markwon:core:4.6.2")

// Kotlin Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

// Navigation Compose
implementation("androidx.navigation:navigation-compose:2.8.0")
```

### S0-03 · Agregar plugins KSP y Hilt al `build.gradle.kts`
> **EDIT** `app/build.gradle.kts` — sección `plugins {}`
```kotlin
id("com.google.devtools.ksp")
id("com.google.dagger.hilt.android")
```

### S0-04 · Agregar plugins KSP y Hilt al `settings.gradle.kts` / `build.gradle.kts` raíz
> **EDIT** `build.gradle.kts` (raíz del proyecto)
```kotlin
id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false
id("com.google.dagger.hilt.android") version "2.51.1" apply false
```

### S0-05 · Crear estructura de paquetes
> **NUEVA** estructura de carpetas dentro de `app/src/main/java/com/misistema/elahora/`:
```
data/
  local/
    db/
    datastore/
  remote/
    github/
domain/
  model/
  repository/
  usecase/
presentation/
  home/
  settings/
  theme/
```

---

## SPRINT 1 — Tema y Design System (Neubrutalism)

### S1-01 · Definir tokens de color
> **NUEVA** `presentation/theme/Color.kt`
```kotlin
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Yellow = Color(0xFFFFE500)
val Violet = Color(0xFF7B2FBE)
val Cyan = Color(0xFF00D4FF)
val Red = Color(0xFFEF4444)
val GrayMuted = Color(0xFF6B7280)
```

### S1-02 · Definir tipografía
> **NUEVA** `presentation/theme/Type.kt`
- Fuente: Roboto Condensed Bold (disponible en Android por defecto)
- Estilos: `titleLarge`, `bodyMedium`, `labelSmall`

### S1-03 · Crear componente reutilizable `NeuCard`
> **NUEVA** `presentation/theme/Components.kt`
- `NeuCard(backgroundColor, content)`: Card con borde negro 3dp, sombra offset sólida 4dp
- `NeuButton(text, backgroundColor, onClick)`: Botón con borde negro, sin esquinas redondeadas

### S1-04 · Aplicar tema en `MainActivity`
> **EDIT** `MainActivity.kt`
- Envolver con el nuevo `ElAhoraTheme` que usa la palette Neubrutalism

---

## SPRINT 2 — Capa de Datos: Room

### S2-01 · Crear entidad `DailyLogEntity`
> **NUEVA** `data/local/db/DailyLogEntity.kt`
```kotlin
@Entity(tableName = "daily_log")
data class DailyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val systemId: String,
    val date: String,       // ISO: "2026-03-14"
    val status: String?,    // "DONE" | "NOT_DONE" | null
    val notes: String?
)
```

### S2-02 · Crear `DailyLogDao`
> **NUEVA** `data/local/db/DailyLogDao.kt`
- `@Upsert insertOrUpdate(log: DailyLogEntity)`
- `@Query getByDate(systemId, date): DailyLogEntity?`
- `@Query getLogsForWeek(systemId, weekStart): List<DailyLogEntity>`

### S2-03 · Crear `AppDatabase`
> **NUEVA** `data/local/db/AppDatabase.kt`
- Room Database con `DailyLogEntity`, versión 1

### S2-04 · Crear `SistemaPreferences` (DataStore)
> **NUEVA** `data/local/datastore/SistemaPreferences.kt`
- Keys: `active_system_id`, `github_token`, `github_repo`, `phrases_json`
- Flow expuesto para cada preferencia

---

## SPRINT 3 — Capa de Datos: GitHub API

### S3-01 · Crear modelos de respuesta GitHub
> **NUEVA** `data/remote/github/GithubModels.kt`
- `data class GithubFile(name: String, path: String, downloadUrl: String)`

### S3-02 · Crear `GithubApiService` (Retrofit)
> **NUEVA** `data/remote/github/GithubApiService.kt`
- `@GET("repos/{owner}/{repo}/contents/sistemas")` → lista archivos
- `@GET` genérico para descargar contenido de un archivo

### S3-03 · Crear `GithubRepository`
> **NUEVA** `data/remote/github/GithubRepository.kt`
- `listSistemas(): List<GithubFile>`
- `downloadSistema(path: String): String` (devuelve el texto del .md)
- Cachea en `internal storage` con nombre del archivo

---

## SPRINT 4 — Domain: Modelos y Parser

### S4-01 · Crear modelo de dominio `Sistema`
> **NUEVA** `domain/model/Sistema.kt`
```kotlin
data class Sistema(
    val id: String,
    val nombre: String,
    val mantra: String,
    val accionDiminuta: String,
    val pasos: List<String>,
    val protocoloPeorDia: String,
    val faseActual: Int,
    val totalFases: Int = 3
)
```

### S4-02 · Crear modelo `DailyLog`
> **NUEVA** `domain/model/DailyLog.kt`

### S4-03 · Crear `SistemaParser`
> **NUEVA** `domain/model/SistemaParser.kt`
- Convierte el texto `.md` de un sistema en un objeto `Sistema`
- Parsea por headers H2 (`## 1.`, `## 2.`, etc.) basados en `plantilla-sistema.md`
- Extrae: mantra (buscando `**Nueva Identidad` o `**Mantra`), acción diminuta, pasos, protocolo del peor día

### S4-04 · Crear UseCases
> **NUEVA** `domain/usecase/GetActiveSistemaUseCase.kt`
> **NUEVA** `domain/usecase/ListSistemasUseCase.kt`
> **NUEVA** `domain/usecase/SaveDailyLogUseCase.kt`
> **NUEVA** `domain/usecase/GetWeekLogsUseCase.kt`

---

## SPRINT 5 — Inyección de Dependencias (Hilt)

### S5-01 · Crear `AppModule`
> **NUEVA** `di/AppModule.kt`
- Provee: `AppDatabase`, `DailyLogDao`, `SistemaPreferences`
- Provee: `Retrofit`, `GithubApiService`, `GithubRepository`

### S5-02 · Anotar `Application` con @HiltAndroidApp
> **NUEVA** `ElAhoraApplication.kt`

### S5-03 · Registrar Application en `AndroidManifest.xml`
> **EDIT** `AndroidManifest.xml`
- `android:name=".ElAhoraApplication"`
- Agregar permiso `INTERNET`

---

## SPRINT 6 — HomeScreen (UI + ViewModel)

### S6-01 · Crear `HomeViewModel`
> **NUEVA** `presentation/home/HomeViewModel.kt`
- `StateFlow<HomeUiState>` con: frase, sistema, todayLog, weekLogs, isLoading
- Expone: `onMarkDay(status)`, `onSaveNote(text)`, `onSwipeSystem(direction)`

### S6-02 · Crear `HomeScreen` — estructura base
> **NUEVA** `presentation/home/HomeScreen.kt`
- Scaffold + TopBar con nombre del sistema
- `HorizontalPager` para swipe entre sistemas y dot indicator

### S6-03 · Crear `SistemaCard` (Mantra)
> **NUEVA** `presentation/home/components/MantraCard.kt`
- `NeuCard` con fondo amarillo, texto del mantra

### S6-04 · Crear `AccionCard` (Acción de hoy)
> **NUEVA** `presentation/home/components/AccionCard.kt`
- `NeuCard` con fondo violeta, ícono ⚡, pasos del sistema activo

### S6-05 · Crear `ProgresoFases`
> **NUEVA** `presentation/home/components/ProgresoFases.kt`
- Fila con indicadores Fase 1 → 2 → 3, punto cian en fase actual

### S6-06 · Crear `TrackerHoy`
> **NUEVA** `presentation/home/components/TrackerHoy.kt`
- Botones `NeuButton`: "✅ Cumplí" (cian) y "❌ No cumplí" (blanco/borde negro)
- TextField para observaciones
- Muestra estado si ya fue marcado hoy

### S6-07 · Crear `DiasAnteriores`
> **NUEVA** `presentation/home/components/DiasAnteriores.kt`
- Sección colapsable con chips L M X J V S D
- Tap en un día sin marcar → abre bottom sheet para registrar

---

## SPRINT 7 — SettingsScreen

### S7-01 · Crear `SettingsViewModel`
> **NUEVA** `presentation/settings/SettingsViewModel.kt`
- Gestiona: token, repo, frases, carga de yo.md y reglas

### S7-02 · Crear `SettingsScreen`
> **NUEVA** `presentation/settings/SettingsScreen.kt`
- Sección Sincronización: campo token + botón refresh
- Sección Frases: lista editable
- Sección Documentos: rows para yo.md y reglas

### S7-03 · Crear `MarkdownReaderScreen`
> **NUEVA** `presentation/settings/MarkdownReaderScreen.kt`
- Usa Markwon para renderizar yo.md y reglas-del-sistema.md

---

## SPRINT 8 — Navegación y Wiring final

### S8-01 · Definir `AppNavigation`
> **NUEVA** `presentation/AppNavigation.kt`
- `NavHost` con rutas: `home`, `settings`, `reader/{docType}`

### S8-02 · Actualizar `MainActivity`
> **EDIT** `MainActivity.kt`
- Instalar `AppNavigation` + `ElAhoraTheme`

### S8-03 · Smoke test manual
- Instalar APK en emulador o dispositivo físico
- Verificar: apertura directa en Home, swipe entre sistemas, marcar día, abrir Settings

---

## Orden de Ejecución

```
S0 → S1 → S2 → S3 → S4 → S5 → S6 → S7 → S8
```

Cada Sprint debe compilar correctamente antes de pasar al siguiente.
