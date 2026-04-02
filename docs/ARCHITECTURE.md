# 🏗️ Arquitectura Técnica — App Android: Mi Sistema Personal

> **Estado:** v1.2 — Actualizada (Sprint 14 completado)
> **Fecha:** 2026-03-21

---

## 1. Visión General

La app implementa **Clean Architecture** estructurada en 3 capas (Data → Domain → Presentation) usando **MVVM**, inyección de dependencias (Hilt), y flujos reactivos (Coroutines/StateFlow). La UI es completamente declarativa con **Jetpack Compose**.

---

## 2. Módulos y Paquetes

Namespace oficial: `com.misistema.elahora`

```text
com.misistema.elahora/
├── data/
│   ├── local/
│   │   ├── db/          # Room (AppDatabase, DAOs)
│   │   └── datastore/   # Preferencias de estado
│   ├── remote/
│   │   └── github/      # GithubApiService (Retrofit)
│   └── repository/      # Implementaciones (Local/Github RepositoryImpl)
├── domain/
│   ├── model/           # Sistema (JSON model), DailyLog
│   ├── repository/      # Interfaces de acceso a datos
│   └── usecase/         # Lógica pura (Sync, Export, GetActive...)
└── presentation/
    ├── home/            # HomeScreen, HomeViewModel + subcomponentes (Pager, Cards)
    ├── settings/        # SettingsScreen, SettingsViewModel
    ├── navigation/      # Grafo de navegación en Compose
    └── theme/           # Color tokens y `LocalSystemTheme` para Theming dinámico
```

---

## 3. Capa de Datos

### 3.1. Base de Datos (Room)
Tabla `daily_log` asegura única entrada (sistema + fecha).
Los Daos soportan escritura y lectura retrospectiva de días en la BD local.

### 3.2. DataStore (Preferencias)
Almacena estado UI general y configs en formato Flow asíncrono (Token PAT, nombre de Repositorio, ID del Sistema Activo).

### 3.3. Remote (Github REST API)
- **Path predeterminado:** Busca repositorios dentro del path configurado y lee el directorio exacto `elahora-data/`.
- **Parsing:** Extrae JSONs estructurados de sistemas (no Markdown).
- **Exportación:** Función `PUT` habilitada en `GithubApiService` permitiendo volcado del Room completo a `.json` externo por resguardo.

---

## 4. Capa de Dominio

### Modelos Nucleares:
1. `Sistema`: Modelo puramente serializable.
2. `DailyLog`: Registro representativo de un día con `id`, `systemId`, `status` y `notes`.

### Casos de Uso Activos:
- `ListSistemasUseCase` / `GetActiveSistemaUseCase`
- `ExportLogsToGithubUseCase` (Permite respaldo en nube)
- `GetWeekLogsUseCase`

---

## 5. Capa de Presentación

### Estilo Visual (UI/UX)
- Transición desde Neubrutalism a **Minimalista Dinámico**.
- Uso intensivo de `HorizontalPager` permitiendo la vista multistema.
- Implementación del modelo de `LocalSystemTheme` vía `CompositionLocalProvider` para cambiar los colores de TODA la aplicación (incluyendo TopBars y Botones) según el archivo JSON del sistema focalizado.
- Estructura de Atomic Design usando `ElAhoraCard`, `ElAhoraButton`, `ElAhoraInput`.

### HomeViewModel
- Sostiene UI State global. Carga el Pager y mantiene estado de todos los sistemas con sus logs actuales en `weekLogsMap`.

### SettingsViewModel
- Expone flujos separados en UI: Validaciones para conectividad de repo vacío (solo token), sincronización vs exportación.

---

## 6. Parsing Dinámico (Sistemas JSON)
La lectura ya no depende de Regex de Markdown complejo. Ahora el backend se estandarizó en objetos `.json` que mapean directamente a Kotlin Serialization, simplificando radicalmente la mantenibilidad.
