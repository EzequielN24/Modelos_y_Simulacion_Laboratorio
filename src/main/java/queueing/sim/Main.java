package queueing.sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.random.RandomGenerator;
import queueing.sim.generators.NonHomogeneousArrivalGenerator;
import queueing.sim.probes.*;
import queueing.sim.strategies.FirstAvailable;
import queueing.sim.strategies.ShortestQueue;
import queueing.sim.strategies.StrategyBag;
import random.variates.Discrete;
import random.variates.Uniform;

/**
 * Punto de entrada principal interactivo y unificado para el Laboratorio de Simulación.
 *
 * <p>Permite seleccionar y ejecutar:
 * <ol>
 *   <li>Etapa 1: Aeropuerto de vuelos de cabotaje (1 pista, 1 cola, 4 semanas).</li>
 *   <li>Etapa 2: Aeropuerto con múltiples pistas y generadores aleatorios (configuración por defecto de 5 pistas o configurable).</li>
 *   <li>Etapa 3: Experimentación con 50 réplicas independientes e intervalos de confianza del 95%.</li>
 *   <li>Ejecución de todas las etapas en secuencia.</li>
 * </ol>
 */
public final class Main {

  private static final double SIM_LENGTH_4_SEMANAS = 4.0 * 7.0 * 24.0 * 60.0; // 40.320 minutos

  private Main() {}

  public static void main(String[] args) {
    if (args.length > 0) {
      ejecutarPorParametro(args[0]);
      return;
    }

    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.println("=================================================================");
      System.out.println("     SIMULADOR DE EVENTOS DISCRETOS — MODELOS Y SIMULACIÓN       ");
      System.out.println("=================================================================");
      System.out.println("Seleccione la etapa a ejecutar:");
      System.out.println("  1. Etapa 1: Aeropuerto de Cabotaje (1 Pista, 1 Cola)");
      System.out.println("  2. Etapa 2: Múltiples Pistas y Colas (5 Pistas por defecto)");
      System.out.println("  3. Etapa 2 (Sensibilidad): Probar con 2, 3, 4 y 5 pistas");
      System.out.println("  4. Etapa 3: 50 Réplicas e Intervalos de Confianza (95%)");
      System.out.println("  5. Ejecutar TODO (Etapas 1, 2 y 3)");
      System.out.println("  0. Salir");
      System.out.print("Opción: ");

      String entrada = scanner.nextLine().trim();
      System.out.println();

      switch (entrada) {
        case "1" -> ejecutarEtapa1();
        case "2" -> ejecutarEtapa2(5, true);
        case "3" -> {
          for (int pistas = 2; pistas <= 5; pistas++) {
            ejecutarEtapa2(pistas, true);
          }
        }
        case "4" -> ejecutarEtapa3(4, 50);
        case "5" -> {
          ejecutarEtapa1();
          ejecutarEtapa2(5, true);
          ejecutarEtapa3(4, 50);
        }
        case "0" -> {
          System.out.println("Finalizando simulador.");
          return;
        }
        default -> System.out.println("Opción no válida. Intente nuevamente.\n");
      }
    }
  }

  private static void ejecutarPorParametro(String param) {
    switch (param.toLowerCase()) {
      case "etapa1", "1" -> ejecutarEtapa1();
      case "etapa2", "2" -> ejecutarEtapa2(5, true);
      case "etapa3", "3" -> ejecutarEtapa3(4, 50);
      case "all", "todo" -> {
        ejecutarEtapa1();
        ejecutarEtapa2(5, true);
        ejecutarEtapa3(4, 50);
      }
      default -> {
        try {
          int n = Integer.parseInt(param);
          ejecutarEtapa2(n, true);
        } catch (NumberFormatException e) {
          System.out.println("Parámetro desconocido: " + param);
        }
      }
    }
  }

  // =================================================================
  // ETAPA 1
  // =================================================================

  public static void ejecutarEtapa1() {
    System.out.println("=================================================================");
    System.out.println("   ETAPA 1: AEROPUERTO DE VUELOS DE CABOTAJE (1 PISTA, 1 COLA)   ");
    System.out.println("=================================================================");
    System.out.printf("Duración de simulación: %.0f minutos (4 semanas)%n%n", SIM_LENGTH_4_SEMANAS);

    Map<Double, Double> tablaArribos = Map.of(10.0, 0.35, 15.0, 0.45, 17.0, 0.20);
    TimeGenerator arribosGen = rng -> new Discrete<>(tablaArribos, rng).sample();

    Map<Double, Double> tablaServicio = Map.of(8.0, 0.38, 10.0, 0.32, 13.0, 0.10, 15.0, 0.20);
    TimeGenerator aterrizajeGen = rng -> new Discrete<>(tablaServicio, rng).sample();

    int pistaId = 1;
    int colaId = 1;

    QueueConfig queueConfig =
        new QueueConfig(colaId, List.of(new ServerConfig(pistaId, aterrizajeGen)));
    QueueingModel model = new QueueingModel(List.of(queueConfig));

    StrategyBag strategies =
        new StrategyBag(
            new FirstAvailable(),
            queues -> queues.getFirst().id(),
            queues -> queues.getFirst().id());

    ArrivalCounter arrivals = new ArrivalCounter();
    DepartureCounter departures = new DepartureCounter();
    SystemTime systemTime = new SystemTime();
    MaxSystemTime maxSystemTime = new MaxSystemTime();
    MinSystemTime minSystemTime = new MinSystemTime();
    WaitingTime waitingTime = new WaitingTime();
    MaxWaitingTime maxWaitingTime = new MaxWaitingTime();
    MinWaitingTime minWaitingTime = new MinWaitingTime();
    IdleTime idleTime = new IdleTime();
    MaxIdleTime maxIdleTime = new MaxIdleTime();
    MinIdleTime minIdleTime = new MinIdleTime();
    MaxQueueSize maxQueueSize = new MaxQueueSize();
    MinQueueSize minQueueSize = new MinQueueSize();

    List<Probe> probes =
        List.of(
            arrivals, departures, systemTime, maxSystemTime, minSystemTime,
            waitingTime, maxWaitingTime, minWaitingTime,
            idleTime, maxIdleTime, minIdleTime, maxQueueSize, minQueueSize);

    RandomGenerator rng = RandomGenerator.of("L64X128MixRandom");
    Simulation simulation =
        new Simulation(0.0, SIM_LENGTH_4_SEMANAS, model, rng, arribosGen, strategies, probes, false);

    simulation.run();

    int totalArribos = arrivals.count();
    int totalAterrizados = departures.count();
    double tTransitoMedio = totalAterrizados > 0 ? systemTime.totalSystemTime() / totalAterrizados : 0.0;
    double tEsperaMedio = totalArribos > 0 ? waitingTime.totalWaitingTime() / totalArribos : 0.0;
    double totalOcio = idleTime.totalIdleTime(pistaId);
    double proporcionOcio = (totalOcio / SIM_LENGTH_4_SEMANAS) * 100.0;

    System.out.println("---------------------- RESULTADOS ETAPA 1 -----------------------");
    System.out.printf("1. Aeronaves que arribaron:                 %d%n", totalArribos);
    System.out.printf("2. Aeronaves que aterrizaron:                %d%n", totalAterrizados);
    System.out.println("3. Tiempo de tránsito en el sistema (minutos):");
    System.out.printf("   - Medio:   %.3f | Máximo: %.3f | Mínimo (>0): %.3f%n",
        tTransitoMedio, maxSystemTime.maxSystemTime(), minSystemTime.minSystemTime());
    System.out.println("4. Tiempo de espera en cola (minutos):");
    System.out.printf("   - Medio:   %.3f | Máximo: %.3f | Mínimo (>0): %.3f%n",
        tEsperaMedio, maxWaitingTime.maxWaitingTime(), minWaitingTime.minWaitingTime());
    System.out.println("5. Tiempo de ocio de la pista:");
    System.out.printf("   - Total:   %.3f min (%.2f %% del tiempo total)%n", totalOcio, proporcionOcio);
    System.out.printf("   - Máximo período continuo: %.3f min | Mínimo (>0): %.3f min%n",
        maxIdleTime.maxIdleTime(pistaId), minIdleTime.minIdleTime(pistaId));
    System.out.println("6. Tamaño de cola de espera:");
    System.out.printf("   - Máximo:  %d | Mínimo (>0): %d aeronave(s)%n",
        maxQueueSize.maxQueueSize(colaId), minQueueSize.minQueueSize(colaId));
    System.out.println("=================================================================\n");
  }

  // =================================================================
  // ETAPA 2
  // =================================================================

  public static ResultadoCorrida ejecutarEtapa2(int cantidadPistas, boolean imprimirReporte) {
    RandomGenerator rng = RandomGenerator.of("L64X128MixRandom");
    return ejecutarSimulacionEtapa2(cantidadPistas, rng, imprimirReporte);
  }

  public static ResultadoCorrida ejecutarSimulacionEtapa2(
      int cantidadPistas, RandomGenerator rng, boolean imprimirReporte) {

    NonHomogeneousArrivalGenerator arribosGen = new NonHomogeneousArrivalGenerator();
    TimeGenerator servicioGen = r -> new Uniform(10.0, 25.0, r).sample();

    List<QueueConfig> queueConfigs = new ArrayList<>();
    for (int i = 1; i <= cantidadPistas; i++) {
      queueConfigs.add(new QueueConfig(i, List.of(new ServerConfig(i, servicioGen))));
    }
    QueueingModel model = new QueueingModel(queueConfigs);

    StrategyBag strategies =
        new StrategyBag(
            new FirstAvailable(),
            new ShortestQueue(),
            queues -> queues.getFirst().id());

    ArrivalCounter arrivals = new ArrivalCounter();
    DepartureCounter departures = new DepartureCounter();
    SystemTime systemTime = new SystemTime();
    MaxSystemTime maxSystemTime = new MaxSystemTime();
    MinSystemTime minSystemTime = new MinSystemTime();
    WaitingTime waitingTime = new WaitingTime();
    MaxWaitingTime maxWaitingTime = new MaxWaitingTime();
    MinWaitingTime minWaitingTime = new MinWaitingTime();
    IdleTime idleTime = new IdleTime();
    MaxIdleTime maxIdleTime = new MaxIdleTime();
    MinIdleTime minIdleTime = new MinIdleTime();
    MaxQueueSize maxQueueSize = new MaxQueueSize();
    MinQueueSize minQueueSize = new MinQueueSize();
    RunwayDurabilityProbe durabilityProbe = new RunwayDurabilityProbe(3000.0, rng);

    List<Probe> probes =
        List.of(
            arrivals, departures, systemTime, maxSystemTime, minSystemTime,
            waitingTime, maxWaitingTime, minWaitingTime,
            idleTime, maxIdleTime, minIdleTime, maxQueueSize, minQueueSize, durabilityProbe);

    Simulation simulation =
        new Simulation(0.0, SIM_LENGTH_4_SEMANAS, model, rng, arribosGen, strategies, probes, false);

    simulation.run();

    int totalArribos = arrivals.count();
    int totalAterrizados = departures.count();
    double tTransitoMedio = totalAterrizados > 0 ? systemTime.totalSystemTime() / totalAterrizados : 0.0;
    double tEsperaMedio = totalArribos > 0 ? waitingTime.totalWaitingTime() / totalArribos : 0.0;
    double maxEspera = maxWaitingTime.maxWaitingTime();
    double minEspera = minWaitingTime.minWaitingTime();
    double maxTransito = maxSystemTime.maxSystemTime();
    double minTransito = minSystemTime.minSystemTime();

    double totalOcioSistema = 0.0;
    double maxOcioGlobal = 0.0;
    double minOcioGlobal = Double.POSITIVE_INFINITY;

    for (int i = 1; i <= cantidadPistas; i++) {
      totalOcioSistema += idleTime.totalIdleTime(i);
      double mo = maxIdleTime.maxIdleTime(i);
      if (mo > maxOcioGlobal) maxOcioGlobal = mo;
      double minO = minIdleTime.minIdleTime(i);
      if (minO > 0.0 && minO < minOcioGlobal) minOcioGlobal = minO;
    }
    if (Double.isInfinite(minOcioGlobal)) minOcioGlobal = 0.0;
    double proporcionOcioTotal = (totalOcioSistema / (SIM_LENGTH_4_SEMANAS * cantidadPistas)) * 100.0;

    int maxColaSistema = 0;
    int minColaSistemaNoCero = Integer.MAX_VALUE;
    for (int i = 1; i <= cantidadPistas; i++) {
      int mq = 0;
      try {
        mq = maxQueueSize.maxQueueSize(i);
      } catch (IllegalArgumentException e) {
        mq = 0;
      }
      if (mq > maxColaSistema) maxColaSistema = mq;
      int minQ = minQueueSize.minQueueSize(i);
      if (minQ > 0 && minQ < minColaSistemaNoCero) minColaSistemaNoCero = minQ;
    }
    if (minColaSistemaNoCero == Integer.MAX_VALUE) minColaSistemaNoCero = 0;

    if (imprimirReporte) {
      System.out.println("=================================================================");
      System.out.printf("   ETAPA 2: CONFIGURACIÓN CON %d PISTAS (4 SEMANAS)%n", cantidadPistas);
      System.out.println("=================================================================");
      System.out.printf("1. Aeronaves que arribaron:                 %d%n", totalArribos);
      System.out.printf("2. Aeronaves que aterrizaron:                %d%n", totalAterrizados);
      System.out.println("3. Tiempo de tránsito en el sistema (minutos):");
      System.out.printf("   - Medio:   %.3f | Máximo: %.3f | Mínimo (>0): %.3f%n",
          tTransitoMedio, maxTransito, minTransito);
      System.out.println("4. Tiempo de espera en cola (minutos):");
      System.out.printf("   - Medio:   %.3f | Máximo: %.3f | Mínimo (>0): %.3f%n",
          tEsperaMedio, maxEspera, minEspera);
      System.out.printf("   - Evaluación restricción 2 hs (120 min):    %s%n",
          (maxEspera <= 120.0 ? "CUMPLE RESTRICCIÓN" : "SUPERA 2 HORAS!"));
      System.out.println("5. Tiempo de ocio de las pistas:");
      System.out.printf("   - Total global: %.3f min (%.2f %% del tiempo total)%n",
          totalOcioSistema, proporcionOcioTotal);
      for (int i = 1; i <= cantidadPistas; i++) {
        System.out.printf("   - Pista %d: Ocio = %.1f min (%.1f%%) | Máx continuo = %.1f min | Mín (>0) = %.1f min%n",
            i, idleTime.totalIdleTime(i),
            (idleTime.totalIdleTime(i) / SIM_LENGTH_4_SEMANAS) * 100.0,
            maxIdleTime.maxIdleTime(i), minIdleTime.minIdleTime(i));
      }
      System.out.println("6. Tamaño de colas de espera:");
      System.out.printf("   - Máximo global: %d | Mínimo (>0): %d aeronave(s)%n",
          maxColaSistema, minColaSistemaNoCero);
      System.out.println("7. Durabilidad final de suelo por pista (inicial = 3000):");
      for (int i = 1; i <= cantidadPistas; i++) {
        System.out.printf("   - Pista %d: %.2f unidades restantes%n", i, durabilityProbe.durabilidad(i));
      }
      System.out.println("=================================================================\n");
    }

    return new ResultadoCorrida(
        totalArribos,
        totalAterrizados,
        tTransitoMedio,
        maxTransito,
        minTransito,
        tEsperaMedio,
        maxEspera,
        minEspera,
        totalOcioSistema,
        proporcionOcioTotal,
        maxOcioGlobal,
        minOcioGlobal,
        maxColaSistema,
        minColaSistemaNoCero,
        durabilityProbe.durabilidades());
  }

  // =================================================================
  // ETAPA 3
  // =================================================================

  public static void ejecutarEtapa3(int cantidadPistas, int totalReplicas) {
    System.out.println("=================================================================");
    System.out.printf("   ETAPA 3: EXPERIMENTACIÓN CON %d RÉPLICAS (95%% DE CERTEZA)%n", totalReplicas);
    System.out.printf("   Configuración evaluada: %d pistas durante 4 semanas c/u%n", cantidadPistas);
    System.out.println("=================================================================");

    List<ResultadoCorrida> resultados = new ArrayList<>();
    RandomGenerator masterRng = RandomGenerator.of("L64X128MixRandom");

    for (int r = 1; r <= totalReplicas; r++) {
      RandomGenerator replicaRng = RandomGenerator.of("L64X128MixRandom");
      replicaRng.nextLong(); // avanzar estado
      ResultadoCorrida res = ejecutarSimulacionEtapa2(cantidadPistas, replicaRng, false);
      resultados.add(res);
    }

    // Para N = 50, valor crítico t de Student (grados de libertad 49, alpha = 0.05 bilateral) ≈ 2.009575
    double tStudent = 2.009575;

    System.out.println("\n----------------- ESTIMACIÓN POR INTERVALOS DE CONFIANZA (95%) -----------------");
    mostrarIntervalo("Total aeronaves arribadas",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.totalArribos).toArray(), tStudent), "");
    mostrarIntervalo("Total aeronaves aterrizadas",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.totalAterrizados).toArray(), tStudent), "");

    mostrarIntervalo("Tiempo medio de tránsito",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.tiempoTransitoMedio).toArray(), tStudent), " min");
    mostrarIntervalo("Tiempo máximo de tránsito",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.maxTransito).toArray(), tStudent), " min");
    mostrarIntervalo("Tiempo mínimo de tránsito (>0)",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.minTransito).toArray(), tStudent), " min");

    mostrarIntervalo("Tiempo medio de espera",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.tiempoEsperaMedio).toArray(), tStudent), " min");
    mostrarIntervalo("Tiempo máximo de espera",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.maxEspera).toArray(), tStudent), " min");
    mostrarIntervalo("Tiempo mínimo de espera (>0)",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.minEspera).toArray(), tStudent), " min");

    mostrarIntervalo("Tiempo total de ocio global",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.totalOcio).toArray(), tStudent), " min");
    mostrarIntervalo("Proporción de ocio sobre tiempo total",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.proporcionOcio).toArray(), tStudent), " %");
    mostrarIntervalo("Máximo período continuo de ocio",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.maxOcioGlobal).toArray(), tStudent), " min");
    mostrarIntervalo("Mínimo período de ocio (>0)",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.minOcioGlobal).toArray(), tStudent), " min");

    mostrarIntervalo("Tamaño máximo de cola",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.maxCola).toArray(), tStudent), " aeronaves");
    mostrarIntervalo("Tamaño mínimo de cola (>0)",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.minColaNoCero).toArray(), tStudent), " aeronaves");

    System.out.println("--------------------------------------------------------------------------------");
    System.out.println("Durabilidad de suelo final por pista (IC 95%):");
    for (int p = 1; p <= cantidadPistas; p++) {
      final int pistaIndex = p;
      double[] durabilidadesPista =
          resultados.stream().mapToDouble(r -> r.durabilidadFinal.getOrDefault(pistaIndex, 3000.0)).toArray();
      mostrarIntervalo(String.format("Pista %d", p),
          calcularEstadisticas(durabilidadesPista, tStudent), " unidades");
    }
    System.out.println("================================================================================\n");
  }

  private static Estadisticas calcularEstadisticas(double[] muestra, double tCritico) {
    int n = muestra.length;
    double suma = 0.0;
    for (double v : muestra) suma += v;
    double media = suma / n;

    double sumaCuad = 0.0;
    for (double v : muestra) {
      double diff = v - media;
      sumaCuad += diff * diff;
    }
    double varianza = sumaCuad / (n - 1);
    double desvio = Math.sqrt(varianza);
    double errorEstandar = desvio / Math.sqrt(n);
    double margenError = tCritico * errorEstandar;

    return new Estadisticas(media, desvio, media - margenError, media + margenError);
  }

  private static void mostrarIntervalo(String nombre, Estadisticas est, String unidad) {
    System.out.printf("   * %-34s: Media = %8.3f%s | IC 95%% = [%8.3f, %8.3f]%s (s = %6.3f)%n",
        nombre, est.media, unidad, est.limiteInferior, est.limiteSuperior, unidad, est.desvio);
  }

  public record Estadisticas(
      double media,
      double desvio,
      double limiteInferior,
      double limiteSuperior) {}

  public record ResultadoCorrida(
      int totalArribos,
      int totalAterrizados,
      double tiempoTransitoMedio,
      double maxTransito,
      double minTransito,
      double tiempoEsperaMedio,
      double maxEspera,
      double minEspera,
      double totalOcio,
      double proporcionOcio,
      double maxOcioGlobal,
      double minOcioGlobal,
      int maxCola,
      int minColaNoCero,
      Map<Integer, Double> durabilidadFinal) {}
}
