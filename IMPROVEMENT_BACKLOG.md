# 📋 Backlog de Evolución: Orquestador SDLC Android

> **Propósito:** Este documento recopila situaciones reales, problemas operativos y déficits (gaps) identificados durante el uso empírico del skill `orquestador-sdlc-android`. Sirve como insumo base (input) para iterar y hacer el skill más robusto en el futuro.

---

## 📝 Registro de Análisis 01: El "Problema del Día 2" y Deriva Documental

- **Fecha de registro:** 2026-04-02
- **Proyecto de contexto:** El Ahora (Android)
- **Fase afectada:** Posterior a Fase 5 / Mantenimiento continuo.

### 📌 Situación
El framework del orquestador se desempeñó de manera impecable y estructurada durante la construcción desde cero (fase *Greenfield*). El proyecto alcanzó el éxito de su MVP inicial. Inmediatamente después, el usuario solicitó cambios estructurales profundos (nuevo diseño minimalista en reemplazo del inicial, migración del parseo de `.md` a objetos estructurados `.json`, adición de feature de exportación de logs).

### 🚨 Problema Encontrado
1. **Disolución de Rol (Amnesia del Skill):** Al enfrentarse a características que no estaban en el PRD original y que se solicitaron de forma conversacional, el agente abandonó silenciosamente su rol estricto. Dejó de mostrar el panel de control de `[VALIDAR] / [SIGUIENTE]`, y retornó a un modo de "coding assistant" estándar.
2. **Documental Drift masivo:** Como consecuencia de lo anterior, se desarrollaron features complejas en vivo sin actualizar el `PRD.md` ni `ARCHITECTURE.md`, lo que desfasó gravemente la documentación con la realidad del código fuente.

### 💡 Aspectos que le faltan al Skill (Para Futuras Versiones)
- **Bucle de Mantenimiento Evolutivo:** Necesita instrucciones precisas para proyectos de *Brownfield* o en fase de post-producción.
- **Protocolo de Micro-Ciclos:** Capacidad para decir: *"Detecto una nueva funcionalidad grande. Reabriremos brevemente Fase 1 para ajustar PRD, luego Fase 4 para Micro-Sprint, y comenzamos"*.
- **Auditoría Documental:** Un paso obligatorio en la Fase 7 (o en paradas de feature) donde el agente lea el código y confronte obligatoriamente si el `ARCHITECTURE.md` es coherente con lo construido.
