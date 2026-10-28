package queueing.sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.random.RandomGenerator;
import queueing.sim.generators.ArribosVuelosHorarioPicoGenerator;
import queueing.sim.probes.*;
import queueing.sim.strategies.FirstAvailable;
import queueing.sim.strategies.ShortestQueue;
import queueing.sim.strategies.StrategyBag;
import random.variates.Discrete;
import random.variates.Uniform;

/**
 * 
 * <p>
 * Opciones del menu:
 * 1. Ejecucion de vuelos de cabotaje (1 pista, 1 cola, 4 semanas).
 * 2. Probar con 2, 3, 4 y 5 pistas.
 * 3. Ejecutar 50 replicas con intervalos de confianza del 95%.
 */
public final class Main {

  private static final double SIM_LENGTH_4_SEMANAS = 4.0 * 7.0 * 24.0 * 60.0; // 40.320 minutos

  private Main() {
  }

  public static void main(String[] args) {
    if (args.length > 0) {
      ejecutarPorParametro(args[0]);
      return;
    }

    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.println("=================================================================");
      System.out.println("     SIMULADOR DE EVENTOS DISCRETOS - MODELOS Y SIMULACION       ");
      System.out.println("=================================================================");
      System.out.println("Seleccione una opcion:");
      System.out.println("  1. Ejecucion de cabotaje");
      System.out.println("  2. Probar con 2, 3, 4 y 5 pistas");
      System.out.println("  3. 50 replicas");
      System.out.println("  0. Salir");
      System.out.print("Opcion: ");

      String entrada = scanner.nextLine().trim();
      System.out.println();

      switch (entrada) {
        case "1" -> ejecutarCabotaje();
        case "2" -> {
          for (int pistas = 2; pistas <= 5; pistas++) {
            ejecutarMultiservidor(pistas, true);
          }
        }
        case "3" -> ejecutarReplicas(4, 50);
        case "0" -> {
          System.out.println("Finalizando simulador.");
          return;
        }
        default -> System.out.println("Opcion no valida. Intente nuevamente.\n");
      }
    }
  }

  private static void ejecutarPorParametro(String param) {
    switch (param.toLowerCase()) {
      case "cabotaje", "1" -> ejecutarCabotaje();
      case "pistas", "2" -> {
        for (int pistas = 2; pistas <= 5; pistas++) {
          ejecutarMultiservidor(pistas, true);
        }
      }
      case "replicas", "3" -> ejecutarReplicas(4, 50);
      case "all", "todo" -> {
        ejecutarCabotaje();
        for (int pistas = 2; pistas <= 5; pistas++) {
          ejecutarMultiservidor(pistas, true);
        }
        ejecutarReplicas(4, 50);
      }
      default -> {
        try {
          int n = Integer.parseInt(param);
          ejecutarMultiservidor(n, true);
        } catch (NumberFormatException e) {
          System.out.println("Parametro desconocido: " + param);
        }
      }
    }
  }

  // =================================================================
  // 1. CABOTAJE (1 PISTA, 1 COLA)
  // =================================================================

  public static void ejecutarCabotaje() {
    System.out.println("=================================================================");
    System.out.println("   AEROPUERTO DE VUELOS DE CABOTAJE (1 PISTA, 1 COLA)            ");
    System.out.println("=================================================================");
    System.out.printf("Duracion de simulacion: %.0f minutos (4 semanas)%n%n", SIM_LENGTH_4_SEMANAS);

    Map<Double, Double> tablaArribos = Map.of(10.0, 0.35, 15.0, 0.45, 17.0, 0.20);
    TimeGenerator arribosGen = rng -> new Discrete<>(tablaArribos, rng).sample();

    Map<Double, Double> tablaServicio = Map.of(8.0, 0.38, 10.0, 0.32, 13.0, 0.10, 15.0, 0.20);
    TimeGenerator aterrizajeGen = rng -> new Discrete<>(tablaServicio, rng).sample();

    int pistaId = 1;
    int colaId = 1;

    QueueConfig queueConfig = new QueueConfig(colaId, List.of(new ServerConfig(pistaId, aterrizajeGen)));
    QueueingModel model = new QueueingModel(List.of(queueConfig));

    StrategyBag strategies = new StrategyBag(
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

    List<Probe> probes = List.of(
        arrivals, departures, systemTime, maxSystemTime, minSystemTime,
        waitingTime, maxWaitingTime, minWaitingTime,
        idleTime, maxIdleTime, minIdleTime, maxQueueSize, minQueueSize);

    RandomGenerator rng = RandomGenerator.of("L64X128MixRandom");
    Simulation simulation = new Simulation(0.0, SIM_LENGTH_4_SEMANAS, model, rng, arribosGen, strategies, probes,
        false);

    simulation.run();

    int totalArribos = arrivals.count();
    int totalAterrizados = departures.count();
    double tTransitoMedio = totalAterrizados > 0 ? systemTime.totalSystemTime() / totalAterrizados : 0.0;
    double tEsperaMedio = totalArribos > 0 ? waitingTime.totalWaitingTime() / totalArribos : 0.0;
    double totalOcio = idleTime.totalIdleTime(pistaId);
    double proporcionOcio = (totalOcio / SIM_LENGTH_4_SEMANAS) * 100.0;

    System.out.println("---------------------- RESULTADOS CABOTAJE ----------------------");
    System.out.printf("1. Aeronaves que arribaron:                 %d%n", totalArribos);
    System.out.printf("2. Aeronaves que aterrizaron:                %d%n", totalAterrizados);
    System.out.println("3. Tiempo de transito en el sistema (minutos):");
    System.out.printf("   - Medio:   %.3f | Maximo: %.3f | Minimo (>0): %.3f%n",
        tTransitoMedio, maxSystemTime.maxSystemTime(), minSystemTime.minSystemTime());
    System.out.println("4. Tiempo de espera en cola (minutos):");
    System.out.printf("   - Medio:   %.3f | Maximo: %.3f | Minimo (>0): %.3f%n",
        tEsperaMedio, maxWaitingTime.maxWaitingTime(), minWaitingTime.minWaitingTime());
    System.out.println("5. Tiempo de ocio de la pista:");
    System.out.printf("   - Total:   %.3f min (%.2f %% del tiempo total)%n", totalOcio, proporcionOcio);
    System.out.printf("   - Maximo periodo continuo: %.3f min | Minimo (>0): %.3f min%n",
        maxIdleTime.maxIdleTime(pistaId), minIdleTime.minIdleTime(pistaId));
    System.out.println("6. Tamano de cola de espera:");
    System.out.printf("   - Maximo:  %d | Minimo (>0): %d aeronave(s)%n",
        maxQueueSize.maxQueueSize(colaId), minQueueSize.minQueueSize(colaId));
    System.out.println("=================================================================\n");
  }

  // =================================================================
  // 2. MULTISERVIDOR (2, 3, 4, 5 PISTAS)
  // =================================================================

  public static ResultadoCorrida ejecutarMultiservidor(int cantidadPistas, boolean imprimirReporte) {
    RandomGenerator rng = RandomGenerator.of("L64X128MixRandom");
    return ejecutarSimulacionMultiservidor(cantidadPistas, rng, imprimirReporte);
  }

  public static ResultadoCorrida ejecutarSimulacionMultiservidor(
      int cantidadPistas, RandomGenerator rng, boolean imprimirReporte) {

    ArribosVuelosHorarioPicoGenerator arribosGen = new ArribosVuelosHorarioPicoGenerator();
    TimeGenerator servicioGen = r -> new Uniform(10.0, 25.0, r).sample();

    List<QueueConfig> queueConfigs = new ArrayList<>();
    for (int i = 1; i <= cantidadPistas; i++) {
      queueConfigs.add(new QueueConfig(i, List.of(new ServerConfig(i, servicioGen))));
    }
    QueueingModel model = new QueueingModel(queueConfigs);

    StrategyBag strategies = new StrategyBag(
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

    List<Probe> probes = List.of(
        arrivals, departures, systemTime, maxSystemTime, minSystemTime,
        waitingTime, maxWaitingTime, minWaitingTime,
        idleTime, maxIdleTime, minIdleTime, maxQueueSize, minQueueSize, durabilityProbe);

    Simulation simulation = new Simulation(0.0, SIM_LENGTH_4_SEMANAS, model, rng, arribosGen, strategies, probes,
        false);

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
      if (mo > maxOcioGlobal)
        maxOcioGlobal = mo;
      double minO = minIdleTime.minIdleTime(i);
      if (minO > 0.0 && minO < minOcioGlobal)
        minOcioGlobal = minO;
    }
    if (Double.isInfinite(minOcioGlobal))
      minOcioGlobal = 0.0;
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
      if (mq > maxColaSistema)
        maxColaSistema = mq;
      int minQ = minQueueSize.minQueueSize(i);
      if (minQ > 0 && minQ < minColaSistemaNoCero)
        minColaSistemaNoCero = minQ;
    }
    if (minColaSistemaNoCero == Integer.MAX_VALUE)
      minColaSistemaNoCero = 0;

    if (imprimirReporte) {
      System.out.println("=================================================================");
      System.out.printf("   CONFIGURACION CON %d PISTAS (4 SEMANAS)%n", cantidadPistas);
      System.out.println("=================================================================");
      System.out.printf("1. Aeronaves que arribaron:                 %d%n", totalArribos);
      System.out.printf("2. Aeronaves que aterrizaron:                %d%n", totalAterrizados);
      System.out.println("3. Tiempo de transito en el sistema (minutos):");
      System.out.printf("   - Medio:   %.3f | Maximo: %.3f | Minimo (>0): %.3f%n",
          tTransitoMedio, maxTransito, minTransito);
      System.out.println("4. Tiempo de espera en cola (minutos):");
      System.out.printf("   - Medio:   %.3f | Maximo: %.3f | Minimo (>0): %.3f%n",
          tEsperaMedio, maxEspera, minEspera);
      System.out.printf("   - Evaluacion restriccion 2 hs (120 min):    %s%n",
          (maxEspera <= 120.0 ? "CUMPLE RESTRICCION" : "SUPERA 2 HORAS!"));
      System.out.println("5. Tiempo de ocio de las pistas:");
      System.out.printf("   - Total global: %.3f min (%.2f %% del tiempo total)%n",
          totalOcioSistema, proporcionOcioTotal);
      for (int i = 1; i <= cantidadPistas; i++) {
        System.out.printf("   - Pista %d: Ocio = %.1f min (%.1f%%) | Max continuo = %.1f min | Min (>0) = %.1f min%n",
            i, idleTime.totalIdleTime(i),
            (idleTime.totalIdleTime(i) / SIM_LENGTH_4_SEMANAS) * 100.0,
            maxIdleTime.maxIdleTime(i), minIdleTime.minIdleTime(i));
      }
      System.out.println("6. Tamano de colas de espera:");
      System.out.printf("   - Maximo global: %d | Minimo (>0): %d aeronave(s)%n",
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
  // 3. EXPERIMENTACION CON 50 REPLICAS
  // =================================================================

  public static void ejecutarReplicas(int cantidadPistas, int totalReplicas) {
    System.out.println("=================================================================");
    System.out.printf("   EXPERIMENTACION CON %d REPLICAS (95%% DE CERTEZA)%n", totalReplicas);
    System.out.printf("   Configuracion evaluada: %d pistas durante 4 semanas c/u%n", cantidadPistas);
    System.out.println("=================================================================");

    List<ResultadoCorrida> resultados = new ArrayList<>();

    for (int r = 1; r <= totalReplicas; r++) {
      RandomGenerator replicaRng = RandomGenerator.of("L64X128MixRandom");
      replicaRng.nextLong(); // avanzar estado
      ResultadoCorrida res = ejecutarSimulacionMultiservidor(cantidadPistas, replicaRng, false);
      resultados.add(res);
    }

    double zCritico = 1.96;

    System.out.println("\n----------------- ESTIMACION POR INTERVALOS DE CONFIANZA (95%) -----------------");
    mostrarIntervalo("Total aeronaves arribadas",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.totalArribos).toArray(), zCritico), "");
    mostrarIntervalo("Total aeronaves aterrizadas",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.totalAterrizados).toArray(), zCritico), "");

    mostrarIntervalo("Tiempo medio de transito",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.tiempoTransitoMedio).toArray(), zCritico), " min");
    mostrarIntervalo("Tiempo maximo de transito",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.maxTransito).toArray(), zCritico), " min");
    mostrarIntervalo("Tiempo minimo de transito (>0)",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.minTransito).toArray(), zCritico), " min");

    mostrarIntervalo("Tiempo medio de espera",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.tiempoEsperaMedio).toArray(), zCritico), " min");
    mostrarIntervalo("Tiempo maximo de espera",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.maxEspera).toArray(), zCritico), " min");
    mostrarIntervalo("Tiempo minimo de espera (>0)",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.minEspera).toArray(), zCritico), " min");

    mostrarIntervalo("Tiempo total de ocio global",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.totalOcio).toArray(), zCritico), " min");
    mostrarIntervalo("Proporcion de ocio sobre tiempo total",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.proporcionOcio).toArray(), zCritico), " %");
    mostrarIntervalo("Maximo periodo continuo de ocio",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.maxOcioGlobal).toArray(), zCritico), " min");
    mostrarIntervalo("Minimo periodo de ocio (>0)",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.minOcioGlobal).toArray(), zCritico), " min");

    mostrarIntervalo("Tamano maximo de cola",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.maxCola).toArray(), zCritico), " aeronaves");
    mostrarIntervalo("Tamano minimo de cola (>0)",
        calcularEstadisticas(resultados.stream().mapToDouble(r -> r.minColaNoCero).toArray(), zCritico), " aeronaves");

    System.out.println("--------------------------------------------------------------------------------");
    System.out.println("Durabilidad de suelo final por pista (IC 95%):");
    for (int p = 1; p <= cantidadPistas; p++) {
      final int pistaIndex = p;
      double[] durabilidadesPista = resultados.stream()
          .mapToDouble(r -> r.durabilidadFinal.getOrDefault(pistaIndex, 3000.0)).toArray();
      mostrarIntervalo(String.format("Pista %d", p),
          calcularEstadisticas(durabilidadesPista, zCritico), " unidades");
    }
    System.out.println("================================================================================\n");
  }

  // Calculo de estimacion puntual (media de medias X_doble_barra) e intervalo de
  // confianza
  // segun formula de diapositiva 26: [ X_doble_barra - z * (S / raiz(n)),
  // X_doble_barra + z * (S / raiz(n)) ]
  private static Estadisticas calcularEstadisticas(double[] muestra, double z) {
    int n = muestra.length;
    double suma = 0.0;
    for (double v : muestra)
      suma += v;
    double mediaGlobal = suma / n; // X con doble barra (estimacion puntual)

    double sumaCuad = 0.0;
    for (double v : muestra) {
      double diff = v - mediaGlobal;
      sumaCuad += diff * diff;
    }
    double varianza = sumaCuad / (n - 1); // S^2
    double s = Math.sqrt(varianza); // S (desvio estandar muestral que estima a sigma)
    double errorEstandar = s / Math.sqrt(n); // S / raiz(n)
    double margenError = z * errorEstandar; // H = z_(alpha/2) * (S / raiz(n))

    return new Estadisticas(mediaGlobal, s, mediaGlobal - margenError, mediaGlobal + margenError);
  }

  private static void mostrarIntervalo(String nombre, Estadisticas est, String unidad) {
    System.out.printf("   * %-34s: Media = %8.3f%s | IC 95%% = [%8.3f, %8.3f]%s (s = %6.3f)%n",
        nombre, est.media, unidad, est.limiteInferior, est.limiteSuperior, unidad, est.desvio);
  }

  public record Estadisticas(
      double media,
      double desvio,
      double limiteInferior,
      double limiteSuperior) {
  }

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
      Map<Integer, Double> durabilidadFinal) {
  }
}
