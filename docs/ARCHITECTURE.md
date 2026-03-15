# 🏗️ Arquitectura Técnica — App Android: Mi Sistema Personal

> **Estado:** Borrador v1.0 — Pendiente de aprobación
> **Fecha:** 2026-03-14

---

## 1. Visión General

La app sigue **Clean Architecture** en 3 capas (Data → Domain → Presentation) con patrón **MVVM** en la capa de presentación, implementado con Jetpack Compose. El flujo de datos es unidireccional.

```
┌─────────────────────────────────────────┐
│          PRESENTATION LAYER             │
│   Compose UI  ←→  ViewModel (StateFlow) │
└───────────────────┬─────────────────────┘
                    │ (usa UseCases)
┌───────────────────▼─────────────────────┐
│            DOMAIN LAYER                 │
│   UseCases / Models / Repository Interfaces │
└──────────┬─────────────────┬────────────┘
           │                 │
┌──────────▼──────┐ ┌────────▼───────────┐
│  DATA — Remote  │ │  DATA — Local      │
│  GitHub API     │ │  Room (DailyLog)   │
│  (Retrofit)     │ │  DataStore (prefs) │
└─────────────────┘ └────────────────────┘
```

---

## 2. Módulos y Paquetes

```
com.misistema.app/
├── data/
│   ├── local/
│   │   ├── db/          # Room Database + DAOs
│   │   └── datastore/   # Preferencias (sistema activo, frases)
│   └── remote/
│       └── github/      # Retrofit + GitHub API service
├── domain/
│   ├── model/           # Entidades puras (System, DailyLog)
│   ├── repository/      # Interfaces de repositorios
│   └── usecase/         # Lógica de negocio
└── presentation/
    ├── home/            # HomeScreen + HomeViewModel
    ├── settings/        # SettingsScreen + SettingsViewModel
    └── theme/           # Neubrutalism design tokens
```

---

## 3. Capa de Datos

### 3.1 Room — Base de Datos Local

**Tabla `daily_log`** (rastreo de cumplimiento por día y sistema):

```sql
CREATE TABLE daily_log (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    system_id   TEXT    NOT NULL,  -- ej: "01-calidad-sueno"
    date        TEXT    NOT NULL,  -- formato ISO: "2026-03-14"
    status      TEXT,              -- "DONE" | "NOT_DONE" | NULL
    notes       TEXT,              -- observaciones opcionales
    UNIQUE(system_id, date)
);
```

**DAOs:**
- `insertOrUpdateLog(log: DailyLog)`
- `getLogByDate(systemId: String, date: String): DailyLog?`
- `getLogsForWeek(systemId: String, weekStart: String): List<DailyLog>`

### 3.2 DataStore — Preferencias

| Clave | Tipo | Descripción |
|-------|------|-------------|
| `active_system_id` | String | ID del sistema activo actual |
| `github_token` | String | Personal Access Token (cifrado) |
| `github_repo` | String | "usuario/mi-sistema" |
| `motivational_phrases` | String (JSON) | Lista de frases del usuario |

### 3.3 GitHub API — Retrofit

**Base URL:** `https://api.github.com/`

| Endpoint | Método | Uso |
|----------|--------|-----|
| `repos/{owner}/{repo}/contents/sistemas` | GET | Listar sistemas disponibles |
| `repos/{owner}/{repo}/contents/{path}` | GET | Descargar contenido de un .md |

**Caché local:** Los `.md` descargados se guardan en `internal storage`. Se re-sincronizan en cada apertura (si hay red).

---

## 4. Capa de Dominio

**Modelos:**
```kotlin
data class Sistema(
    val id: String,        // "01-calidad-sueno"
    val nombre: String,    // "El Santuario del Sueño"
    val mantra: String,
    val accionDiminuta: String,
    val pasos: List<String>,
    val protocoloPeorDia: String,
    val faseActual: Int,   // 1, 2 o 3
    val totalFases: Int
)

data class DailyLog(
    val systemId: String,
    val date: LocalDate,
    val status: LogStatus?,  // DONE, NOT_DONE, null
    val notes: String?
)

enum class LogStatus { DONE, NOT_DONE }
```

**UseCases principales:**
- `GetActiveSistemaUseCase` — Descarga y parsea el .md activo
- `ListSistemasUseCase` — Lista los sistemas del repo
- `SaveDailyLogUseCase` — Guarda el registro del día
- `GetWeekLogsUseCase` — Obtiene los logs de la semana actual

---

## 5. Capa de Presentación

### HomeViewModel — StateFlow

```kotlin
data class HomeUiState(
    val frase: String,
    val sistema: Sistema?,
    val todayLog: DailyLog?,
    val weekLogs: List<DailyLog>,
    val isLoading: Boolean
)
```

- **Swipe entre sistemas:** `pager` con `HorizontalPager` de Accompanist/Compose.
- **Navegación:** `NavHost` con 2 destinos: `home` y `settings`.

---

## 6. Librerías Clave

| Librería | Versión aprox. | Uso |
|---------|---------------|-----|
| Jetpack Compose BOM | 2024.x | UI |
| Room | 2.6.x | Base de datos local |
| DataStore Preferences | 1.1.x | Preferencias simples |
| Retrofit + OkHttp | 2.11.x / 4.x | GitHub API |
| Hilt | 2.51.x | Inyección de dependencias |
| Accompanist Pager | — | HorizontalPager para swipe |
| Markwon | 4.6.x | Fallback render Markdown (yo.md, reglas) |
| Kotlinx Serialization | 1.6.x | Parsear JSON de respuestas GitHub |

---

## 7. Parsing de Documentos Markdown

La app **NO renderiza el Markdown crudo** de los sistemas — lo parsea como estructura:

```
# ⚙️ Sistema 01: El Santuario del Sueño   → nombre
## 1. Identidad y Propósito               → sección mantra
## 2. El Espectro de la Acción            → accionDiminuta + fases
## 3. El Motor del Sistema                → pasos[]
## 4. Protección y Supervivencia          → protocoloPeorDia
## 5. Medición Visual y Auditoría         → (solo referencia)
```

Parser: custom regex/string parser sobre el contenido del archivo. Basado en los headers H2 de la `plantilla-sistema.md`.

Los documentos `yo.md` y `reglas-del-sistema.md` sí se muestran con **Markwon** (renderizado visual de Markdown) en la pantalla de Settings.
