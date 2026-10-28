# Informe de Laboratorio: Simulador de Eventos Discretos para Gestion Aeroportuaria

**Catedra:** Modelos y Simulacion  
**Carrera:** Licenciatura en Ciencias de la Computacion  
**Fecha:** Octubre 2026  

---

## 1. Introduccion y Objetivos

### 1.1. Contexto del Problema
La gestion eficiente del trafico aereo y la infraestructura aeroportuaria constituye uno de los desafios operacionales mas complejos en el transporte moderno. La pista de aterrizaje es el recurso critico del sistema: opera bajo restricciones estrictas de seguridad donde solo una aeronave puede realizar la maniobra de descenso y despeje a la vez. Cuando la tasa de arribos excede temporalmente la capacidad de atencion de las pistas disponibles, se generan colas de espera en el espacio aereo.

La acumulacion de retrasos no solo impacta en la satisfaccion de los pasajeros y los costos de combustible de las aerolineas, sino que compromete la seguridad operativa si las aeronaves permanecen tiempos excesivos en patron de espera. Por otro lado, la construccion y el mantenimiento preventivo y correctivo del pavimento de una pista representan inversiones millonarias. En consecuencia, el diseno de la infraestructura aeroportuaria requiere un balance optimo entre el nivel de servicio ofrecido (tiempos de espera acotados) y los costos de capital y mantenimiento asociados a la cantidad de pistas operativas.

El presente trabajo aborda esta problematica mediante el desarrollo y ejecucion de un **Simulador de Eventos Discretos (DES - Discrete Event Simulation)** programado en Java 21, modelando la dinamica de arribos, asignacion de colas, ocupacion de pistas y degradacion fisica del suelo a lo largo de un horizonte temporal de 4 semanas continuas de operacion (40.320 minutos).

---

### 1.2. Objetivos del Practico

El objetivo general es disenar, implementar y analizar un simulador de eventos discretos que permita instanciar realidades operativas y evaluar cientificamente diferentes configuraciones aeroportuarias bajo incertidumbre estocastica.

#### Objetivos Especificos:
1. **Implementacion de la Arquitectura de Simulacion:**
   * Construir un motor de simulacion basado en la **Lista de Eventos Futuros (FEL - Future Event List)** y la tecnica de ***bootstrapping***, donde cada evento de arribo planifica de manera autonoma el arribo posterior.
   * Representar formalmente el estado del sistema, el avance del tiempo mediante un reloj de simulacion global (*clock*) y las transiciones de estado asociadas a eventos de llegada (*Arrival*) y salida (*Departure*).
   * Aplicar principios de diseno de software orientado a objetos y patrones arquitectonicos (Observer para *Probes*, Strategy para politicas de encolado, generadores modulares).

2. **Desarrollo de Generadores de Variables Aleatorias:**
   * Implementar e integrar generadores estocasticos basados en el metodo de transformacion inversa y funciones de densidad continuas y discretas (Uniforme, Exponencial, Normal y Empirica Discreta).
   * Soportar procesos de Poisson no homogeneos para representar variaciones temporales de demanda a lo largo del dia (horas pico vs. horas valle).

3. **Evolucion y Complejizacion del Modelo:**
   * **Etapa 1 (Cabotaje):** Modelar y validar un escenario base de un unico servidor (1 pista) y una cola FIFO con distribuciones empiricas tabuladas.
   * **Etapa 2 (Multiservidor y Dinamica de Desgaste):** Extender el sistema a multiples servidores con colas individuales, politica de seleccion por cola mas corta (*Shortest Queue*), durabilidad inicial del suelo (3.000 unidades) y desgaste estocastico por aterrizaje segun una distribucion Normal.
   * **Etapa 3 (Diseno Experimental e Intervalos de Confianza):** Realizar 50 replicas estadisticamente independientes de 4 semanas de duracion para obtener estimadores puntuales e intervalos de confianza del 95% utilizando la distribucion t de Student.

4. **Toma de Decisiones y Recomendacion Operativa:**
   * Evaluar mediante analisis de sensibilidad la cantidad optima de pistas de aterrizaje (entre 2 y 5 pistas).
   * Garantizar el cumplimiento estricto del acuerdo de nivel de servicio (SLA): **ninguna aeronave debe esperar mas de 2 horas (120 minutos) en cola**.
   * Justificar una recomendacion fundada tecnica y economicamente para la administracion aeroportuaria, considerando tiempos de espera, factores de utilizacion/ocio y vida util del pavimento.



---

## 2. Fundamentos de Modelado y Arquitectura del Simulador

### 2.1. Conceptos Fundamentales de la Simulacion de Eventos Discretos (DES)
Un sistema de simulacion orientada a eventos discretos modela la evolucion dinamica de un sistema fisico en el cual las variables de estado cambian exclusivamente en instantes de tiempo puntuales y discretos (ocurrencia de eventos), manteniendose inalteradas durante los intervalos intermedios.

El simulador implementado se fundamenta en tres abstracciones cardinales:
1. **Estado del Sistema:** Conjunto de variables y estructuras que describen el estado fisico en cualquier instante: estado de las pistas (ocupada / libre), longitud y aeronaves presentes en cada una de las colas de espera, y durabilidad residual del pavimento.
2. **Reloj de Simulacion (*Clock*):** Variable que representa el tiempo actual dentro de la realidad simulada. El reloj no avanza de forma continua segundo a segundo, sino que salta cronologicamente hacia adelante al instante exacto del proximo evento planificado en la Lista de Eventos Futuros (*Next-Event Time Advance*).
3. **Lista de Eventos Futuros (FEL - *Future Event List*):** Estructura de cola de prioridad que almacena y ordena cronologicamente por su tiempo de ocurrencia todos los eventos agendados. El motor de simulacion extrae de forma iterativa el evento con menor marca temporal, actualiza el reloj a dicho tiempo y despacha la ejecucion del evento.

### 2.2. Tecnica de *Bootstrapping* y Ciclo de Vida de los Eventos
Para automatizar la progresion del modelo sin precargar millones de arribos en memoria, se utiliza la tecnica de ***bootstrapping*** (o autoregeneracion de eventos):
* **Evento Inicial:** Al comenzar la simulacion (`Simulation.run()`), se inicializa el reloj en `initClock = 0.0` y se introduce en la FEL el primer arribo generado mediante el generador de tiempos entre llegadas.
* **Ciclo Autonomo de Llegadas:** Cuando un evento `Arrival` es extraido de la FEL y ejecutado en el tiempo `t`, este genera inmediatamente el tiempo hasta la siguiente llegada `delta_t` y agenda en la FEL un nuevo evento `Arrival` para el instante `t + delta_t`. De este modo, la cadena de arribos se sostiene de manera indefinida sin intervencion externa.
* **Planificacion de Salidas:** Cuando una aeronave ingresa inmediatamente a una pista libre o es desencolada tras finalizar el servicio anterior, se calcula el tiempo de duracion de la maniobra de aterrizaje `delta_s` y se programa en la FEL un evento `Departure` en el instante `t + delta_s`.
* **Cierre de Servicio:** Al ejecutarse `Departure`, la aeronave desciende a sus pasajeros y despeja la pista. El servidor verifica si existen aeronaves en su cola de espera: si hay aeronaves aguardando, extrae la primera (politica FIFO) e inicia una nueva salida; si la cola esta vacia, la pista pasa a estado inactivo (*idle*) y notifica a los observadores.

### 2.3. Principios de Diseno S.O.L.I.D. y Patrones Arquitectonicos
La arquitectura del software fue concebida priorizando la alta cohesion, el bajo acoplamiento y la extensibilidad, siguiendo las mejores practicas de la ingenieria de software:

* **S - Responsabilidad Unica (*Single Responsibility Principle*):**
  * Las clases de eventos (`Arrival`, `Departure`) solo contienen la logica de transicion de estado y planificacion temporal.
  * El motor `Simulation` unicamente orquesta la extraccion de eventos y el avance del reloj.
  * Los componentes `Probe` se limitan a capturar datos y computar metricas sin interferir en las reglas operativas del aeropuerto.
* **O - Abierto/Cerrado (*Open/Closed Principle*):**
  * El sistema permite incorporar nuevas politicas de asignacion de pistas o nuevos generadores de probabilidad sin necesidad de modificar el nucleo del simulador (`QueueingModel` o `Simulation`).
* **L - Sustitucion de Liskov (*Liskov Substitution Principle*):**
  * Todos los generadores implementan interfaces comunes (`TimeGenerator`, `RandomVariate`), permitiendo intercambiar de manera transparente distribuciones empiricas por funciones continuas (Uniforme, Exponencial).
* **I - Segregacion de Interfaces (*Interface Segregation Principle*):**
  * La interfaz `Probe` provee metodos *default* vacios (`entityArrived`, `entityEnteredQueue`, `serviceStarted`, `entityDeparted`, `serverBecameIdle`, `serverBecameBusy`, `simulationStarted`, `simulationFinished`). Cada sonda observadora implementa exclusivamente las notificaciones relevantes para su medicion.
  * Vistas inmutables como `ServerView`, `QueueView` y `EntityView` exponen a las sondas unicamente lectura de estado, impidiendo modificaciones accidentales del modelo durante la observacion.
* **D - Inversion de Dependencias (*Dependency Inversion Principle*):**
  * La clase `Simulation` depende de abstracciones (`StrategyBag`, `TimeGenerator`, `List<Probe>`) inyectadas a traves de su constructor y configuradas externamente en el `Main`.

### 2.4. Patrones de Diseno Aplicados
* **Patron Observer (*Probes*):** Implementa un esquema de suscripcion desacoplado. Durante la ejecucion, el contexto de simulacion notifica a una coleccion de `Probe` en cada cambio de estado significativo. Esto garantiza que la medicion de tiempos de transito, tiempos de espera, ocio de pistas y desgaste de suelo no ensucie la logica del modelo.
* **Patron Strategy (*Strategies*):** Permite parametrizar dinamicamente el comportamiento del sistema:
  * `ShortestQueue`: Asigna la aeronave a la pista que posea la cola de menor longitud, o a la de menor indice en caso de empate.
  * `FirstAvailable`: Asigna a la primera pista disponible cuando hay capacidad ociosa.
* **Patron Factory / Bag:** La clase `StrategyBag` agrupa y transporta de forma tipada las estrategias configuradas para la ejecucion.


---

## 3. Supuestos del Modelo y Criterios Adoptados

Para garantizar la validez teorica, la consistencia estadistica y la viabilidad computacional de la simulacion, se definieron formalmente los supuestos operativos del sistema y los criterios metodologicos de medicion.

### 3.1. Supuestos Operativos del Sistema Aeroportuario
1. **Horizonte de Simulacion Finito y Continuo:**
   * La duracion de cada corrida es de 4 semanas continuas, equivalentes a 40.320 minutos de operacion ininterrumpida (4 semanas * 7 dias * 24 horas * 60 minutos).
   * No se contemplan interrupciones por condiciones climaticas adversas ni cierres nocturnos generales del espacio aereo (las operaciones continuan las 24 horas).
2. **Capacidad y Caracteristicas de las Pistas:**
   * Cada pista de aterrizaje es tratada como un servidor unitario: aloja estrictamente a una sola aeronave a la vez durante todo su tiempo de maniobra y descenso de pasajeros.
   * La duracion del aterrizaje modela desde que el avion toca pista hasta que los pasajeros descienden y la pista queda plenamente despejada para la siguiente operacion.
3. **Mecanismo de Asignacion y Colas Individuales:**
   * En la configuracion multiservidor, cada pista dispone de su propia cola de espera individual (arquitectura m servidores / m colas).
   * Al arribar una aeronave al espacio aereo:
     * Si una o mas pistas estan libres, la aeronave es asignada inmediatamente a aterrizar segun la estrategia `FirstAvailable` (priorizando la pista de menor identificador numerico).
     * Si todas las pistas se encuentran ocupadas, la aeronave ingresa a la cola de la pista que posea la menor cantidad de aviones aguardando (`ShortestQueue`). En caso de igualdad de longitud de cola entre dos o mas pistas, se selecciona la pista con menor indice (orden lexicografico: Pista 1, luego Pista 2, etc.).
   * No se permite el trasvase (*jockeying*) ni el reordenamiento de aeronaves entre colas una vez asignadas. La disciplina dentro de cada cola es estrictamente FIFO (*First In, First Out*).
4. **Dinamica de Desgaste y Durabilidad del Suelo:**
   * Cada pista inicia la simulacion con una durabilidad estructural de 3.000 unidades.
   * Por cada aterrizaje completado, el impacto y rodadura de la aeronave genera un desgaste inmediato extraido estocasticamente de una distribucion Normal con media mu = 5 y desviacion estandar sigma = 1 unidades de durabilidad.
   * No se modelan mantenimientos intermedios que reparen la pista durante el transcurso de las 4 semanas, lo que permite evaluar el desgaste neto acumulado al final del periodo operacional y proyectar la degradacion a mediano plazo.

### 3.2. Criterios Metodologicos de Medicion (Sondas / *Probes*)
1. **Medicion de Tiempos Minimos Estrictamente Positivos (`min > 0`):**
   * El enunciado requiere el computo del tiempo medio, maximo y "minimo (distinto de cero)" para el tiempo de espera, tiempo de transito y ocio.
   * Criterio adoptado: Si una aeronave ingresa inmediatamente a una pista libre, su tiempo de espera es formalmente cero (0.0). Incluir el cero como minimo desvirtuaria la metrica de demoras, volviendola trivial (siempre 0). Por ende, las sondas `MinWaitingTime`, `MinSystemTime`, `MinIdleTime` y `MinQueueSize` filtran estrictamente valores `t > 0`, registrando el minimo tiempo positivo no nulo observado.
2. **Registro Integral y Continuo del Tiempo de Ocio (*Idle Time*):**
   * El tiempo de inactividad de una pista se acumula a partir de los periodos en los cuales el servidor permanece desocupado sin aeronaves en atencion.
   * Para asegurar la conservacion temporal exacta, al alcanzarse el tiempo final de la simulacion (`endClock`), la sonda `IdleTime` computa el ultimo periodo inactivo transcurrido desde el momento en que la pista quedo libre hasta el instante de cierre (`endClock - idleStartTime`), sumandolo al acumulador global antes de calcular la fraccion de ocio respecto al tiempo total simulado.
3. **Manejo Seguro de Servidores Subutilizados:**
   * En escenarios con 4 o 5 pistas, las pistas de mayor indice reciben un caudal significativamente menor debido a la politica de seleccion prioritaria por menor indice ante igualdad de longitud de cola.
   * Como consecuencia natural, puede ocurrir que en una pista especifica su cola permanezca en tamano cero durante toda la corrida (o que nunca registre esperas mayores a cero). Para estas pistas, las sondas retornan 0 de forma segura en lugar de emitir excepciones por falta de muestras, manteniendo la integridad de las metricas consolidadas.
4. **Acuerdo de Nivel de Servicio (SLA):**
   * Se establece como restriccion operativa innegociable que **ninguna aeronave puede superar los 120 minutos (2 horas) de espera en cola**. Cualquier configuracion de infraestructura donde el tiempo maximo de espera exceda este umbral es clasificada automaticamente como inviable para la operacion aeroportuaria.


---

## 4. Desarrollo por Etapas e Implementacion

El proyecto se ejecuto de manera incremental a traves de tres etapas sucesivas, comenzando por un caso base simple hasta alcanzar un estudio experimental exhaustivo con estimacion por intervalos de confianza.

### 4.1. Etapa 1: Aeropuerto de Vuelos de Cabotaje (1 Pista, 1 Cola)
En esta primera etapa se modelo y valido un escenario base con un unico servidor (1 pista de aterrizaje) y una cola unica con disciplina FIFO.

* **Distribuciones Probabilisticas:**
  * **Tiempos entre Arribos (Tabla 1):** Variable empirica discreta con valores y probabilidades:
    * 10 minutos: probabilidad 0.35
    * 15 minutos: probabilidad 0.45
    * 17 minutos: probabilidad 0.20
    * Media teorica esperada: 10 * 0.35 + 15 * 0.45 + 17 * 0.20 = 13.65 minutos.
  * **Tiempos de Aterrizaje / Servicio (Tabla 2):** Variable empirica discreta con valores y probabilidades:
    * 8 minutos: probabilidad 0.38
    * 10 minutos: probabilidad 0.32
    * 13 minutos: probabilidad 0.10
    * 15 minutos: probabilidad 0.20
    * Media teorica esperada: 8 * 0.38 + 10 * 0.32 + 13 * 0.10 + 15 * 0.20 = 10.54 minutos.
  * **Intensidad de Trafico Teorica (rho):** rho = media_servicio / media_arribos = 10.54 / 13.65 = 0.772 (77.2% de ocupacion teorica, 22.8% de ocio teorico).

* **Resultados Obtenidos tras 4 semanas (40.320 minutos):**
  * Aeronaves arribadas: 2.943
  * Aeronaves que completaron aterrizaje: 2.942 (1 aeronave permanecio en pista completando su maniobra al cerrar el reloj).
  * Tiempo de transito en el sistema (minutos):
    * Medio: 11.525 min
    * Maximo: 31.000 min
    * Minimo (distinto de cero): 8.000 min
  * Tiempo de espera en cola (minutos):
    * Medio: 0.925 min
    * Maximo: 21.000 min
    * Minimo (distinto de cero): 1.000 min
  * Tiempo de ocio de la pista:
    * Total acumulado: 9.131,000 minutos
    * Proporcion de ocio: 22.65% (muy proximo al 22.8% teorico).
    * Maximo ocio continuo: 34.000 min
    * Minimo ocio continuo (distinto de cero): 1.000 min
  * Longitud de la cola de espera:
    * Maximo: 2 aeronaves
    * Minimo (distinto de cero): 1 aeronave

El sistema se muestra estable bajo estas condiciones, ya que la tasa de servicio supera a la tasa de arribos (rho < 1) y los tiempos de espera promedio no superan el minuto.

---

### 4.2. Etapa 2: Multiservidor, Variables Aleatorias y Complejizacion
Para la segunda etapa, el aeropuerto se complejizo incorporando la biblioteca modular externa `random-variate-gen-lab`, adaptando generadores continuos e implementando una topologia de multiples pistas con colas individuales.

#### Generadores Implementados e Integrados:
1. **Generador Uniforme U(a, b):** Basado en el metodo de transformacion inversa: `X = a + (b - a) * U(0,1)`. Empleado para la duracion del descenso de pasajeros entre 10 y 25 minutos (media = 17.5 min).
2. **Generador Exponencial Exp(mu):** Muestreado como `X = -ln(1 - U) * mu` (o `-ln(U) * mu`).
3. **Proceso de Arribos No Homogeneo:** Se desarrollo `NonHomogeneousArrivalGenerator` para modelar las fluctuaciones del trafico aereo segun la hora del dia (ciclo diario de 1.440 minutos):
   * Horas Pico (09:00 a 13:00 hs [minutos 540 a 780] y 20:00 a 23:00 hs [minutos 1.200 a 1.380]): Exponencial con media mu = 9 minutos (alta demanda).
   * Horas Valle (resto del dia): Exponencial con media mu = 15 minutos (demanda moderada).
4. **Generador Normal N(mu, sigma):** Muestreado como `X = mu + sigma * Z`, donde Z es una variable normal estandar N(0, 1) generada por el algoritmo de Box-Muller / Marsaglia Polar. Aplicado con mu = 5 y sigma = 1 para computar el desgaste de suelo por aterrizaje.

#### Analisis de Sensibilidad: Evaluacion de 2, 3, 4 y 5 Pistas
Para responder a la consigna de postular la configuracion mas adecuada frente a la restriccion de que **las aeronaves no pueden superar 2 horas (120 minutos) de espera en cola**, se realizo una simulacion comparativa de 4 semanas para cada configuracion de pistas:

* **Configuracion con 2 Pistas:**
  * Arribos: 3.197 | Aterrizajes: 3.167
  * Tiempo medio de espera: 153.28 min (~2.5 horas)
  * **Tiempo maximo de espera: 638.82 min (~10.6 horas)** -> **RECHAZADA (Incumple categoricamente el SLA de 120 min)**.
  * Ocio global: 28.18%. Cola maxima: 48 aeronaves.
* **Configuracion con 3 Pistas:**
  * Arribos: 3.197 | Aterrizajes: 3.190
  * Tiempo medio de espera: 9.94 min
  * **Tiempo maximo de espera: 200.92 min (~3.3 horas)** -> **RECHAZADA (Supera las 2 horas toleradas en momentos de congestion)**.
  * Ocio global: 53.64%. Cola maxima: 18 aeronaves.
* **Configuracion con 4 Pistas:**
  * Arribos: 3.197 | Aterrizajes: 3.196
  * Tiempo medio de espera: 0.39 min (~23 segundos)
  * **Tiempo maximo de espera: 29.46 min** -> **APROBADA (Cumple holgadamente la restriccion de 120 min)**.
  * Ocio global: 65.23%. Cola maxima: 4 aeronaves.
* **Configuracion con 5 Pistas:**
  * Arribos: 3.197 | Aterrizajes: 3.197
  * Tiempo medio de espera: 0.14 min (~8 segundos)
  * **Tiempo maximo de espera: 17.87 min** -> **APROBADA operacionalmente, pero SOBREDIMENSIONADA**.
  * Ocio global: 72.19%. La Pista 5 permanece inactiva mas del 93% del tiempo.

**Postulacion de la Configuracion Optima:**
Se determino que **4 pistas es la configuracion optima para el aeropuerto**. Presenta un tiempo maximo de espera de solo 29.46 minutos (muy por debajo del limite de 120 min), con colas maximas de apenas 4 aviones y un tiempo promedio de espera despreciable (0.39 min). Adoptar 5 pistas representaria un sobrecosto innecesario del 25% en inversion inicial y costos de mantenimiento mensual para una pista que permaneceria ociosa casi la totalidad del tiempo.

---

### 4.3. Etapa 3: Replicacion de Ejecuciones e Intervalos de Confianza (95%)
Con el fin de obtener estimadores estadisticamente robustos y no sesgados por una unica semilla pseudoaleatoria, se planifico un experimento de simulacion compuesto por **50 replicas independientes** de 4 semanas completas de operacion (40.320 minutos por replica) para la configuracion optima de 4 pistas.

#### Metodologia Estadistica:
* Muestra de replicas: n = 50.
* Grados de libertad: gl = n - 1 = 49.
* Nivel de confianza: 1 - alfa = 0.95 (alfa = 0.05).
* Valor critico de la distribucion t de Student: t(49, 0.025) = 2.009575.
* Error estandar de la media: E = t * (s / raiz(n)), donde s es la desviacion estandar muestral.
* Intervalo de confianza: [Media - E ; Media + E].

#### Resultados Consolidados de las 50 Replicas (IC 95%):

1. **Cantidad Total de Aeronaves Arribadas:**
   * Media muestral: 3.194,16 aeronaves
   * Intervalo de confianza 95%: **[3.180,55 ; 3.207,77] aeronaves**
2. **Cantidad Total de Aeronaves que Aterrizaron:**
   * Media muestral: 3.193,06 aeronaves
   * Intervalo de confianza 95%: **[3.179,40 ; 3.206,72] aeronaves**
3. **Tiempo de Transito en el Sistema (minutos):**
   * Tiempo medio: **[17,90 ; 17,96] min** (Media: 17.93 min)
   * Tiempo maximo: **[57,40 ; 67,73] min** (Media de maximos: 62.56 min)
   * Tiempo minimo (>0): **[10,00 ; 10,00] min** (Correspondiente al extremo inferior de U(10, 25))
4. **Tiempo de Espera en Cola (minutos):**
   * Tiempo medio: **[0,39 ; 0,43] min** (Media: 0.41 min)
   * Tiempo maximo: **[38,84 ; 49,30] min** (Media de maximos: 44.07 min; en ninguna de las 50 corridas supero los 65 min)
   * Tiempo minimo (>0): **[0,01 ; 0,02] min**
5. **Proporcion de Ocio Global:**
   * Intervalo de confianza 95%: **[65,16% ; 65,46%]**
6. **Durabilidad Final del Suelo por Pista (Durabilidad inicial: 3.000 unidades):**
   * Pista 1 (mayor carga): **[-3.758,96 ; -3.706,50] unidades** (desgaste promedio: ~6.732 unidades)
   * Pista 2: **[-1.826,74 ; -1.772,81] unidades** (desgaste promedio: ~4.799 unidades)
   * Pista 3: **[47,66 ; 96,55] unidades** (conserva durabilidad positiva limite)
   * Pista 4 (menor carga): **[1.470,22 ; 1.516,12] unidades** (conserva cerca del 50% de durabilidad inicial)

Los intervalos de confianza estrechos evidencian una reducida variabilidad muestral tras 50 replicas de 4 semanas, garantizando que la configuracion de 4 pistas asegura de forma consistente y reproducible el cumplimiento del nivel de servicio requerido.
