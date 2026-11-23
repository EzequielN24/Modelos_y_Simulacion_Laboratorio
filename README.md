# Laboratorio - Modelos y Simulación

Entrega del laboratorio de la materia Modelos y Simulación (simulador de eventos discretos para gestión aeroportuaria).

## Integrantes del grupo

- Ezequiel Nodar
- Ezequiel Bernaldez

---

## Estructura de la entrega

La entrega está organizada en **3 proyectos Maven (Java 21)**:

1. **`random-variate-gen-lab/`** (`gida:random-variate-gen:1.0-SNAPSHOT`): Librería generadora de variables aleatorias (distribuciones `Uniform`, `Exponential`, `Normal` y `Discrete`).
2. **`queueing-sim-lab/`** (`gida:queueing-sim:1.0-SNAPSHOT`): Librería del motor de simulación de sistemas de colas orientado a eventos discretos (eventos, FEL, colas, servidores, estrategias y sondas).
3. **`main-app/`** (`gida:main-app:1.0-SNAPSHOT`): Aplicación principal que consume ambas librerías (`airport.sim.Main`), incluyendo el generador de arribos con horarios pico (`ArribosVuelosHorarioPicoGenerator`) y la probe de desgaste de pistas (`RunwayDurabilityProbe`).

---

## Prerrequisitos

- **Java JDK 21** o superior instalado y disponible en el `PATH` (`java -version`).

---

## Instalación y compilación

Dado que `queueing-sim-lab` depende de `random-variate-gen-lab`, y `main-app` depende de ambas librerías, es necesario compilar e instalar las librerías en orden en el repositorio local de Maven (`~/.m2`):

### En Windows (PowerShell / CMD)

```powershell
# 1. Compilar e instalar la librería de distribuciones
cd random-variate-gen-lab
.\mvnw.cmd clean install
cd ..

# 2. Compilar e instalar la librería del simulador
cd queueing-sim-lab
.\mvnw.cmd clean install
cd ..

# 3. Compilar la aplicación principal
cd main-app
.\mvnw.cmd clean package
cd ..
```

### En Linux / macOS

```bash
# 1. Compilar e instalar la librería de distribuciones
cd random-variate-gen-lab
./mvnw clean install
cd ..

# 2. Compilar e instalar la librería del simulador
cd queueing-sim-lab
./mvnw clean install
cd ..

# 3. Compilar la aplicación principal
cd main-app
./mvnw clean package
cd ..
```

---

## Ejecución de la aplicación

Una vez compilados los proyectos, entrar a `main-app/` y ejecutar con Maven Wrapper (`exec-maven-plugin`):

### Modo interactivo (menú por consola)

```powershell
# En Windows (PowerShell / CMD)
cd main-app
.\mvnw.cmd exec:java

# En Linux / macOS
cd main-app
./mvnw exec:java
```

El menú ofrece las siguientes opciones:
1. **Ejecución de cabotaje** (1 pista, 1 cola, horizonte de 4 semanas = 40.320 minutos).
2. **Probar con 2, 3, 4 y 5 pistas** (arribos con horarios pico, desgaste de suelo y evaluación de restricción de espera máxima de 2 horas).
3. **50 réplicas** (configuración de 4 pistas con estimación puntual e intervalos de confianza del 95%).
0. **Salir**.

### Modo directo por argumento

También se puede ejecutar cualquier escenario directamente pasando un parámetro:

- `cabotaje` o `1`: ejecuta el escenario de vuelos de cabotaje.
- `pistas` o `2`: ejecuta las configuraciones de 2, 3, 4 y 5 pistas.
- `replicas` o `3`: ejecuta las 50 réplicas con intervalos de confianza del 95%.
- `all` o `todo`: ejecuta los tres escenarios en secuencia.
- `<N>` (número entero, ej. `4`): ejecuta el escenario multiservidor con `N` pistas.

Ejemplo:
```powershell
# En Windows (PowerShell / CMD)
.\mvnw.cmd exec:java "-Dexec.args=all"

# En Linux / macOS
./mvnw exec:java -Dexec.args="all"
```

---
