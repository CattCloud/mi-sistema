# Referencias del Orquestador SDLC Android

## Decisiones Arquitectónicas

Ver `Aclarador-orquestador.md` en la raíz del proyecto para el registro completo de decisiones validadas por el usuario.

## Inventario Completo de Skills

### Fase 1: Descubrimiento (PRD)
| Skill | Fuente | Rol |
|:------|:-------|:----|
| `prd` | github/awesome-copilot | Genera PRD formal |
| `brainstorming` | obra/superpowers | Refina visión y alcance |

### Fase 2: UI/UX
| Skill | Fuente | Rol |
|:------|:-------|:----|
| `figma-mcp-integration` | Figma MCP | **PRIMARY** — Wireframes y mockups |
| `excalidraw-diagram-generator` | github/awesome-copilot | **FALLBACK** — Bocetos rápidos |
| `compose-ui` | awesome-android-agent-skills | Material Design 3 patterns |
| `compose-navigation` | awesome-android-agent-skills | Navegación type-safe |
| `ui-ux-pro-max` | nextlevelbuilder | Principios UX avanzados |
| `interface-design` | dammyjay93 | Design tokens y sistema visual |
| `android-accessibility` | awesome-android-agent-skills | Checklist de accesibilidad |

### Fase 3: Arquitectura
| Skill | Fuente | Rol |
|:------|:-------|:----|
| `android-architecture` | awesome-android-agent-skills | Clean Architecture + Hilt |
| `android-data-layer` | awesome-android-agent-skills | Room + Retrofit + Offline-First |
| `android-retrofit` | awesome-android-agent-skills | Networking detallado |
| `android-gradle-logic` | awesome-android-agent-skills | Build system multi-módulo |
| `architecture-patterns` | wshobson/agents | Patrones complementarios |
| `api-design-principles` | wshobson/agents | Contratos REST/GraphQL |

### Fase 4: Sprint Plan
| Skill | Fuente | Rol |
|:------|:-------|:----|
| `writing-plans` | obra/superpowers | Tareas "bite-sized" 2-5 min |

### Fase 5: Coding
| Skill | Fuente | Rol |
|:------|:-------|:----|
| `executing-plans` | obra/superpowers | Motor de ejecución |
| `android-jetpack-compose-expert` | LOCAL | Kotlin + Compose + Material3 |
| `test-driven-development` | obra/superpowers | Red→Green→Refactor |
| `android-viewmodel` | awesome-android-agent-skills | StateFlow + lifecycle |
| `kotlin-concurrency-expert` | awesome-android-agent-skills | Coroutines |
| `coil-compose` | awesome-android-agent-skills | Carga de imágenes |
| `git-commit` | github/awesome-copilot | Conventional Commits |
| `systematic-debugging` | obra/superpowers | Debugging 4 fases |
| `using-git-worktrees` | obra/superpowers | Ramas aisladas |

### Fase 6: QA
| Skill | Fuente | Rol |
|:------|:-------|:----|
| `compose-performance-audit` | awesome-android-agent-skills | Recomposition storms |
| `android-testing` | awesome-android-agent-skills | Unit + Hilt + Roborazzi |
| `android_ui_verification` | LOCAL | E2E en emulador via ADB |
| `android-emulator-skill` | awesome-android-agent-skills | Automatización avanzada |
| `requesting-code-review` | obra/superpowers | Code review formal |
| `receiving-code-review` | obra/superpowers | Procesar feedback |
| `verification-before-completion` | obra/superpowers | Hard-gate de cierre |
| `security-best-practices` | supercent-io | OWASP Top 10 |

### Fase 7: Cierre
| Skill | Fuente | Rol |
|:------|:-------|:----|
| `finishing-a-development-branch` | obra/superpowers | Merge + cleanup |
| `documentation-writer` | github/awesome-copilot | Docs Diátaxis |
| `changelog-maintenance` | supercent-io | CHANGELOG estandarizado |
