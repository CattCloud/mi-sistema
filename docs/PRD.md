# 📋 PRD — App Android: Mi Sistema Personal

> **Estado:** Aprobado v1.2 — Actualizado al cierre del Sprint 14+
> **Fecha:** 2026-03-21
> **Autor:** Orquestador SDLC Android

---

## 1. Visión General

### Problema a resolver

El usuario tiene un sistema de vida documentado en archivos (alojados en GitHub) y actualmente debe abrir su computador para consultarlos. No existe una forma móvil, rápida y orientada al día presente para:
1. **Leer** qué dice su sistema (normas, proceso, protocolos).
2. **Registrar** si cumplió o no con la acción del sistema ese día, con notas opcionales.

La app elimina esa fricción y materializa el principio del sistema: _"Enfócate solo en hoy."_

### Solución

Aplicación Android nativa, exclusivamente para uso personal, que sincroniza sistemas desde GitHub en formato JSON y provee un tracker diario minimalista con frase motivacional contextualizada. Adicionalmente permite exportar el avance nuevamente hacia GitHub.

---

## 2. Usuario

**Único usuario:** El creador del sistema.

Perfil psicológico a tener en cuenta para el diseño de UX:
- Tendencia a pensar en el futuro y el pasado → la app debe anclar al usuario en el **día presente**.
- Patrón de "reinicio total" ante fallos → la app **NO mostrará rachas acumuladas** ni estadísticas que generen presión. Solo el día de hoy.
- Boss 2 (Analista Paralizante) → la app debe tener **cero fricción** para abrir y registrar.

---

## 3. Funcionalidades del MVP

### 3.1 Pantalla Principal (El Día de Hoy)

- Al abrir la app, el último sistema activo se muestra **inmediatamente**. Cero fricción.
- **Frase de anclaje al presente** (ej. "Hoy es un nuevo día. Solo esto") asociada a la configuración JSON del sistema.
- **Día de la Semana** visible pero sin presión de rachas.
- **Deslizamiento horizontal (HorizontalPager):** Permite cambiar rápidamente el sistema activo.
- **Sistema de diseño dinámico:** El tema de colores cambia dinámicamente según el sistema focalizado (ej. colores basados en estado de sueno o cuerpo).
- **Tracker del día:**
  - `✅ Sí` o `❌ No` grande y accesible.
  - Campo de observaciones.
- **Días Anteriores:** Sección colapsable que muestra los registros de los últimos días en un chip list interactivo, permitiendo registro retroactivo sin límite real en visual.

### 3.2 Visualización Estructurada del Sistema

El sistema es consumido desde GitHub como un `.json` y se presenta en componentes nativos:
- **Identidad y Mantra:** Card inicial altamente visible.
- **Acción Ideal y Diminuta:** El objetivo puntual del día.
- **El Proceso Exacto** (pasos): Mini rutina mostrada como lista.
- **Protocolo del Peor Día:** Medidas para cuando el dia va mal.
- Todo bajo una capa estética Sereno/Minimalista.

### 3.3 Configuración (Pantalla secundaria)

- **Input de Configuración GitHub:**
  - Repositorio asociado (owner/repo).
  - Token PAT (obligatorio solo para exportar, opcional para sincronizar sistemas públicos).
- **Control de Sincronización:** Botones para descargar últimas configuraciones de sistemas.
- **Control de Exportación:** Botón para exportar todos los registros (`daily_logs`) como JSON directo en una rama/commit en el repo de GitHub.
- **Selector manual de Sistema Activo:** Permite definir un sistema explícito en lugar del swipe.

---

## 4. Fuente de Datos: GitHub

- Los sistemas se estructuran como `.json` y viven en la subcarpeta `elahora-data/` del repo en GitHub.
- Retrofit se comunica con GitHub REST API para:
  - Listar archivos en `elahora-data/`.
  - Descargar los `.json` crudos.
  - Subir la base de datos de logs exportados.
- Los archivos `.json` se **cachean localmente** permitiendo funcionalidad Offline-First.
- Logs locales (SQLite/Room) respaldando la BD.

---

## 5. Flujo Principal de Uso (User Journey)

```text
[Abrir app]
    → Ver pantalla principal con la frase o cita del sistema activo.
    → Deslizar en Pager horizontal para saltar a otro sistema (si se requiere).
    → [Acción del día] Marcar ✅ o ❌ + escribir observación.
    → [Opcional] Clic en Configuración → "EXPORTAR REGISTROS A GITHUB"
    → [Listo] App cerrada.
```

---

## 6. Pantallas

| Pantalla | Descripción |
|----------|-------------|
| `HomeScreen` | Pager principal, frase del día, sistema activo, tracker del día, días anteriores colapsables. |
| `SettingsScreen` | Flujo de Github (conectar, descargar sistemas, exportar logs), selección explícita del sistema. |
| `MarkdownReaderScreen` | (Residual) Usada temporalmente para leer archivos como `yo.md`. |

---

## 7. Restricciones y No-Features (MVP)

- ❌ **Sin notificaciones push**
- ❌ **Sin modo escritura** de configuraciones desde la app (solo creación en github → json).
- ❌ **Sin rachas ni presión**.
- ✅ **Exportación total:** Aprobada la exportación de logs para resguardo de la data propia.
- ✅ **Completamente nativa/offline**.
