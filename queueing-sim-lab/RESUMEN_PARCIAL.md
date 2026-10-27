# Resumen Detallado para el Parcial: Simulador de Colas (Queueing Sim)

Este documento detalla la arquitectura del simulador de eventos discretos, haciendo especial énfasis en los eventos clave **`Arrival`** y **`Departure`**, sus algoritmos, prioridades y efectos en el sistema.

---

## 1. Foco Principal: Eventos `Arrival` y `Departure`

Ambas clases implementan la interfaz `Event` y definen cómo evoluciona el estado del sistema en el tiempo.

### A. `Arrival` (Llegada de una Entidad)

Representa el instante en que un cliente o entidad entra al sistema.

* **Atributos:**
  * `clock`: Tiempo de simulación en el que ocurre este arribo.
  * `entity`: La entidad que arriba al sistema.
  * `interarrivalTimeGenerator`: Generador de tiempo hasta la próxima llegada.
  * `entityGenerator`: Generador de la próxima entidad a arribar.

* **Prioridad (`priority()`):**
  * Retorna **`1`**.
  * **Punto clave de parcial:** Las salidas tienen prioridad `0`. Por lo tanto, si una llegada y una salida coinciden exactamente en el mismo instante de reloj (`clock`), **la salida se procesa primero**. Esto permite liberar el servidor antes de evaluar si la llegada encuentra servidores libres.

* **Algoritmo paso a paso (`execute(SimulationContext context)`):**
  1. **Marca de tiempo:** Se registra el tiempo de llegada en la entidad:
     ```java
     this.entity.arrivalTime(this.clock);
     ```
  2. **Notificar llegada:** Se notifica a los observadores (`probes`):
     ```java
     probe.entityArrived(this.clock, this.entity);
     ```
  3. **Evaluar servidores disponibles:** Se consultan los servidores libres:
     ```java
     List<ServerView> serverDisponibles = internalContext.model().availableServers();
     ```
  4. **Bifurcación (Servidor Libre vs. Encolar):**
     * **Caso 1: No hay servidores libres (`serverDisponibles.isEmpty()`):**
       * Se consulta la estrategia de llegada para elegir una cola:
         ```java
         int cola_seleccionada = internalContext.strategyBag().arrivalQueueSelection().select(colas);
         ```
       * Se inserta la entidad en la cola elegida:
         ```java
         internalContext.model().queue(cola_seleccionada).add(this.entity);
         ```
       * Se notifica a las sondas:
         * `probe.entityEnteredQueue(...)`
         * `probe.queueSizeChanged(...)`
     * **Caso 2: Hay al menos un servidor libre:**
       * Se consulta la estrategia de servidores para elegir cuál atiende:
         ```java
         int server_seleccionado = internalContext.strategyBag().serverSelection().select(serverDisponibles);
         ```
       * El servidor seleccionado toma la entidad:
         ```java
         Server server = internalContext.model().server(server_seleccionado);
         server.seize(this.entity);
         ```
       * Se calcula la duración de la atención:
         ```java
         double duracion_servicio = server.serviceDuration(internalContext.randomGenerator());
         ```
       * **Se programa el evento `Departure`:**
         ```java
         context.schedule(new Departure(this.clock + duracion_servicio, this.entity, server));
         ```
       * Se notifica a las sondas:
         * `probe.serverBecameBusy(...)`
         * `probe.serviceStarted(...)`
  5. **Auto-encadenamiento (Próxima llegada):**
     * Sin importar si fue atendido o encolado, **siempre** se genera y agenda la siguiente llegada:
       ```java
       Entity proxima_entidad = this.entityGenerator.generate();
       double tiempo_proxima_llegada = this.interarrivalTimeGenerator.generate(internalContext.randomGenerator());
       context.schedule(new Arrival(this.clock + tiempo_proxima_llegada, proxima_entidad, this.interarrivalTimeGenerator, this.entityGenerator));
       ```

---

### B. `Departure` (Fin de Atención / Salida)

Representa el instante en que un servidor finaliza la atención de una entidad.

* **Atributos:**
  * `clock`: Tiempo de simulación en que culmina la atención.
  * `entity`: La entidad que completó su servicio.
  * `server`: El servidor que la atendió y se desocupa.

* **Prioridad (`priority()`):**
  * Retorna **`0`** (mayor precedencia que `Arrival` en caso de empate en `clock`).

* **Algoritmo paso a paso (`execute(SimulationContext context)`):**
  1. **Liberación del servidor:**
     ```java
     this.server.release();
     ```
  2. **Notificar salida:**
     ```java
     probe.entityDeparted(this.clock, this.entity);
     ```
  3. **Inspección de colas con espera:**
     * Se buscan todas las colas que tengan entidades esperando (`queue.size() > 0`).
  4. **Bifurcación (Colas vacías vs. Tomar siguiente entidad):**
     * **Caso 1: Ninguna cola tiene entidades en espera (`queues_con_esperas.isEmpty()`):**
       * El servidor permanece ocioso.
       * Se notifica a las sondas:
         ```java
         probe.serverBecameIdle(this.clock, this.server);
         ```
     * **Caso 2: Hay entidades esperando:**
       * Se consulta la estrategia de selección de colas de salida para decidir de qué cola extraer:
         ```java
         int queue_id = internalContext.strategyBag().departureQueueSelection().select(queues_con_esperas);
         Queue queue_seleccionada = internalContext.model().queue(queue_id);
         ```
       * Se extrae la entidad de la cola:
         ```java
         Entity entidad = queue_seleccionada.remove();
         ```
       * **El mismo servidor** que se liberó toma inmediatamente a la entidad:
         ```java
         this.server.seize(entidad);
         ```
       * Se notifica a las sondas:
         * `probe.queueSizeChanged(...)`
         * `probe.serviceStarted(...)`
       * Se calcula la duración de atención y **se agenda el siguiente `Departure`**:
         ```java
         double tiempo_duracion = this.server.serviceDuration(internalContext.randomGenerator());
         context.schedule(new Departure(this.clock + tiempo_duracion, entidad, this.server));
         ```

---

## 2. Motor de Simulación (Discrete Event Simulation)

* **`Event` (Interfaz):**
  * `double clock()`: Instante temporal en el que ocurre.
  * `int priority()`: Criterio de desempate temporal (menor número = antes).
  * `void execute(SimulationContext context)`: Acción que modifica el estado del sistema.

* **`FutureEventList` (FEL):**
  * Lista cronológica de eventos futuros ordenada mediante:
    1. Menor `clock` primero.
    2. Menor `priority` primero (Departure [0] antes que Arrival [1]).
    3. Orden de inserción (FIFO / estable).
  * Métodos: `insert(Event)`, `imminent()` (extrae el primero), `peek()`, `isEmpty()`.

* **`Simulation`:**
  * Controla el ciclo principal:
    ```
    mientras la FEL no esté vacía y no se alcance condición de parada:
      evento = fel.imminent()
      clock = evento.clock()
      evento.execute(context)
    ```

* **`SimulationContext` / `InternalSimulationContext`:**
  * Fachada que provee a los eventos los servicios necesarios: agendar eventos (`schedule`), generador aleatorio (`randomGenerator`), modelo (`model`), estrategias (`strategyBag`) y sondas (`probes`).

---

## 3. Modelo del Sistema de Colas

* **`Entity`:**
  * Representa el cliente o elemento que fluye por el sistema.
  * Guarda su `id` y `arrivalTime` para el posterior cálculo de tiempos de espera y estancia.

* **`Server` / `ServerView`:**
  * Representa un recurso de servicio.
  * Métodos: `seize(entity)`, `release()`, `isBusy()`, `isIdle()`, `serviceDuration(randomGenerator)`.
  * `ServerView` es una interfaz inmutable (read-only) expuesta a las estrategias de decisión.

* **`Queue` / `QueueView`:**
  * Estructura donde esperan las entidades antes de ser atendidas.
  * Métodos: `add(entity)`, `remove()`, `size()`, `isEmpty()`.
  * `QueueView` provee inspección de solo lectura (`size()`, `id()`).

* **`QueueingModel`:**
  * Contenedor de todas las colas y servidores del sistema.
  * Métodos globales: `availableServers()`, `queues()`, `server(id)`, `queue(id)`.

---

## 4. Estrategias de Decisión (`StrategyBag`)

Contiene las tres políticas configurables del sistema:
1. **`ServerSelectionStrategy`:** Al llegar un `Arrival` con múltiples servidores libres, elige cuál atiende.
2. **`QueueSelectionStrategy (arrival)`:** Al llegar un `Arrival` y no haber servidores libres, elige en qué cola espera la entidad.
3. **`QueueSelectionStrategy (departure)`:** Al liberarse un servidor en `Departure` con clientes en espera en múltiples colas, elige de qué cola se extrae el siguiente.

---

## 5. Recolección de Métricas (`Probe`)

Implementa el patrón **Observer**. Los eventos notifican cambios de estado para calcular indicadores:
* **Llegadas:** `entityArrived`, `entityEnteredQueue`, `queueSizeChanged`, `serverBecameBusy`, `serviceStarted`.
* **Salidas:** `entityDeparted`, `serverBecameIdle`, `queueSizeChanged`, `serviceStarted`.
* Métricas habituales obtenidas:
  * $W$: Tiempo promedio en el sistema.
  * $W_q$: Tiempo promedio de espera en cola.
  * $L, L_q$: Cantidad promedio de entidades en sistema y en cola.
  * $\rho$: Factor de utilización del servidor.
