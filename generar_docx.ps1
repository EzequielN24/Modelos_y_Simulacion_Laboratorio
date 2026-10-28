param(
    [string]$OutputPath = "INFORME_FINAL.docx"
)

Add-Type -AssemblyName System.IO.Compression.FileSystem

$tempDir = Join-Path $env:TEMP ("docx_" + [System.Guid]::NewGuid().ToString("N"))
New-Item -ItemType Directory -Path $tempDir | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tempDir "_rels") | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tempDir "word") | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tempDir "word\_rels") | Out-Null

$contentTypes = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
</Types>
'@

$rootRels = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>
'@

$wordRels = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>
'@

$stylesXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:docDefaults>
    <w:rPrDefault>
      <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri" w:cs="Calibri"/>
      <w:sz w:val="22"/>
      <w:color w:val="262626"/>
    </w:rPrDefault>
    <w:pPrDefault>
      <w:pPr>
        <w:spacing w:after="160" w:line="276" w:lineRule="auto"/>
      </w:pPr>
    </w:pPrDefault>
  </w:docDefaults>
  <w:style w:type="paragraph" w:styleId="Title">
    <w:name w:val="Title"/>
    <w:pPr>
      <w:spacing w:before="240" w:after="200"/>
      <w:jc w:val="center"/>
    </w:pPr>
    <w:rPr>
      <w:rFonts w:ascii="Calibri Light" w:hAnsi="Calibri Light"/>
      <w:b/>
      <w:sz w:val="44"/>
      <w:color w:val="1F497D"/>
    </w:rPr>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Subtitle">
    <w:name w:val="Subtitle"/>
    <w:pPr>
      <w:spacing w:after="400"/>
      <w:jc w:val="center"/>
    </w:pPr>
    <w:rPr>
      <w:rFonts w:ascii="Calibri Light" w:hAnsi="Calibri Light"/>
      <w:i/>
      <w:sz w:val="24"/>
      <w:color w:val="595959"/>
    </w:rPr>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Heading1">
    <w:name w:val="heading 1"/>
    <w:pPr>
      <w:spacing w:before="360" w:after="120"/>
    </w:pPr>
    <w:rPr>
      <w:rFonts w:ascii="Calibri Light" w:hAnsi="Calibri Light"/>
      <w:b/>
      <w:sz w:val="30"/>
      <w:color w:val="1F497D"/>
    </w:rPr>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Heading2">
    <w:name w:val="heading 2"/>
    <w:pPr>
      <w:spacing w:before="240" w:after="80"/>
    </w:pPr>
    <w:rPr>
      <w:rFonts w:ascii="Calibri Light" w:hAnsi="Calibri Light"/>
      <w:b/>
      <w:sz w:val="26"/>
      <w:color w:val="2E74B5"/>
    </w:rPr>
  </w:style>
  <w:style w:type="paragraph" w:styleId="Heading3">
    <w:name w:val="heading 3"/>
    <w:pPr>
      <w:spacing w:before="160" w:after="60"/>
    </w:pPr>
    <w:rPr>
      <w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/>
      <w:b/>
      <w:sz w:val="24"/>
      <w:color w:val="1F497D"/>
    </w:rPr>
  </w:style>
</w:styles>
'@

$documentXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:body>
    <w:p>
      <w:pPr><w:pStyle w:val="Title"/></w:pPr>
      <w:r><w:t>Informe de Laboratorio: Simulador de Eventos Discretos para Gestion Aeroportuaria</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:pStyle w:val="Subtitle"/></w:pPr>
      <w:r><w:t>Catedra: Modelos y Simulacion | Licenciatura en Ciencias de la Computacion | Octubre 2026</w:t></w:r>
    </w:p>

    <!-- SECCION 1 -->
    <w:p>
      <w:pPr><w:pStyle w:val="Heading1"/></w:pPr>
      <w:r><w:t>1. Introduccion y Objetivos</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:pStyle w:val="Heading2"/></w:pPr>
      <w:r><w:t>1.1. Contexto del Problema</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>La gestion eficiente del trafico aereo y de la infraestructura aeroportuaria constituye uno de los desafios operacionales mas complejos en el transporte moderno. La pista de aterrizaje es el recurso critico del sistema: opera bajo restricciones estrictas de seguridad donde solo una aeronave puede realizar la maniobra de descenso y despeje a la vez. Cuando la tasa de arribos excede temporalmente la capacidad de atencion de las pistas disponibles, se generan colas de espera en el espacio aereo circundante.</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>La acumulacion excesiva de retrasos no solo impacta en la satisfaccion de los pasajeros y en los costos operativos de combustible para las aerolineas, sino que compromete la seguridad operacional si las aeronaves permanecen tiempos prolongados en patron de espera. Por otro lado, la construccion y el mantenimiento preventivo y correctivo del pavimento de una pista representan inversiones millonarias. En consecuencia, el diseno y la planificacion de la infraestructura aeroportuaria requieren un balance optimo entre el nivel de servicio ofrecido (tiempos de espera acotados) y los costos de capital y conservacion asociados a la cantidad de pistas operativas.</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>El presente trabajo aborda esta problematica mediante el desarrollo y ejecucion de un Simulador de Eventos Discretos (DES - Discrete Event Simulation) programado en Java 21, modelando la dinamica de arribos, asignacion de colas, ocupacion de pistas y degradacion fisica del suelo a lo largo de un horizonte temporal de 4 semanas continuas de operacion (40.320 minutos).</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:pStyle w:val="Heading2"/></w:pPr>
      <w:r><w:t>1.2. Objetivos del Practico</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>El objetivo general consiste en disenar, implementar y analizar un simulador orientado a eventos que permita instanciar realidades operativas y evaluar cientificamente diferentes configuraciones aeroportuarias bajo incertidumbre estocastica.</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>Los objetivos especificos se articulan en los siguientes ejes:</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>1. Implementacion de la Arquitectura de Simulacion: </w:t></w:r>
      <w:r><w:t>Construir un motor de simulacion basado en la Lista de Eventos Futuros (FEL - Future Event List) y la tecnica de bootstrapping, donde cada evento de arribo planifica autonomamente el arribo sucesor. Representar el avance temporal a traves de un reloj global (clock) y transiciones de estado asociadas a eventos Arrival y Departure, aplicando principios SOLID y patrones de diseno (Observer para Probes, Strategy para politicas de cola).</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>2. Desarrollo de Generadores de Variables Aleatorias: </w:t></w:r>
      <w:r><w:t>Implementar e integrar generadores de variables aleatorias mediante transformacion inversa y funciones continuas y discretas (Uniforme, Exponencial, Normal y Empirica Discreta). Soportar procesos de Poisson no homogeneos para modelar fluctuaciones de trafico entre horarios pico y horarios normales.</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>3. Evolucion y Complejizacion del Modelo: </w:t></w:r>
      <w:r><w:t>Abordar secuencialmente la Etapa 1 (cabotaje: 1 pista, 1 cola, tablas empiricas), la Etapa 2 (multiservidor: hasta 5 pistas, colas individuales, politica Shortest Queue, desgaste con distribucion Normal N(mu=5, sigma=1)) y la Etapa 3 (experimentacion basada en 50 replicas independientes de 4 semanas).</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>4. Analisis de Sensibilidad y Toma de Decisiones: </w:t></w:r>
      <w:r><w:t>Evaluar la configuracion optima de pistas garantizando el estricto cumplimiento del nivel de servicio (tiempo maximo de espera en cola inferior a 2 horas o 120 minutos), estimando con un 95% de confianza (t de Student con 49 grados de libertad) los parametros globales de rendimiento y durabilidad.</w:t></w:r>
    </w:p>

    <!-- SECCION 2 -->
    <w:p>
      <w:pPr><w:pStyle w:val="Heading1"/></w:pPr>
      <w:r><w:t>2. Fundamentos de Modelado y Arquitectura del Simulador</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:pStyle w:val="Heading2"/></w:pPr>
      <w:r><w:t>2.1. Conceptos Fundamentales de la Simulacion de Eventos Discretos (DES)</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>Un sistema de simulacion orientada a eventos discretos modela la evolucion dinamica de un sistema fisico en el cual las variables de estado cambian exclusivamente en instantes de tiempo puntuales y discretos (ocurrencia de eventos), permaneciendo inalteradas durante los intervalos intermedios. El simulador implementado se fundamenta en tres abstracciones cardinales:</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>a) Estado del Sistema: </w:t></w:r>
      <w:r><w:t>Conjunto de variables y estructuras que describen el estado fisico en cualquier instante: condicion de cada pista (libre u ocupada), aeronaves presentes en cada cola de espera y unidades de durabilidad residual del pavimento.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>b) Reloj de Simulacion (Clock): </w:t></w:r>
      <w:r><w:t>Variable escalar que representa el tiempo transcurrido en el modelo. El reloj no avanza de forma continua segundo a segundo, sino que salta cronologicamente hacia adelante al instante exacto del proximo evento planificado en la Lista de Eventos Futuros (Next-Event Time Advance).</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>c) Lista de Eventos Futuros (FEL - Future Event List): </w:t></w:r>
      <w:r><w:t>Estructura de cola de prioridad que almacena y ordena cronologicamente los eventos por su tiempo de ocurrencia. El motor de simulacion extrae secuencialmente el evento con menor tiempo, actualiza el reloj a dicho valor y despacha su logica de ejecucion.</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:pStyle w:val="Heading2"/></w:pPr>
      <w:r><w:t>2.2. Tecnica de Bootstrapping y Ciclo de Vida de los Eventos</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>Para asegurar un consumo eficiente de memoria y permitir ejecuciones de duracion indefinida, se adopta la tecnica de bootstrapping (o autoregeneracion de eventos):</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>1. Inicializacion: </w:t></w:r>
      <w:r><w:t>Al comenzar la ejecucion, el reloj se establece en t = 0.0 y se planifica en la FEL el primer arribo generado mediante el generador de tiempos entre llegadas.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>2. Ciclo Autonomo de Llegadas (Arrival): </w:t></w:r>
      <w:r><w:t>Cuando un evento Arrival se procesa en el tiempo t, este calcula inmediatamente el intervalo hasta el siguiente arribo (delta_t) y coloca un nuevo evento Arrival en la FEL para el instante t + delta_t. De esta manera, los arribos se suceden ininterrumpidamente.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>3. Planificacion de Descenso y Salida (Departure): </w:t></w:r>
      <w:r><w:t>Si al arribar existe una pista libre, el avion inicia el aterrizaje de inmediato. Se sortea el tiempo de descenso (delta_s) y se programa un evento Departure en t + delta_s. Si todas las pistas estan ocupadas, el avion ingresa a la cola correspondiente segun la estrategia vigente.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>4. Cierre de Servicio: </w:t></w:r>
      <w:r><w:t>Al dispararse el evento Departure, la aeronave finaliza su maniobra y abandona el sistema. El servidor examina su cola: si hay aeronaves esperando, desencola a la primera segun politica FIFO y programa una nueva salida; si la cola esta vacia, el servidor pasa a estado inactivo (idle) y se notifica a las sondas de medicion.</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:pStyle w:val="Heading2"/></w:pPr>
      <w:r><w:t>2.3. Principios S.O.L.I.D. y Patrones Arquitectonicos</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>El diseno del software se rige por los principios fundamentales de la ingenieria orientada a objetos:</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>Single Responsibility (S): </w:t></w:r>
      <w:r><w:t>Cada clase tiene un unico rol. Simulation se encarga del avance temporal; los eventos administran cambios de estado; y las sondas (Probes) recopilan estadisticas sin intervenir en la logica operativa.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>Open/Closed (O): </w:t></w:r>
      <w:r><w:t>El simulador esta abierto a la extension e incorpora nuevas politicas de encolado o distribuciones probabilistas sin requerir modificaciones en su motor principal.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>Liskov Substitution (L): </w:t></w:r>
      <w:r><w:t>Todos los generadores implementan TimeGenerator y RandomVariate, resultando completamente intercambiables entre distribuciones empiricas o teoricas.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>Interface Segregation (I): </w:t></w:r>
      <w:r><w:t>La interfaz Probe proporciona metodos default vacios para que cada sonda implemente unicamente las notificaciones requeridas. Las vistas inmutables (ServerView, QueueView, EntityView) evitan modificaciones colaterales del estado desde las sondas.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>Dependency Inversion (D): </w:t></w:r>
      <w:r><w:t>Simulation no se acopla a implementaciones concretas, sino a abstracciones inyectadas externamente en su instanciacion.</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:pStyle w:val="Heading3"/></w:pPr>
      <w:r><w:t>Patrones de Diseno:</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>Patron Observer (Probes): </w:t></w:r>
      <w:r><w:t>Permite la medicion desacoplada de tiempos de transito, espera, inactividad y degradacion mediante eventos de ciclo de vida.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>Patron Strategy: </w:t></w:r>
      <w:r><w:t>Encapsula la politica de seleccion de servidores (FirstAvailable) y de colas (ShortestQueue, resolviendo empates por menor indice de pista).</w:t></w:r>
    </w:p>

    <!-- SECCION 3 -->
    <w:p>
      <w:pPr><w:pStyle w:val="Heading1"/></w:pPr>
      <w:r><w:t>3. Supuestos del Modelo y Criterios Adoptados</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:pStyle w:val="Heading2"/></w:pPr>
      <w:r><w:t>3.1. Supuestos Operativos del Sistema Aeroportuario</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>Para garantizar la validez teorica, consistencia estadistica y representatividad real del fenomeno, se establecieron los siguientes supuestos operativos:</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>1. Horizonte Temporal Finito y Continuo: </w:t></w:r>
      <w:r><w:t>Cada simulacion opera durante un horizonte estandar de 4 semanas ininterrumpidas, totalizando 40.320 minutos de reloj. No se consideran pausas nocturnas ni cancelaciones por factores climaticos adversos.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>2. Servidores Unitarios Exclusivos: </w:t></w:r>
      <w:r><w:t>Cada pista aloja estrictamente a una sola aeronave a la vez durante todo su tiempo de maniobra y descenso de pasajeros, garantizando las normas internacionales de seguridad aerea.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>3. Mecanismo de Encolado y Disciplina de Servicio: </w:t></w:r>
      <w:r><w:t>El sistema multiservidor cuenta con colas individuales e independientes por pista. Si hay pistas libres, la aeronave toma la primera libre (FirstAvailable). Si todas estan ocupadas, ingresa a la pista con menor longitud de cola (ShortestQueue). Los empates en tamano de cola se resuelven asignando la pista de menor identificador. No se admite cambio de cola (jockeying) y rige una disciplina estricta FIFO.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>4. Desgaste y Degradacion del Suelo: </w:t></w:r>
      <w:r><w:t>Cada pista parte con una durabilidad inicial de 3.000 unidades. Cada aterrizaje completado deduce inmediatamente una cantidad de desgaste sorteada segun una distribucion Normal con media mu = 5 y desvio sigma = 1 unidades. No se simulan reparaciones intermedias en las 4 semanas, permitiendo registrar la degradacion bruta acumulada.</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:pStyle w:val="Heading2"/></w:pPr>
      <w:r><w:t>3.2. Criterios Metodologicos de Medicion (Probes)</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>1. Medicion de Minimos Estrictamente Mayores a Cero (min > 0): </w:t></w:r>
      <w:r><w:t>Cuando un avion encuentra una pista desocupada, su tiempo de espera es exactamente 0. Si se registrara dicho valor, la metrica del minimo quedaria neutralizada e invariablemente en 0. Por ende, las sondas MinWaitingTime, MinSystemTime, MinIdleTime y MinQueueSize filtran unicamente valores estrictamente positivos (t > 0), reflejando la minima demora o cola efectiva cuando esta se produjo.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>2. Cierre Exacto del Tiempo de Ocio (Idle Time): </w:t></w:r>
      <w:r><w:t>Para asegurar la rigurosa conservacion temporal, al finalizar la simulacion (endClock = 40.320 min), la sonda IdleTime computa el tramo de inactividad que quedo abierto desde la ultima salida hasta el reloj final (endClock - idleStartTime), sumandolo al acumulador total.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>3. Manejo Robusto de Servidores Subutilizados: </w:t></w:r>
      <w:r><w:t>En escenarios con baja saturacion (por ejemplo pistas 4 o 5), algunas colas nunca llegan a acumular aviones o registrar esperas positivas. Para estas pistas, las sondas retornan 0 de forma segura en lugar de arrojar excepciones por ausencia de muestras.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>4. Restriccion Operacional Innegociable (SLA): </w:t></w:r>
      <w:r><w:t>Se define que ninguna aeronave puede esperar mas de 2 horas (120 minutos) en cola. Cualquier configuracion de infraestructura cuyo tiempo maximo de espera supere este umbral se considera inadmisible para la operacion del aeropuerto.</w:t></w:r>
    </w:p>

    <!-- SECCION 4 -->
    <w:p>
      <w:pPr><w:pStyle w:val="Heading1"/></w:pPr>
      <w:r><w:t>4. Desarrollo por Etapas e Implementacion</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:pStyle w:val="Heading2"/></w:pPr>
      <w:r><w:t>4.1. Etapa 1: Aeropuerto de Vuelos de Cabotaje (1 Pista, 1 Cola)</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>En esta etapa base se modelo un sistema con 1 pista y 1 cola FIFO bajo distribuciones empiricas tabuladas en 4 semanas (40.320 minutos). Tiempos entre arribos (Tabla 1): 10 min (p=0.35), 15 min (p=0.45), 17 min (p=0.20), media teorica = 13.65 min. Tiempos de descenso (Tabla 2): 8 min (p=0.38), 10 min (p=0.32), 13 min (p=0.10), 15 min (p=0.20), media teorica = 10.54 min. Factor de utilizacion teorico rho = 10.54 / 13.65 = 77.2%.</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:rPr><w:b/></w:rPr><w:t>Resultados obtenidos en 4 semanas: </w:t></w:r>
      <w:r><w:t>Arribaron 2.943 aeronaves y aterrizaron 2.942. El tiempo de transito promedio fue 11.525 min (max: 31.0 min, min>0: 8.0 min). El tiempo medio de espera fue 0.925 min (max: 21.0 min, min>0: 1.0 min). El tiempo de ocio total fue de 9.131 min (22.65% del tiempo de simulacion, coincidente con el 22.8% teorico). Longitud maxima de cola: 2 aeronaves.</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:pStyle w:val="Heading2"/></w:pPr>
      <w:r><w:t>4.2. Etapa 2: Multiservidor, Variables Aleatorias y Complejizacion</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>Se integro la biblioteca random-variate-gen-lab con distribuciones continuas: Uniforme U(10, 25) para descenso (media 17.5 min); arribos no homogeneos mediante NonHomogeneousArrivalGenerator (Exp(mu=9) en horas pico [09-13hs y 20-23hs], Exp(mu=15) en horas valle); y Normal N(mu=5, sigma=1) para desgaste de suelo.</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:rPr><w:b/></w:rPr><w:t>Analisis de Sensibilidad (2 a 5 Pistas): </w:t></w:r>
      <w:r><w:t>Se contrasto el cumplimiento de la restriccion de espera maxima inferior a 120 minutos (2 horas):</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>- Con 2 Pistas: </w:t></w:r>
      <w:r><w:t>Espera media = 153.28 min. Espera maxima = 638.82 min (~10.6 h). RECHAZADA por violacion categorica del SLA.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>- Con 3 Pistas: </w:t></w:r>
      <w:r><w:t>Espera media = 9.94 min. Espera maxima = 200.92 min (~3.3 h). RECHAZADA por exceder las 2 horas admisibles.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>- Con 4 Pistas: </w:t></w:r>
      <w:r><w:t>Espera media = 0.39 min (~23 s). Espera maxima = 29.46 min. APROBADA y postulada como CONFIGURACION OPTIMA al respetar holgadamente la cota con costos razonables.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>- Con 5 Pistas: </w:t></w:r>
      <w:r><w:t>Espera media = 0.14 min. Espera maxima = 17.87 min. Sobredimensionada innecesariamente (72.2% de ocio global; pista 5 ociosa > 93%).</w:t></w:r>
    </w:p>

    <w:p>
      <w:pPr><w:pStyle w:val="Heading2"/></w:pPr>
      <w:r><w:t>4.3. Etapa 3: Replicacion de Ejecuciones e Intervalos de Confianza (95%)</w:t></w:r>
    </w:p>
    <w:p>
      <w:r><w:t>Para la configuracion optima de 4 pistas, se ejecutaron 50 replicas independientes de 4 semanas completas (40.320 min c/u) con semillas aleatorias diferenciadas. Se construyeron intervalos de confianza del 95% empleando la distribucion t de Student (gl = 49, t critico = 2.009575):</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>1. Aeronaves Arribadas: </w:t></w:r>
      <w:r><w:t>Media: 3.194,16 | IC 95%: [3.180,55 ; 3.207,77] aeronaves.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>2. Aeronaves que Aterrizaron: </w:t></w:r>
      <w:r><w:t>Media: 3.193,06 | IC 95%: [3.179,40 ; 3.206,72] aeronaves.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>3. Tiempo de Transito: </w:t></w:r>
      <w:r><w:t>Media: [17,90 ; 17,96] min. Maximo: [57,40 ; 67,73] min. Minimo (>0): [10,00 ; 10,00] min.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>4. Tiempo de Espera en Cola: </w:t></w:r>
      <w:r><w:t>Media: [0,39 ; 0,43] min. Maximo: [38,84 ; 49,30] min (cumplimiento absoluto del limite de 120 min). Minimo (>0): [0,01 ; 0,02] min.</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>5. Proporcion de Ocio Global: </w:t></w:r>
      <w:r><w:t>IC 95%: [65,16% ; 65,46%].</w:t></w:r>
    </w:p>
    <w:p>
      <w:pPr><w:ind w:left="400"/></w:pPr>
      <w:r><w:rPr><w:b/></w:rPr><w:t>6. Durabilidad Final de Suelo (Base: 3.000 unidades): </w:t></w:r>
      <w:r><w:t>Pista 1: [-3.758,96 ; -3.706,50]. Pista 2: [-1.826,74 ; -1.772,81]. Pista 3: [47,66 ; 96,55]. Pista 4: [1.470,22 ; 1.516,12].</w:t></w:r>
    </w:p>

    <w:sectPr>
      <w:pgSz w:w="11906" w:h="16838"/>
      <w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440"/>
    </w:sectPr>
  </w:body>
</w:document>
'@

[System.IO.File]::WriteAllText((Join-Path $tempDir "[Content_Types].xml"), $contentTypes, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText((Join-Path $tempDir "_rels\.rels"), $rootRels, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText((Join-Path $tempDir "word\_rels\document.xml.rels"), $wordRels, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText((Join-Path $tempDir "word\styles.xml"), $stylesXml, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText((Join-Path $tempDir "word\document.xml"), $documentXml, [System.Text.Encoding]::UTF8)

$destinationPath = Resolve-Path . | Join-Path -ChildPath $OutputPath
if (Test-Path $destinationPath) {
    Remove-Item $destinationPath -Force
}

[System.IO.Compression.ZipFile]::CreateFromDirectory($tempDir, $destinationPath)
Remove-Item -Recurse -Force $tempDir

Write-Output "DOCX actualizado con exito en: $destinationPath"
