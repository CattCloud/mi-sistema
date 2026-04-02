# 📋 Sprint Plan — ElAhora (Mi Sistema Personal)

> **Estado:** v1.2 — Histórico de ejecución
> **Proyecto:** `c:\consciente\mi-sistema\ElAhora`

---

*Nota: Los Sprints 0 al 8 abarcaron la base original estructurada (Room, Retrofit, Datastore, etc). El enfoque metodológico derivó en refactorizaciones de las capas de diseño a partir de iteraciones continuas. Este registro abarca el estado final de las tareas clave ejecutadas en las Fases Avanzadas.*

## SPRINT Avanzado — JSON Migration & Logic Fix
- **HECHO** - Migrar lectura de sistemas de formato Markdown `.md` a formato serializable `.json`.
- **HECHO** - Actualizar ruta de GitHub a `elahora-data/` para separar la lectura nativa de la app de los archivos personales MD.
- **HECHO** - Reestructurar `SettingsViewModel` para diferenciar Sincronización de Lectura (requiere Repo) de Exportación Voluntaria (requiere Token PAT + escritura).
- **HECHO** - Implementar `ExportLogsToGithubUseCase` permitiendo volcado de base de datos a `.json` en Gist/Repo externo.

## SPRINT Avanzado — Theming y Rediseño Minimalista (Sprint 14)
- **HECHO** - Abandonar esquema Neubrutalism por esquema Minimalista/Sereno. Crear tipografía `Nunito` y estructura `SystemTheme`.
- **HECHO** - Implementar `LocalSystemTheme` vía `CompositionLocalProvider` para cambiar los colores de Acento basados en los metadatos JSON del sistema focalizado.
- **HECHO** - Crear `ElAhoraCard`, `ElAhoraInput`, `ElAhoraButton`, reemplazando antiguos componentes visuales (`NeuCard`).
- **HECHO** - Implementar `HorizontalPager` en `HomeScreen` permitiendo swipe dinámico lateral para cambiar entre contextos de Sistemas ruteados por ID (ej. `01-sueno`).

## SPRINT Final — Bugfixing y UX Layout
- **HECHO** - Solucionar problemas de firma en `downloadSistema`.
- **HECHO** - Arreglo de validaciones nulas en tokens y carga de UI State.
- **HECHO** - Ajuste de layout final en Header de Configuración (separación visual formal entre "Sincronización Github" y "Selección Sistema Activo").
- **HECHO** - Posicionamiento de flecha Back `←` en NavigationTopBar.
- **HECHO** - Reubicación de Grid Box layout en iconos de header central (`HomeScreen`).

## Tareas Pendientes (Backlog Histórico Opcional)
- Refinamiento de Accesibilidad.
- Pruebas E2E de Testing Unitario en ViewModels que quedaron desplazadas por la velocidad de iteración.
