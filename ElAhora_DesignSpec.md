**El Ahora**

*Guía de Especificaciones de Diseño UI*

Versión 1.0  ·  Android nativo (Kotlin + Jetpack Compose)

Marzo 2026


# **1. Principios de Diseño**
Estos principios guían cada decisión visual. Antes de implementar cualquier componente, verificar que cumple con todos.

### **1.1 Cero fricción**
La app debe abrirse y mostrar la acción del día en menos de 10 segundos. Ningún elemento decorativo, animación de bienvenida, ni pantalla intermedia debe retrasar al usuario.

### **1.2 Anclaje al presente**
Solo se muestra información del día actual y de la semana en curso. No hay estadísticas acumuladas, rachas, ni métricas que generen presión psicológica. La jerarquía visual siempre prioriza la Acción Diminuta del día.

### **1.3 Color como identidad del sistema**
Cada sistema personal tiene un color representativo único. Ese color impregna la app cuando ese sistema está activo — no como decoración, sino como señal de identidad. El fondo base y los acentos cambian según el sistema activo.

### **1.4 Minimalismo funcional**
Fondo blanco limpio. Cards blancas con sombra sutil para separar secciones — sin bordes ni separadores de línea entre secciones principales. Solo lo esencial en pantalla. El color del sistema aparece en puntos estratégicos, no en todos lados.


# **2. Paleta de Colores**
La paleta se divide en dos capas: colores base (fijos para toda la app) y colores de sistema (variables según el sistema activo). El ejemplo de referencia usa el Sistema 01 — El Santuario del Sueño, cuyo color representativo es el morado.

## **2.1 Colores base (fijos)**

**Fondo de pantalla principal**

||**Fondo base — Blanco frío neutro**   #F7F7F9|
| :- | :- |

**Texto principal (títulos, contenido)**

||**Texto primario — Casi negro azulado**   #1A1A2E|
| :- | :- |

**Cards y superficies elevadas**

||**Card surface — Blanco puro**   #FFFFFF|
| :- | :- |

**Card Peor Día (siempre ámbar, independiente del sistema)**

||**Ámbar fondo — Zona de emergencia**   #FFF8EC|
| :- | :- |
||**Ámbar texto — Título y cuerpo dentro de la card**   #8B5E10|

## **2.2 Colores de sistema (variables · Ejemplo: Sistema Sueño)**
Estos colores cambian cuando el usuario cambia de sistema activo mediante swipe. El desarrollador debe implementarlos como un objeto de tema inyectable.

||**Acento principal — Botones, íconos activos, labels de acento**   #7B5EA7|
| :- | :- |
||**Acento oscuro — Card Identidad (fondo sólido), títulos de sección**   #3D2B6B|
||**Acento claro — Fondos de inputs, círculos numerados, hover states**   #EDE8F5|
||**Acento medio — Labels en mayúsculas, texto muted, placeholders**   #9B85C4|

## **2.3 Colores de estado (fijos, semánticos)**
Usados en los círculos de días y botones de registro. No cambian con el sistema.

||**Cumplido — Fondo del círculo y botón activo**   #D4F0E0|
| :- | :- |
||**Cumplido — Ícono de check y texto activo**   #1A5C38|
||**No cumplido — Fondo del círculo y botón activo**   #FAD8D8|
||**No cumplido — Ícono de X y texto activo**   #8B2020|


# **3. Tipografía**
Fuente única en toda la app: Nunito (Google Fonts). Redondeada y amigable, buena legibilidad en tamaños pequeños en mobile.

*→  Importar desde Google Fonts: family=Nunito:wght@400;500;600*

*→  En Jetpack Compose: usar downloadable fonts o incluir el .ttf en res/font/*

|**Elemento**|**Valor / Color**|**Notas para el desarrollador**|
| :- | :- | :- |
|**Label de sección**|10sp / 400 / MAYÚS / tracking 0.1em|*Color acento medio (#9B85C4). Ej: 'IDENTIDAD', 'ACCIÓN DIMINUTA HOY'*|
|**Frase motivacional**|13sp / 400 / itálica|*Color texto secundario (#9B85C4). Siempre entre comillas.*|
|**Acción Diminuta (título)**|24sp / 600|*Color texto primario (#1A1A2E). El elemento más grande en pantalla.*|
|**Subtítulo acción**|13sp / 500|*Color acento principal (#7B5EA7). Ej: '+ Mini-Rutina de Bajada'*|
|**Cuerpo de cards**|13sp / 500|*Color texto primario (#1A1A2E). Identidad, pasos del proceso.*|
|**Texto muted / hint**|13sp / 400|*Color #B0A8C8. Placeholders, estados 'Sin marcar'.*|
|**Registro — título**|13sp / 600|*Color texto primario (#1A1A2E). Ej: 'Registro — Domingo (hoy)'*|
|**Botón Guardar**|14sp / 600|*Color blanco puro (#FFFFFF) sobre fondo acento (#7B5EA7).*|
|**Números de pasos**|11sp / 600|*Color acento principal (#7B5EA7) dentro de círculo claro.*|
|**Días de la semana (L M Mi...)**|10sp / 400|*Color acento medio (#9B85C4).*|
|**Texto 'hoy' en círculo**|9sp / 600|*Color texto primario (#1A1A2E).*|
|**Card Peor Día — título**|11sp / 600 / MAYÚS / tracking 0.06em|*Color ámbar oscuro (#8B5E10).*|
|**Card Peor Día — cuerpo**|12sp / 400|*Color ámbar texto (#7A5218). Line height 1.4.*|


# **4. Espaciado y Layout**
La app usa un sistema de espaciado basado en múltiplos de 4dp. El contenido vive en un scroll vertical continuo sin paginación.

|**Elemento**|**Valor / Color**|**Notas para el desarrollador**|
| :- | :- | :- |
|**Padding lateral de pantalla**|20dp a cada lado|*Margen consistente en todo el scroll.*|
|**Padding superior (primer elemento)**|28dp|*Desde el borde superior de la pantalla hasta la fecha.*|
|**Gap entre cards principales**|10dp|*Espacio vertical entre cada card o sección.*|
|**Padding interno de cards**|14dp vertical / 16dp horizontal|*Espacio interno de todas las cards blancas.*|
|**Gap entre pasos del proceso**|0dp (solo padding interno)|*Los pasos se separan con un divider de 1dp color #F0EDF8.*|
|**Gap entre botones Cumplí / No cumplí**|8dp|*Flex row con gap fijo.*|
|**Margen inferior del botón Guardar**|0dp|*Es el último elemento de la card de registro.*|
|**Gap entre sección días y card registro**|10dp|*Consistente con el resto del layout.*|


# **5. Componentes**

## **5.1 Header de pantalla**
No es un AppBar nativo de Android. Es contenido libre dentro del scroll, sin elevación ni sombra.

- Izquierda: fecha en label de sección (10sp, mayús, acento medio) + frase motivacional debajo en 13sp itálica.
- Derecha: ícono del sistema activo en círculo de 38dp con fondo acento claro y sombra sutil (elevation 2dp). Debajo del círculo, dot indicator de sistemas (3 puntos de 6dp, punto activo en acento principal, inactivos en acento claro).
- NO usar TopAppBar de Material. NO header fijo. Todo scrollea junto con el contenido.

## **5.2 Card Identidad**
Única card con fondo sólido del color del sistema. Es el ancla visual más fuerte de la pantalla.

- Fondo: acento oscuro (#3D2B6B para el sistema sueño).
- Texto: blanco puro #FFFFFF, 13sp, weight 500, line height 1.5.
- Label 'IDENTIDAD' en 10sp, mayús, color #B8A8E0 (blanco opacado, no acento medio).
- Border radius: 18dp. Sin borde. Box shadow: elevation 4dp con color del acento al 25% opacidad.
- Margen inferior: 10dp hacia la siguiente sección.

## **5.3 Sección Acción Diminuta**
No es una card — es texto libre sobre el fondo gris. Genera contraste por tamaño, no por contenedor.

- Label superior: 'ACCIÓN DIMINUTA HOY · FASE 1' en 10sp, mayús, color acento medio.
- Título principal: 24sp, weight 600, color texto primario. Es el texto más grande de la pantalla.
- Subtítulo: 13sp, weight 500, color acento principal. Ej: '+ Mini-Rutina de Bajada (6 min)'.
- Sin card contenedora. Margin bottom 14dp hacia la card de pasos.

## **5.4 Card de Pasos del Proceso**
Card blanca con los pasos numerados. Solo los pasos que aplican al día actual (el sistema define cuáles).

- Fondo: blanco #FFFFFF. Border radius 18dp. Box shadow: elevation 2dp, color #00000012.
- Cada paso: fila con círculo numerado (22dp diámetro, fondo acento claro, número en acento principal 11sp 600) + texto 13sp 400 color texto primario.
- Separador entre pasos: línea de 1dp, color #F0EDF8 (acento muy claro). Sin separador después del último paso.
- Padding interno de cada fila: 8dp arriba y abajo, 0dp lateral (el padding lo da la card).

## **5.5 Selector de días de la semana**
Fila horizontal de 7 botones táctiles (L M Mi J V S D). Selector, no navegación.

- Cada ítem: label de día (10sp, acento medio) encima de un círculo de 26dp de diámetro.
- Los 7 círculos deben caber en el ancho de pantalla (340dp aprox). Gap entre ítems: 4dp. Usar weight distribuido en flex row.
- Estado sin marcar: fondo acento claro (#EDE8F5), sin ícono.
- Estado cumplido: fondo verde (#D4F0E0), ícono check (10dp) en verde oscuro (#1A5C38).
- Estado no cumplido: fondo rojo claro (#FAD8D8), ícono X (10dp) en rojo (#8B2020).
- Estado seleccionado activo: ring de 2dp en acento principal (#7B5EA7) alrededor del círculo, fondo blanco.
- Al tocar un día: el bloque de registro de abajo se actualiza con los datos de ese día. Por defecto carga 'hoy'.

## **5.6 Card de Registro del Día**
Bloque de captura del registro. Siempre editable para el día seleccionado en el selector de días.

- Header: título 'Registro — [Nombre del día]' (13sp 600, texto primario) a la izquierda + estado actual (13sp, color muted) a la derecha.
- Dos botones de estado (Cumplí / No cumplí): flex row, gap 8dp, height 44dp, border radius 12dp.
- Estado inactivo: fondo acento claro (#F0EDF8), texto acento principal, sin borde.
- Estado activo Cumplí: fondo #D4F0E0, texto #1A5C38, weight 600.
- Estado activo No cumplí: fondo #FAD8D8, texto #8B2020, weight 600.
- Input de nota: fondo gris base (#F7F7F9), sin borde, border radius 10dp, 10dp padding vertical, 12dp horizontal. Placeholder 'Nota del día (opcional)' en color muted.
- Botón Guardar registro: ancho completo, 13dp padding vertical, border radius 14dp, fondo acento principal (#7B5EA7), texto blanco 14sp 600. Box shadow elevation 3dp con color acento al 35%.
- IMPORTANTE: el guardado NUNCA es automático. Solo ocurre al presionar 'Guardar registro'. Mientras el usuario escribe en el input, no se dispara ningún guardado ni evento de actualización.
- Si se intenta guardar sin seleccionar estado: mostrar mensaje de error debajo del botón ('Selecciona Cumplí o No cumplí primero') en 11sp, color acento principal.

## **5.7 Card Peor Día**
Siempre visible al fondo de la pantalla. Recordatorio del protocolo de emergencia del sistema.

- Fondo: ámbar cálido (#FFF8EC). Border radius 16dp. Box shadow elevation 2dp color #0000000D.
- Ícono de escudo (16dp) en color ámbar oscuro (#B07A20) alineado al top izquierdo.
- Título 'PEOR DÍA': 11sp, weight 600, mayús, tracking 0.06em, color #8B5E10.
- Cuerpo: 12sp, weight 400, color #7A5218, line height 1.4. Texto fijo que viene del campo 'Protocolo del Peor Día' del documento del sistema.
- Esta card NO forma parte de la lógica de registro. Es solo lectura.

## **5.8 Ícono del sistema activo**
Círculo en el header que representa visualmente el sistema activo. NO es un botón de menú ni un indicador de modo.

- Tamaño del círculo: 38dp de diámetro.
- Fondo: acento claro del sistema (#EDE8F5 para sueño). Box shadow elevation 2dp.
- El ícono dentro varía por sistema: luna para sueño, pesa para ejercicio, etc. Tamaño del ícono: 18dp.
- Color del ícono: acento principal del sistema (#7B5EA7 para sueño).
- Dot indicator debajo: 3 puntos de 6dp con gap de 4dp. Punto activo en acento principal, puntos inactivos en acento claro.


# **6. Comportamiento e Interacciones**

## **6.1 Cambio de sistema (swipe horizontal)**
- Deslizar el dedo a la izquierda o derecha en cualquier parte de la HomeScreen carga el sistema anterior o siguiente.
- El cambio es instantáneo: el ícono del header, la card de Identidad, los colores del tema y el contenido del sistema se actualizan juntos.
- El dot indicator refleja el sistema activo. El sistema activo persiste en DataStore (no se reinicia al cerrar la app).
- NO usar un drawer lateral ni un selector de pantalla separada para cambiar sistemas.

## **6.2 Selector de días**
- Al abrir la app, siempre carga el día actual como seleccionado.
- Al tocar un día diferente: el título de la card de registro cambia al nombre del día, y el estado y nota se cargan desde Room.
- El ring de selección (2dp, acento principal) sigue al día tocado.
- El usuario puede editar cualquier día de la semana en curso en cualquier momento.

## **6.3 Guardado del registro**
- El estado (Cumplí / No cumplí) se selecciona tocando el botón correspondiente. El botón activo cambia de color inmediatamente como feedback visual.
- El texto en el input de nota se escribe libremente. No hay auto-guardado ni debounce.
- Al tocar 'Guardar registro': validar que hay un estado seleccionado → guardar en Room → actualizar el círculo del día en el selector → mostrar mensaje de confirmación.
- El registro es editable en cualquier momento. Volver a tocar un día ya registrado carga sus datos y permite modificarlos.

## **6.4 Scroll**
- La pantalla principal es un scroll vertical continuo. Sin secciones fijas, sin bottom sheets, sin paginación.
- NO hay header fijo (AppBar) ni footer/navigation bar en la HomeScreen.
- El contenido va desde el borde superior hasta el final de la card Peor Día.


# **7. Sistema de Temas por Sistema**
Cada sistema tiene un objeto de tema con 4 valores de color. Al cambiar de sistema activo, la app aplica el nuevo tema globalmente.

**Estructura del objeto SystemTheme:**

**accentMain**   →  Color principal: botones, dot activo, ring de selección, subtítulo de acción.

**accentDark**   →  Color oscuro: fondo de card Identidad, títulos de sección del documento.

**accentLight**   →  Color claro: fondo del ícono del sistema, círculos de pasos, inputs, círculos de días sin marcar.

**accentMid**   →  Color medio: labels en mayús, texto muted, placeholders.

**Sistema 01 — El Santuario del Sueño (referencia):**

|**Elemento**|**Valor / Color**|**Notas para el desarrollador**|
| :- | :- | :- |
|**accentMain**|#7B5EA7|*Morado medio. Botones principales, selección activa.*|
|**accentDark**|#3D2B6B|*Morado oscuro. Fondo card Identidad, títulos.*|
|**accentLight**|#EDE8F5|*Lila muy claro. Fondos secundarios, inputs.*|
|**accentMid**|#9B85C4|*Morado suave. Labels, texto muted.*|


# **8. Restricciones de Implementación**
Lista de elementos que NO deben aparecer en la HomeScreen bajo ninguna circunstancia.

- Sin TopAppBar ni BottomNavigationBar de Material en la HomeScreen. La navegación a Settings es desde dentro del scroll (si se requiere, un ícono de engranaje pequeño al final de la pantalla o en el header).
- Sin pantalla de bienvenida, splash screen animada ni selección de sistema al abrir. La app arranca directamente en el sistema activo.
- Sin contador de rachas, porcentaje de cumplimiento, ni ninguna estadística acumulada.
- Sin guardado automático del campo de nota. Todo guardado es intencional mediante el botón.
- Sin scroll horizontal en la fila de días. Los 7 días deben caber en pantalla ajustando el tamaño de los círculos.
- Sin gradientes, sombras dramáticas ni efectos de blur. Solo box shadow sutil (elevation 2-4dp).
- Sin modo oscuro implementado en esta versión. La app usa siempre el tema claro.


# **9. Assets e Íconos**

- Fuente: Nunito (400, 500, 600). Descargar desde Google Fonts e incluir en res/font/.
- Íconos del sistema: usar íconos de Material Symbols (Rounded style) para consistencia. Tamaño siempre 18dp dentro del círculo del header.
- Ícono de la app (launcher): definir en una iteración separada. Pendiente de aprobación del diseño de ícono.
- Check mark en días cumplidos: ícono de check de Material (stroke weight 2.5dp aprox), 10dp, color verde oscuro.
- X en días no cumplidos: ícono de close de Material, 10dp, color rojo.
- Escudo en card Peor Día: ícono shield de Material, 16dp, color ámbar.

*— Fin del documento —*
