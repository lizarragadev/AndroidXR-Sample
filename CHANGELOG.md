# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-10-13

### Agregado
- Aplicación Android XR completa para DevFest 2025 Sureste de México
- Interfaz Material 3 con diseño adaptativo
- Navegación entre lista de charlas y detalles
- Soporte para tema claro/oscuro
- Información de ubicación y eventos virtuales
- Datos del evento en formato JSON
- Soporte para Android XR con fallback a modo 2D
- Layout de dos paneles para pantallas grandes
- Selector de días con indicadores visuales (Presencial/Virtual)
- Información detallada de speakers y charlas
- Recursos y biografías de speakers
- Diálogos informativos con mapas y enlaces de YouTube
- Iconos personalizados (sol/luna, ubicación, live stream)

### Características Técnicas
- Jetpack Compose para UI moderna
- Navigation Compose para navegación
- StateFlow para gestión de estado reactiva
- Gson para parsing de JSON
- Coil para carga de imágenes
- Arquitectura MVVM con ViewModel
- Repository pattern para acceso a datos
- Componentes reutilizables y modulares

### Limpieza y Optimización
- Código completamente limpio sin elementos no utilizados
- Imports optimizados
- Funciones no utilizadas eliminadas
- Archivos de recursos innecesarios removidos
- Estructura de proyecto optimizada
- Compilación exitosa verificada

### Datos del Evento
- Día 1 (Presencial): 15 de Noviembre, 2024
- Días 2-3 (Virtual): 16-17 de Noviembre, 2024
- 15+ charlas programadas
- 10+ speakers confirmados
- Temas: AI/ML, Mobile Development, Web Development, DevOps

## [Próximas Versiones]

### [1.1.0] - Planeado
- [ ] Avatar 3D del DevFest
- [ ] Orbiter con información del speaker
- [ ] Efectos de partículas
- [ ] Transiciones suaves entre pantallas

### [1.2.0] - Planeado
- [ ] Entornos 3D temáticos por día
- [ ] Visualizaciones de datos flotantes
- [ ] Sistema de badges 3D
- [ ] Audio espacial básico

### [1.3.0] - Planeado
- [ ] Tour virtual del campus
- [ ] Simulación de salas de conferencias
- [ ] Gamificación completa
- [ ] Integración con dispositivos externos

---

## Notas de Desarrollo

### Estructura de Ramas
- `main`: Código estable y funcional
- `feature/xr-enhancements`: Desarrollo de características XR avanzadas

### Convenciones de Commit
- `feat:` Nueva característica
- `fix:` Corrección de bug
- `docs:` Cambios en documentación
- `style:` Cambios de formato
- `refactor:` Refactorización de código
- `test:` Agregar o modificar tests
- `chore:` Cambios en herramientas o configuración
