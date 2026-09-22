# AndinaSalud · Examen parcial U1

Aplicación de citas médicas hecha con Kotlin Multiplatform y Compose Multiplatform. El mismo código de `composeApp/src/commonMain` contiene dominio, datos simulados, ViewModels y pantallas para Android e iOS. No hay llamadas de red ni base de datos.

## Abrir en Android Studio

1. Clona `https://github.com/Alonso-Tripul/andinasalud-u1.git` o usa **File > New > Project from Version Control**.
2. Selecciona la rama `examen-ancajima` en Android Studio. `main` conserva solamente el proyecto KMP inicial.
3. Abre la carpeta raíz, espera a que termine **Gradle Sync** e instala **Android SDK Platform API 37** si Android Studio lo solicita. `compileSdk` es 37 por los requisitos de Compose; `targetSdk` sigue en 35 y `minSdk` en 24.
4. Elige la configuración `androidApp` y ejecuta en un emulador o dispositivo Android.

Para iOS hace falta una Mac con Xcode. Abre `iosApp/iosApp.xcodeproj`, selecciona un simulador y ejecuta `iosApp`. El script de Xcode compila el framework de `composeApp` antes de construir la aplicación.

## Estructura

```text
androidApp/             Punto de entrada Android
iosApp/                 Punto de entrada SwiftUI/Xcode
composeApp/src/commonMain/kotlin/pe/upeu/andinasalud/
  domain/model/         Paciente, sede, especialidad, médico, cita y estado sealed
  domain/repository/    Contrato CitaRepository y reloj
  domain/usecase/       Lectura, solicitud y cancelación; reglas RN-01 a RN-05
  data/local/           Datos semilla y reloj del sistema
  data/repository/      Repositorio falso en memoria
  presentation/         ViewModels con StateFlow, UI y navegación
  di/                   Módulo Koin en commonMain
```

La interfaz solo depende de casos de uso y modelos del dominio. Para conectar una API más adelante se reemplaza `CitaRepositoryFake` por otra implementación de `CitaRepository` y se cambia el enlace en `AppModule.kt`. Las pantallas y casos de uso quedan iguales.

## Comportamiento implementado

- Inicio con saludo, próxima cita y accesos rápidos.
- Lista ordenada con filtros por estado y búsqueda por especialidad o médico, sin mayúsculas ni tildes.
- Detalle y cancelación con confirmación.
- Solicitud con errores debajo de cada campo y confirmación posterior.
- Perfil con datos del paciente y cambio de tema inmediato.
- Barra inferior Inicio/Citas/Perfil y retorno Android con el botón del sistema.
- Estados de carga de 800 ms, vacío y error con reintento. En **Mis citas** hay una opción para demostrar el error simulado.

La semilla contiene cuatro sedes, cinco especialidades, dos médicos por especialidad y seis citas. Las tres citas programadas se generan en fechas futuras relativas al día en que se inicia la app; dos están atendidas y una cancelada.

## Reglas de negocio

| Regla | Ubicación |
|---|---|
| RN-01 fecha y hora futuras | `SolicitarCitaUseCase` |
| RN-02 máximo tres programadas | `SolicitarCitaUseCase` |
| RN-03 cancelación solo programada y con más de 24 h | `CancelarCitaUseCase` |
| RN-04 motivo de 10 a 200 caracteres | `SolicitarCitaUseCase` |
| RN-05 sin dos programadas en el mismo día y hora | `SolicitarCitaUseCase` |

Para demostrar una solicitud exitosa con los datos iniciales, primero cancela una de las tres citas programadas que cumpla RN-03 y luego solicita una nueva. La fuente es solo memoria: los cambios se reinician al cerrar el proceso.

## Evidencias y evaluación

El PDF del examen pide capturas reales de seis pantallas en Android y en iOS, además de evidencias del historial Git. Esas capturas deben hacerse al ejecutar la app en los dispositivos o simuladores; este repositorio no incluye imágenes fabricadas. La rama `examen-ancajima` conserva el desarrollo separado de `main` para mostrar la diferencia solicitada por el docente.

La modalidad individual indicada por el estudiante difiere del criterio de colaboración de dos integrantes que figura en el enunciado. El repositorio no simula contribuciones ni revisiones de otra persona.

## Pruebas

`composeApp/src/commonTest` contiene pruebas de las cinco reglas de negocio, incluida una creación y cancelación correcta. Se pueden ejecutar desde Android Studio tras sincronizar Gradle. La compilación Android no se pudo completar en el entorno de generación porque el SDK local está protegido contra lectura; la ejecución en dispositivo y en Mac/iOS queda pendiente de verificación real.
