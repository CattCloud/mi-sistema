# 📋 PRD — App Android: Mi Sistema Personal

> **Estado:** Borrador v1.1 — Actualizado con clarificaciones. Pendiente de aprobación del usuario.
> **Fecha:** 2026-03-14
> **Autor:** Orquestador SDLC Android

---

## 1. Visión General

### Problema a resolver

El usuario tiene un sistema de vida documentado en archivos Markdown (alojados en GitHub) y actualmente debe abrir su computador para consultarlos. No existe una forma móvil, rápida y orientada al día presente para:
1. **Leer** qué dice su sistema (normas, proceso, protocolos).
2. **Registrar** si cumplió o no con la acción del sistema ese día, con notas opcionales.

La app elimina esa fricción y materializa el principio del sistema: _"Enfócate solo en hoy."_

### Solución

Aplicación Android nativa, exclusivamente para uso personal, que sincroniza los documentos del sistema desde GitHub y provee un tracker diario minimalista con frase motivacional contextualizada.

---

## 2. Usuario

**Único usuario:** El creador del sistema.

Perfil psicológico a tener en cuenta para el diseño de UX:
- Tendencia a pensar en el futuro y el pasado → la app debe anclar al usuario en el **día presente**.
- Patrón de "reinicio total" ante fallos → la app **NO mostrará rachas acumuladas** ni estadísticas que generen presión. Solo el día de hoy.
- Boss 2 (Analista Paralizante) → la app debe tener **cero fricción** para abrir y registrar.

---

## 3. Funcionalidades del MVP

### 3.1 Pantalla Principal — El Día de Hoy

> **[CLARIFICACIÓN v1.1]** Al abrir la app, el sistema activo se muestra **inmediatamente**. Sin pantallas de bienvenida, sin selección previa. Cero fricción.

- **Frase de anclaje al presente** en la parte superior (rotativa, configurable). Ej:
  - _"Hoy es un nuevo día."_
  - _"Enfócate solo en hoy. 1% mejor."_
  - _"No hay ayer aquí. Solo esto."_
- **Fecha actual** visible (sin semáforo visual de racha).
- **Sección del sistema** — Ver §3.2 para el diseño visual estratégico.
- **Tracker del día (sección inferior):**
  - Tarjeta destacada con estado del día: `[Sin marcar]` / `✅ Cumplí` / `❌ No cumplí`.
  - Campo de texto opcional para observaciones (~200 caracteres).
  - Una vez marcado, cambia al estado correspondiente (editable en el mismo día).

**Swipe horizontal para cambiar de sistema:**
> **[CLARIFICACIÓN v1.1]** Deslizar el dedo a la derecha o izquierda en la pantalla principal carga el siguiente/anterior sistema. El cambio es instantáneo y persiste como "sistema activo". Elimina el botón de selección de sistema en la pantalla principal.

**Rastreo retroactivo (acceso al historial de días):**
> **[CLARIFICACIÓN v1.1]** Si el usuario no pudo marcar un día pasado por falta de tiempo, **puede hacerlo retroactivamente**. Al final de la pantalla principal habrá una sección colapsable "Días anteriores" con los últimos días de la semana actual, donde puede entrar a cualquier día sin marcar y registrarlo. La restricción anterior (sin edición retroactiva) queda **eliminada** del MVP.

### 3.2 Visualización Estructurada del Sistema

> **[CLARIFICACIÓN v1.1]** El contenido del sistema **NO se muestra como Markdown crudo**. La app interpreta la estructura del documento (basada en la `plantilla-sistema.md`) y usa **componentes nativos de Android** para presentarlo de forma visual y estratégica.

Estructura de visualización por sección:

| Sección del doc | Componente Android | Prioridad visual |
|---|---|---|
| **Identidad y Propósito** (Mantra + Por qué) | `Card` con fondo resaltado (color de acento) | 🔝 ALTA — siempre visible primero |
| **Acción Diminuta (Fase actual)** | `Card` con icono de rayo ⚡ + texto grande | 🔝 ALTA — el "qué hacer hoy" |
| **El Proceso Exacto** (pasos) | Lista numerada con `StepIndicator` | MEDIA |
| **Protocolo del Peor Día** | `Card` colapsable con icono de escudo 🛡️ | MEDIA |
| **Motor / Detonador** | Chip o badge pequeño en la tarjeta de acción | BAJA |
| **Checkpoints de Progresión** | Barra de progreso visual (Fase 1→2→3) | BAJA |

- La sección de **Identidad** y **Acción Diminuta** siempre son las más prominentes visualmente.
- El resto de secciones están disponibles haciendo scroll hacia abajo.
- Solo lectura.

### 3.3 Cambio de Sistema

- **Swipe horizontal** en la `HomeScreen` → carga el siguiente/anterior sistema (ver §3.1).
- Alternativa: un indicador de puntos (dot indicator) en la parte inferior muestra cuántos sistemas hay y en cuál está el usuario.
- El sistema activo persiste localmente.

### 3.4 Configuración (Pantalla secundaria)

- **Documentos complementarios:** Acceso de lectura a `yo.md` y `reglas-del-sistema.md` (renderizados como Markdown).
- **Frases motivacionales:** Lista editable de frases de anclaje.
- **Autenticación GitHub:** Token personal (PAT) para sincronizar el repositorio.

---

## 4. Fuente de Datos: GitHub

- Los archivos `.md` viven en el repositorio privado de GitHub del usuario.
- La app usa la **GitHub REST API** (autenticada con un Personal Access Token) para:
  - Listar archivos en la carpeta `sistemas/`.
  - Descargar el contenido de los archivos `.md` solicitados.
- Los archivos se **cachean localmente** para permitir lectura sin internet.
- El tracker diario (registros Sí/No + observaciones) se almacena **100% local** en Room (SQLite). No se sube a GitHub.

---

## 5. Flujo Principal de Uso (User Journey)

```
[Abrir app]
    → Ver pantalla principal con frase de anclaje + fecha de hoy
    → Ver sistema activo (nombre)
    → [Opcional] Tocar nombre del sistema → Ver documento completo
    → [Acción del día] Marcar ✅ o ❌ + escribir observación
    → [Listo] App cerrada. Se registró el día.
```

---

## 6. Pantallas (Lista preliminar)

| Pantalla | Descripción |
|----------|-------------|
| `HomeScreen` | Frase del día, fecha, sistema activo (visual), tracker del día, días anteriores colapsables |
| `SettingsScreen` | yo.md, reglas, frases, token GitHub |

**Nota:** `SystemListScreen` eliminada del MVP. El cambio de sistema se hace con **swipe horizontal** directamente en la `HomeScreen`.

---

## 7. Restricciones y No-Features (MVP)

- ❌ **Sin notificaciones push** en el MVP (no se pide aún).
- ❌ **Sin estadísticas ni rachas** (evita presión psicológica).
- ✅ **Rastreo retroactivo** para días de la semana en curso sin marcar (ver §3.1).
- ❌ **Sin modo escritura** desde la app (solo lectura de GitHub).
- ❌ **Sin publicación en Play Store** (APK personal).
- ✅ **Soporte offline:** los documentos se cachean. El tracker siempre funciona offline.

---

## 8. Plataforma y Tecnología (orientación para Fase 3)

- **Android nativo** (Kotlin + Jetpack Compose).
- **Almacenamiento local:** Room (tracker diario) + DataStore (preferencias).
- **Red:** Retrofit + GitHub REST API.
- **Renderizado Markdown:** Librería open-source (ej. `Markwon`).
- **Arquitectura:** MVVM + Clean Architecture (a definir en Fase 3).

---

## 9. Criterios de Éxito del MVP

1. El usuario puede abrir la app e inmediatamente ver la frase del día y marcar su cumplimiento en menos de **10 segundos**.
2. El documento del sistema activo es legible y navegable desde el celular.
3. El registro persiste aunque se cierre y reabra la app.
4. La app funciona sin internet si los documentos ya fueron descargados.
