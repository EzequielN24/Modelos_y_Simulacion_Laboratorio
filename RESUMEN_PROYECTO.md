# Resumen del Proyecto: `queueing-sim-lab`

> **Materia / Contexto:** Modelos y Simulación — Laboratorio  
> **Tecnologías:** Java 21, Maven (Maven Wrapper), JUnit 5, Spotless  
> **Patrón Arquitectónico:** Simulación de Eventos Discretos (Discrete-Event Simulation - DES), Observer, Strategy, Context/Facade  
> **Estado Actual:** **100% de Pruebas Pasadas (87/87 en verde)** :white_check_mark:

---

## 1. Visión General del Proyecto

`queueing-sim-lab` es un simulador de **sistemas de colas y líneas de espera orientado a eventos discretos**, desarrollado con fines educativos para la cátedra de *Modelos y Simulación*.

El proyecto está diseñado bajo un enfoque de **Desarrollo Guiado por Pruebas (TDD)**:
- Contiene la arquitectura completa, interfaces, modelos de configuración y una suite exhaustiva de tests unitarios y de integración.
- Los componentes de lógica central (el motor de simulación, la gestión de la cola y servidores, los eventos `Arrival` y `Departure`, la lista de eventos futuros y los cálculos estadísticos en las sondas/probes) fueron implementados y validados paso a paso.
- Se incluye además una clase ejecutable interactiva [`Main.java`](file:///d:/Uni/5to%20a%C3%B1o/Modelos%20y%20Simulacion/Laboratorio/queueing-sim-lab/src/main/java/queueing/sim/Main.java) que permite visualizar en tiempo real cómo avanza el reloj, el estado de las colas, servidores y la Lista de Eventos Futuros (FEL).

---

## 2. Diagrama Conceptual del Flujo de Simulación

```
                           +---------------------------+
                           |     FutureEventList       |
                           |  (Ordenada por clock/pri) |
                           +-------------+-------------+
                                         |
                                  imminent() / peek()
                                         v
                               +-------------------+
                               |    Simulation     | <---> SimulationContext
                               +---------+---------+
                                         |
                       +-----------------+-----------------+
                       |                                   |
                       v                                   v
             [ Evento: Arrival ]                 [ Evento: Departure ]
                       |                                   |
           ¿Hay servidores libres?                Libera el servidor actual.
             /               \                    ¿Hay clientes esperando?
           SI                 NO                         /            \
          /                     \                       SI             NO
    Seize Server            Encolar Entity         Extraer Entity   Servidor
  (ServerSelection)      (ArrivalQueueSelection)  (DepartureQueue)  queda IDLE
          |                                             |
   Agenda Departure                              Agenda Departure
          \                                             /
           +--------------------+----------------------+
                                |
                   Siempre agenda próxima Arrival
                                |
                                v
               Notificación a Probes (Métricas)
```

---

## 3. Arquitectura y Componentes del Sistema

El código se divide en tres paquetes principales dentro de `src/main/java/queueing/sim`:
1. `queueing.sim`: Núcleo de la simulación, colas, servidores, eventos y contexto.
2. `queueing.sim.strategies`: Estrategias de decisión para asignación de colas y servidores.
3. `queueing.sim.probes`: Sondas/Observadores para recolectar métricas del sistema.

---

### 3.1. Núcleo de Simulación (`queueing.sim`)

| Clase / Interfaz | Responsabilidad / Descripción | Estado |
|---|---|---|
| `Simulation` | Motor principal. Ejecuta el bucle de eventos (`while`), avanza el reloj, coordina la FEL, notifica inicio/fin a las sondas e incluye soporte de depuración paso a paso (`stepByStep`). | **Completamente implementado** |
| `SimulationContext` | Interfaz pública provista a los eventos durante `execute()`. Permite acceder a `randomGenerator()`, `model()` y agendar eventos `schedule()`. | Interfaz completa |
| `InternalSimulationContext` | Implementación del contexto. Permite acceder a la bolsa de estrategias (`strategyBag`) y a la lista de sondas (`probes`). | **Completamente implementado** |
| `FutureEventList` (FEL) | Lista de eventos futuros ordenada cronológicamente por `clock` ascendente; en caso de empate, por menor `priority`, y luego por orden de inserción (FIFO). | **Completamente implementado** |
| `Event` | Interfaz base de eventos con `clock()`, `priority()` y `execute(SimulationContext)`. | Interfaz completa |
| `Arrival` | Evento de llegada de una entidad: si hay servidores disponibles, toma uno y agenda salida; si no, va a la cola según estrategia. Siempre agenda la próxima llegada. | **Completamente implementado** |
| `Departure` | Evento de fin de servicio: libera el servidor; si hay entidades en espera en alguna cola, toma una según estrategia y agenda la próxima salida; si no, el servidor queda libre. | **Completamente implementado** |
| `Entity` / `EntityView` | Entidad o cliente que transita por el sistema. Posee un identificador único `id` y su instante de arribo `arrivalTime`. | **Completamente implementado** |
| `EntityGenerator` | Interfaz funcional `@FunctionalInterface` proveedora de nuevas instancias de `Entity`. | Completo |
| `Server` / `ServerView` | Servidor con estados IDLE/BUSY. Tiene un generador de tiempo de servicio (`TimeGenerator`) y métodos `seize(entity)` y `release()`. | **Completamente implementado** |
| `Queue` / `QueueView` | Cola FIFO basada en `ArrayDeque<Entity>`. Gestiona las entidades en espera asociadas a un grupo de servidores. | **Completamente implementado** |
| `QueueingModel` | Contenedor principal del modelo. Construye y valida las colas y servidores a partir de configuraciones, asegurando IDs únicos. | **Completamente implementado** |
| `QueueConfig` y `ServerConfig` | Records inmutables que definen la estructura declarativa de colas y servidores antes de instanciar el modelo. | **Completamente implementados** |
| `TimeGenerator` | Interfaz funcional `(RandomGenerator) -> double` para modelar distribuciones temporales (tiempos de servicio y entre arribos). | Interfaz completa |

---

### 3.2. Estrategias (`queueing.sim.strategies`)

Permiten desacoplar las reglas de negocio de la lógica del simulador (Patrón Strategy):

- **`ServerSelectionStrategy`**: Decide qué servidor libre toma una entidad cuando hay más de uno disponible.
  - Implementación incluida: **`FirstAvailable`** (toma el primer servidor libre disponible en la lista).
- **`QueueSelectionStrategy`**: Decide a qué cola ingresar o de qué cola extraer una entidad.
  - *Llegada (`arrivalQueueSelection`)*: Determina a qué cola va un cliente cuando todos los servidores están ocupados (ej. cola más corta, round-robin).
  - *Salida (`departureQueueSelection`)*: Determina de qué cola extraer el siguiente cliente cuando un servidor se libera (ej. prioridades, longitud).
- **`StrategyBag`**: Record que empaqueta las tres estrategias anteriores para suministrarlas a la simulación.

---

### 3.3. Sondas y Métricas (`queueing.sim.probes`)

Implementan el patrón **Observer** a través de la interfaz `Probe`. Todos sus métodos tienen implementaciones por defecto vacías, de modo que cada sonda solo implementa los eventos que le interesan.

| Sonda | Eventos observados | Métrica calculada | Estado actual |
|---|---|---|---|
| `ArrivalCounter` | `entityArrived` | Total de entidades que arribaron | **Completamente implementado** |
| `DepartureCounter` | `entityDeparted` | Total de entidades que completaron servicio | **Completamente implementado** |
| `ServedEntitiesCounter` | `serviceStarted` | Total de entidades que iniciaron servicio | **Completamente implementado** |
| `WaitingTime` | `entityEnteredQueue`, `serviceStarted` | Suma acumulada de tiempo de espera en cola | **Completamente implementado** |
| `MaxWaitingTime` | `entityEnteredQueue`, `serviceStarted` | Máximo tiempo de espera en cola | **Completamente implementado** |
| `SystemTime` | `entityArrived`, `entityDeparted` | Tiempo total transcurrido en el sistema (cola + servicio) | **Completamente implementado** |
| `MaxSystemTime` | `entityArrived`, `entityDeparted` | Máximo tiempo que una entidad pasó en el sistema | **Completamente implementado** |
| `IdleTime` | `serverBecameIdle`, `serverBecameBusy`, `simulationFinished` | Tiempo acumulado de ocio/inactividad por servidor | **Completamente implementado** |
| `MaxIdleTime` | `serverBecameIdle`, `serverBecameBusy`, `simulationFinished` | Máximo período continuo de inactividad por servidor | **Completamente implementado** |
| `MaxQueueSize` | `queueSizeChanged` | Tamaño máximo alcanzado por cada cola | **Completamente implementado** |

---

## 4. Estructura de Directorios

```text
queueing-sim-lab/
├── pom.xml                               # Configuración de Maven, Java 21, JUnit 5, Spotless
├── mvnw / mvnw.cmd                       # Maven Wrapper para Linux/macOS y Windows
├── README.md                             # Documentación de inicio del repositorio
├── RESUMEN_PROYECTO.md                   # Resumen arquitectónico y técnico
├── src/
│   ├── main/java/queueing/sim/
│   │   ├── Arrival.java                  # Evento de llegada
│   │   ├── Departure.java                # Evento de salida
│   │   ├── Entity.java                   # Entidad cliente/tarea
│   │   ├── EntityGenerator.java          # Generador de entidades
│   │   ├── EntityView.java               # Vista de solo lectura de entidad
│   │   ├── Event.java                    # Contrato de evento de simulación
│   │   ├── FutureEventList.java          # Lista de eventos futuros (FEL)
│   │   ├── InternalSimulationContext.java# Contexto interno para eventos
│   │   ├── Main.java                     # Clase ejecutable interactiva / CLI
│   │   ├── Queue.java                    # Cola FIFO
│   │   ├── QueueConfig.java              # Configuración inmutable de cola
│   │   ├── QueueingModel.java            # Modelo que agrupa colas y servidores
│   │   ├── QueueView.java                # Vista de solo lectura de cola
│   │   ├── Server.java                   # Servidor con tiempos de atención
│   │   ├── ServerConfig.java             # Configuración inmutable de servidor
│   │   ├── ServerView.java               # Vista de solo lectura de servidor
│   │   ├── Simulation.java               # Motor del bucle de simulación
│   │   ├── SimulationContext.java        # Contexto expuesto a eventos
│   │   ├── TimeGenerator.java            # Generador estocástico o determinista de tiempos
│   │   ├── probes/                       # Métricas y observadores (todas implementadas)
│   │   │   ├── ArrivalCounter.java
│   │   │   ├── DepartureCounter.java
│   │   │   ├── IdleTime.java
│   │   │   ├── MaxIdleTime.java
│   │   │   ├── MaxQueueSize.java
│   │   │   ├── MaxSystemTime.java
│   │   │   ├── MaxWaitingTime.java
│   │   │   ├── Probe.java
│   │   │   ├── ServedEntitiesCounter.java
│   │   │   ├── SystemTime.java
│   │   │   └── WaitingTime.java
│   │   └── strategies/                   # Estrategias de decisión
│   │       ├── FirstAvailable.java
│   │       ├── QueueSelectionStrategy.java
│   │       ├── ServerSelectionStrategy.java
│   │       └── StrategyBag.java
│   └── test/java/queueing/sim/           # Suite de Pruebas Unitarias e Integración (87 tests)
│       ├── ArrivalTest.java
│       ├── DepartureTest.java
│       ├── FutureEventListTest.java
│       ├── QueueingModelTest.java
│       ├── QueueSelectionStrategyTest.java
│       ├── QueueTest.java
│       ├── ServerTest.java
│       ├── SimulationIntegrationTest.java# Test de integración D/D/1 completo (3 escenarios)
│       ├── SimulationTest.java
│       ├── probes/                       # Tests unitarios para cada probe
│       └── testing/                      # Fixtures y mocks de prueba (OneQueueOneServer, etc.)
```

---

## 5. El Test de Integración: Modelo D/D/1 (`SimulationIntegrationTest`)

El archivo `SimulationIntegrationTest.java` valida exhaustivamente el motor y las sondas en tres escenarios deterministas con 1 cola y 1 servidor con tiempo entre arribos de 10 unidades (`D = 10`) a lo largo de 100 unidades de tiempo:

1. **Servicio más rápido que arribos (`service = 9`, `arrival = 10`):**
   - No hay colas (espera = 0).
   - El servidor descansa 1 unidad de tiempo entre cliente y cliente (tiempo ocioso = 10, max ocio = 1).
   - 11 arribos, 11 atendidos, 10 partidas completas antes de `t=100`.
2. **Servicio igual a arribos (`service = 10`, `arrival = 10`):**
   - El servidor nunca está ocioso (`idleTime = 0`), ni se generan colas (`waitingTime = 0`).
3. **Servicio más lento que arribos (`service = 11`, `arrival = 10`):**
   - Se forma cola creciente.
   - Tiempo acumulado de espera en cola = 45.0, espera máxima = 9.0.
   - 0 tiempo ocioso en el servidor.

---

## 6. Comandos de Utilidad

```powershell
# Ejecutar la simulación interactiva por consola
.\mvnw compile exec:java '-Dexec.mainClass=queueing.sim.Main'

# Ejecutar todas las pruebas unitarias e integración (87 tests)
.\mvnw test

# Ejecutar solo los tests de integración
.\mvnw test -Dtest=SimulationIntegrationTest

# Verificar y aplicar formato de código Google Java Style
.\mvnw spotless:check
.\mvnw spotless:apply
```

---

## 7. Relación con `random-variate-gen-lab`

El repositorio compañero [random-variate-gen-lab](https://github.com/dglabella/random-variate-gen-lab) implementa **generadores de variables aleatorias / distribuciones estadísticas**:
- `random.variates.Distribution<T>` (interfaz funcional)
- `Exponential`: Para tiempos entre arribos (procesos de Poisson) y tiempos de servicio exponencial ($\sim Exp(\lambda)$ / M/M/c).
- `Uniform`: Distribuciones continuas en rangos $[a, b)$.
- `Normal`: Distribuciones normales $\sim N(\mu, \sigma)$ (Box-Muller / polar).
- `Discrete`: Distribuciones empíricas / discretas ponderadas.

### ¿Cómo se conectan ambos proyectos?
En este simulador (`queueing-sim-lab`), las duraciones temporales están desacopladas mediante la interfaz funcional:
```java
@FunctionalInterface
public interface TimeGenerator {
  double generate(RandomGenerator randomGenerator);
}
```
En las pruebas actuales de integración (`SimulationIntegrationTest`) se utilizan generadores deterministas (por ejemplo `rg -> 10.0` para arribos y `rg -> 9.0` para servicio, constituyendo un sistema $D/D/1$). 

Al integrar `random-variate-gen-lab`, las instancias de `Distribution<Double>` implementadas allí se suministran como `TimeGenerator` a:
1. `Simulation`: para modelar el tiempo entre arribos (`interarrivalTimeGenerator`).
2. `ServerConfig`: para modelar los tiempos de servicio de cada servidor (`serviceTimeGenerator`).

Esto permite transformar el simulador en un sistema estocástico completo como **$M/M/1$**, **$M/M/k$**, **$M/G/1$**, etc.
