# Guía Completa para el Parcial: Pseudocódigo y Métricas (Queueing Sim)

Este documento contiene el **pseudocódigo ultra detallado** de `Simulation`, `Arrival` y `Departure`, junto con la explicación exacta de **cómo se calculan y obtienen todas las métricas** (estadísticas de la simulación).

---

## 1. Pseudocódigo del Ciclo Principal: `Simulation`

El motor de simulación maneja el avance del tiempo discreto mediante la **Future Event List (FEL)**.

```text
ALGORITMO Simulation.run()
ENTRADAS:
    initClock                 // Tiempo inicial (ej. 0.0)
    simLength                 // Duración máxima de simulación
    model                     // Colas y servidores
    FEL                       // Future Event List ordenada por (clock ASC, priority ASC, orden_insercion FIFO)
    strategyBag               // Estrategias de selección
    probes                    // Lista de observadores para métricas
    interarrivalTimeGenerator // Generador estocástico entre arribos
    randomGenerator           // Generador de números aleatorios

INICIO:
    endClock = initClock + simLength
    clockActual = initClock

    // 1. Inicializar generador de entidades consecutivas
    nextEntityId = 1
    DEFINIR FUNCION generarProximaEntidad():
        id = nextEntityId
        nextEntityId = nextEntityId + 1
        RETORNAR nueva Entidad(id)

    // 2. Condición inicial: Si la FEL está vacía, arrancar con la primera llegada en initClock
    SI FEL.isEmpty() ENTONCES:
        primeraEntidad = generarProximaEntidad()
        eventoInicial = nuevo Arrival(clockActual, primeraEntidad, interarrivalTimeGenerator, generarProximaEntidad)
        FEL.insert(eventoInicial)
    FIN SI

    // 3. Notificar a las sondas el arranque de la simulación
    PARA CADA probe EN probes HACER:
        probe.simulationStarted(clockActual)
    FIN PARA

    // 4. Notificar a las sondas que todos los servidores arrancan desocupados (ociosos)
    PARA CADA server EN model.servers() HACER:
        PARA CADA probe EN probes HACER:
            probe.serverBecameIdle(clockActual, server)
        FIN PARA
    FIN PARA

    // 5. Bucle principal de simulación (Time-Advance Mechanism)
    MIENTRAS (!FEL.isEmpty() Y FEL.peek().clock <= endClock) HACER:

        // Extraer el evento inminente (el de menor clock y mayor prioridad)
        eventoInminente = FEL.imminent()

        // Avanzar el reloj de la simulación al instante del evento
        clockActual = eventoInminente.clock

        // Ejecutar la rutina del evento pasándole el contexto
        eventoInminente.execute(context)

    FIN MIENTRAS

    // 6. Fin de la simulación: Notificar a las sondas para consolidar métricas de tiempo
    PARA CADA probe EN probes HACER:
        probe.simulationFinished(endClock)
    FIN PARA

FIN ALGORITMO
```

---

## 2. Pseudocódigo del Evento: `Arrival` (Llegada)

* **Prioridad:** `1` (menor precedencia que Departure si coinciden en el mismo instante de reloj).
* **Propósito:** Atender a la entidad si hay servidor libre, o encolarla. **Siempre** auto-agenda el próximo arribo.

```text
ALGORITMO Arrival.execute(context)
DATOS DEL EVENTO:
    clock                     // Tiempo en el que ocurre esta llegada
    entity                    // Entidad que acaba de llegar
    interarrivalTimeGenerator // Generador de tiempo hasta el próximo arribo
    entityGenerator           // Función para instanciar la siguiente entidad

INICIO:
    // 1. Estampar hora de llegada en la entidad
    entity.arrivalTime = clock

    // 2. Notificar a las sondas el arribo de la entidad
    PARA CADA probe EN context.probes() HACER:
        probe.entityArrived(clock, entity)
    FIN PARA

    // 3. Inspeccionar servidores disponibles en todo el sistema
    servidoresLibres = context.model.availableServers()

    // 4. Bifurcación: Servidores ocupados vs. Servidor libre
    SI servidoresLibres ESTÁ VACÍO ENTONCES:
        // --- CASO 4.A: TODOS OCUPADOS -> ENCOLAR ---
        colas = context.model.queues()
        
        // Consultar estrategia para decidir en qué cola espera
        colaElegidaId = context.strategyBag.arrivalQueueSelection.select(colas)
        cola = context.model.queue(colaElegidaId)

        // Insertar entidad en la cola
        cola.add(entity)

        // Notificar a las sondas: entró a cola y cambió el tamaño de la cola
        PARA CADA probe EN context.probes() HACER:
            probe.entityEnteredQueue(clock, entity, cola)
            probe.queueSizeChanged(clock, cola)
        FIN PARA

    SINO:
        // --- CASO 4.B: HAY SERVIDOR LIBRE -> ATENDER INMEDIATAMENTE ---
        // Consultar estrategia para elegir cuál servidor atiende
        servidorElegidoId = context.strategyBag.serverSelection.select(servidoresLibres)
        servidor = context.model.server(servidorElegidoId)

        // Ocupar el servidor con la entidad
        servidor.seize(entity)

        // Muestrear duración de atención del servidor
        duracionServicio = servidor.serviceDuration(context.randomGenerator)

        // PROGRAMAR EL FIN DE ATENCIÓN (Departure)
        tiempoFinServicio = clock + duracionServicio
        context.schedule(nuevo Departure(tiempoFinServicio, entity, servidor))

        // Notificar a las sondas: servidor pasó a ocupado y comenzó el servicio
        PARA CADA probe EN context.probes() HACER:
            probe.serverBecameBusy(clock, servidor)
            probe.serviceStarted(clock, entity, servidor)
        FIN PARA
    FIN SI

    // 5. Auto-encadenamiento: PROGRAMAR LA PRÓXIMA LLEGADA (Siempre)
    proximaEntidad = entityGenerator.generate()
    tiempoHastaProximaLlegada = interarrivalTimeGenerator.generate(context.randomGenerator)
    tiempoProximaLlegada = clock + tiempoHastaProximaLlegada

    context.schedule(nuevo Arrival(tiempoProximaLlegada, proximaEntidad, interarrivalTimeGenerator, entityGenerator))

FIN ALGORITMO
```

---

## 3. Pseudocódigo del Evento: `Departure` (Salida / Fin de Servicio)

* **Prioridad:** `0` (mayor precedencia que Arrival si coinciden en el mismo reloj: desocupa el servidor antes de evaluar una llegada).
* **Propósito:** Liberar al servidor, despedir a la entidad y, si hay entidades en espera en alguna cola, tomar a la siguiente inmediatamente.

```text
ALGORITMO Departure.execute(context)
DATOS DEL EVENTO:
    clock   // Tiempo en el que finaliza el servicio
    entity  // Entidad que completó su atención y sale del sistema
    server  // Servidor que la estaba atendiendo

INICIO:
    // 1. Liberar el servidor actual
    server.release()

    // 2. Notificar a las sondas que la entidad partió del sistema
    PARA CADA probe EN context.probes() HACER:
        probe.entityDeparted(clock, entity)
    FIN PARA

    // 3. Inspeccionar colas que tengan al menos una entidad esperando
    colasConEspera = LISTA_VACÍA
    PARA CADA q EN context.model.queues() HACER:
        SI q.size() > 0 ENTONCES:
            colasConEspera.agregar(q)
        FIN SI
    FIN PARA

    // 4. Bifurcación: Nadie esperando vs. Extraer siguiente cliente
    SI colasConEspera ESTÁ VACÍA ENTONCES:
        // --- CASO 4.A: NO HAY ESPERAS -> EL SERVIDOR QUEDA OCIOSO ---
        PARA CADA probe EN context.probes() HACER:
            probe.serverBecameIdle(clock, server)
        FIN PARA

    SINO:
        // --- CASO 4.B: HAY ENTIDADES ESPERANDO EN COLA ---
        // Consultar estrategia de salida para elegir de qué cola extraer
        colaElegidaId = context.strategyBag.departureQueueSelection.select(colasConEspera)
        cola = context.model.queue(colaElegidaId)

        // Extraer a la siguiente entidad de esa cola
        siguienteEntidad = cola.remove()

        // El MISMO servidor que se acaba de liberar toma a la nueva entidad
        server.seize(siguienteEntidad)

        // Notificar a las sondas: cambió el tamaño de la cola e inició un servicio
        PARA CADA probe EN context.probes() HACER:
            probe.queueSizeChanged(clock, cola)
            probe.serviceStarted(clock, siguienteEntidad, server)
        FIN PARA

        // Muestrear duración del nuevo servicio y PROGRAMAR NUEVO DEPARTURE
        duracionServicio = server.serviceDuration(context.randomGenerator)
        tiempoFinServicio = clock + duracionServicio

        context.schedule(nuevo Departure(tiempoFinServicio, siguienteEntidad, server))
    FIN SI

FIN ALGORITMO
```

---

## 4. Cómo se Obtienen las "Cosas" (Métricas de Rendimiento)

En una simulación existen dos tipos de métricas:
1. **Métricas basadas en observaciones de entidades** (promedios discretos por cliente).
2. **Métricas ponderadas en el tiempo** (promedios continuos integrados sobre el reloj).

A continuación se detalla cómo cada sonda calcula su valor:

### A. Tiempo Promedio de Espera en Cola (Wq)
* **Variables internas:**
  * `mapaEntradaCola`: Diccionario `(Entidad -> clockEntrada)`.
  * `totalEspera = 0.0`
  * `totalEntidadesAtendidas = 0`
* **Lógica en eventos:**
  * Al dispararse `entityEnteredQueue(clock, entity, queue)`:
    * `mapaEntradaCola.guardar(entity, clock)`
  * Al dispararse `serviceStarted(clock, entity, server)`:
    * `clockEntrada = mapaEntradaCola.eliminar(entity)`
    * Si `clockEntrada != NULL` (la entidad hizo cola):
      * `esperaIndividual = clock - clockEntrada`
      * `totalEspera = totalEspera + esperaIndividual`
    * `totalEntidadesAtendidas = totalEntidadesAtendidas + 1`
* **Fórmula final de la métrica:**
  $$\text{Wq} = \frac{\text{totalEspera}}{\text{totalEntidadesAtendidas}}$$

---

### B. Tiempo Promedio en el Sistema (W)
* **Variables internas:**
  * `mapaLlegada`: Diccionario `(Entidad -> clockLlegada)`.
  * `totalTiempoSistema = 0.0`
  * `totalSalidas = 0`
* **Lógica en eventos:**
  * Al dispararse `entityArrived(clock, entity)`:
    * `mapaLlegada.guardar(entity, clock)`
  * Al dispararse `entityDeparted(clock, entity)`:
    * `clockLlegada = mapaLlegada.eliminar(entity)`
    * Si `clockLlegada == NULL` -> Lanzar excepción `IllegalStateException` (inconsistencia de arribo no registrado).
    * `tiempoSistemaIndividual = clock - clockLlegada`
    * `totalTiempoSistema = totalTiempoSistema + tiempoSistemaIndividual`
    * `totalSalidas = totalSalidas + 1`
* **Fórmula final de la métrica:**
  $$\text{W} = \frac{\text{totalTiempoSistema}}{\text{totalSalidas}}$$

---

### C. Factor de Utilización del Servidor ($\rho$) y Tiempo Ocioso (Idle Time)
* **Variables internas:**
  * `mapaInicioOcio`: Diccionario `(Servidor -> clockInicioOcio)`.
  * `tiempoTotalOcioso = 0.0`
* **Lógica en eventos:**
  * Al dispararse `serverBecameIdle(clock, server)` (al inicio de la simulación o en `Departure` sin cola):
    * `mapaInicioOcio.guardar(server, clock)`
  * Al dispararse `serverBecameBusy(clock, server)` (en `Arrival` o al tomar alguien de cola):
    * `clockInicio = mapaInicioOcio.eliminar(server)`
    * Si `clockInicio != NULL`:
      * `intervaloOcioso = clock - clockInicio`
      * `tiempoTotalOcioso = tiempoTotalOcioso + intervaloOcioso`
  * Al dispararse `simulationFinished(endClock)`:
    * Para los servidores que quedaron ociosos hasta el final:
      * `tiempoTotalOcioso = tiempoTotalOcioso + (endClock - clockInicio)`
* **Fórmulas finales:**
  $$\text{Tiempo Ocupado} = \text{Tiempo Total Simulación} - \text{tiempoTotalOcioso}$$
  $$\rho = \frac{\text{Tiempo Ocupado}}{\text{Tiempo Total Simulación}}$$

---

### D. Longitud Promedio de la Cola (Lq) — Ponderada en el Tiempo
* **Variables internas:**
  * `areaAcumulada = 0.0`
  * `ultimoCambioClock = initClock`
  * `tamañoActual = 0`
* **Lógica en eventos:**
  * Al dispararse `queueSizeChanged(clock, queue)`:
    * `duracionIntervalo = clock - ultimoCambioClock`
    * `areaAcumulada = areaAcumulada + (tamañoActual * duracionIntervalo)`
    * `tamañoActual = queue.size()`
    * `ultimoCambioClock = clock`
  * Al dispararse `simulationFinished(endClock)`:
    * `areaAcumulada = areaAcumulada + (tamañoActual * (endClock - ultimoCambioClock))`
* **Fórmula final:**
  $$\text{Lq} = \frac{\text{areaAcumulada}}{\text{Tiempo Total Simulación}}$$

---

### E. Relación Fundamental: Ley de Little (Verificación Teórica en Parcial)
Si en el parcial te piden verificar consistencia entre métricas:
$$L = \lambda \cdot W$$
$$L_q = \lambda \cdot W_q$$
$$W = W_q + \text{Tiempo Promedio de Servicio}$$

Donde $\lambda$ es la tasa efectiva de arribos ($\lambda = \frac{\text{Arribos Totales}}{\text{Tiempo Simulado}}$).
