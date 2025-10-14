# DevFest 2025 Sureste de México - Android XR App

Una aplicación Android XR innovadora para el evento DevFest 2025 Sureste de México, que combina la funcionalidad tradicional de una app móvil con características inmersivas de realidad extendida.

## 🚀 Características

### 📱 Funcionalidad Principal
- **Navegación por días del evento** (Día 1: Presencial, Días 2-3: Virtual)
- **Lista de charlas** con información detallada de speakers
- **Vista de detalles** con biografías y recursos
- **Layout adaptativo** (pantalla única vs. dos paneles)
- **Tema claro/oscuro** con colores personalizados del DevFest
- **Información de ubicación** y eventos virtuales

### 🥽 Características XR
- **Soporte para Android XR** con fallback a modo 2D
- **Spatial UI** para experiencias inmersivas
- **Interfaz espacial** que se adapta al entorno XR
- **Navegación por gestos** y comandos de voz (próximamente)

## 🛠️ Tecnologías

- **Android XR** - Realidad extendida
- **Jetpack Compose** - UI moderna y declarativa
- **Material 3** - Sistema de diseño actualizado
- **Navigation Compose** - Navegación entre pantallas
- **StateFlow** - Gestión de estado reactiva
- **Gson** - Parsing de JSON
- **Coil** - Carga de imágenes

## 📁 Estructura del Proyecto

```
app/src/main/java/tech/lizza/demoxr/
├── data/                    # Modelos de datos
│   ├── Speaker.kt          # Modelo de speaker
│   └── Talk.kt             # Modelo de charla
├── navigation/              # Navegación
│   └── EventNavigation.kt  # Graph de navegación
├── repository/              # Acceso a datos
│   └── EventRepository.kt  # Repositorio de datos
├── ui/                      # Interfaz de usuario
│   ├── components/         # Componentes reutilizables
│   ├── screens/            # Pantallas principales
│   └── theme/              # Temas y colores
├── viewmodel/              # Lógica de negocio
│   └── EventViewModel.kt   # ViewModel principal
└── MainActivity.kt         # Actividad principal
```

## 🎨 Diseño

### Colores del Tema
- **Azul DevFest**: `#1976D2` (primario)
- **Naranja DevFest**: `#FF5722` (secundario)
- **Rosa DevFest**: `#E91E63` (terciario)
- **Fondo claro**: `#F8F8F8`

### Modos de Visualización
- **Día 1 (Presencial)**: Verde `#2E7D32`
- **Días 2-3 (Virtual)**: Azul `#1976D2`

## 📊 Datos del Evento

### Día 1 - Presencial (15 Nov 2024)
- **Ubicación**: Universidad Tecnológica de Tabasco
- **Horario**: 12:00 PM - 4:00 PM
- **Charla destacada**: "Architecture and Implementation of AI Agents with Google's ADK and Python"

### Días 2-3 - Virtual (16-17 Nov 2024)
- **Plataforma**: Google Meet
- **Transmisión**: YouTube Live
- **Horarios**: 11:00 AM - 3:00 PM / 4:00 PM

## 🚀 Instalación y Uso

### Requisitos
- Android Studio Hedgehog o superior
- Android SDK 34+
- Dispositivo Android XR o emulador

### Compilación
```bash
./gradlew assembleDebug
```

### Instalación
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 🔮 Características XR Futuras

### Fase 1: Elementos Básicos
- [ ] Avatar 3D del DevFest
- [ ] Orbiter con información del speaker
- [ ] Efectos de partículas
- [ ] Transiciones suaves

### Fase 2: Interactividad Avanzada
- [ ] Entornos 3D temáticos
- [ ] Visualizaciones de datos flotantes
- [ ] Sistema de badges 3D
- [ ] Audio espacial

### Fase 3: Experiencias Inmersivas
- [ ] Tour virtual del campus
- [ ] Simulación de salas de conferencias
- [ ] Gamificación completa
- [ ] Integración con dispositivos externos

## 🤝 Contribución

Este proyecto está en desarrollo activo. Las contribuciones son bienvenidas:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👥 Equipo

- **Desarrollador Principal**: [Tu Nombre]
- **Diseño**: Equipo DevFest 2025 Sureste de México
- **Contenido**: GDG Villahermosa

## 📞 Contacto

- **Evento**: DevFest 2025 Sureste de México
- **Organizador**: GDG Villahermosa
- **Ubicación**: Villahermosa, Tabasco, México

---

*Desarrollado con ❤️ para la comunidad de desarrolladores del sureste de México*
