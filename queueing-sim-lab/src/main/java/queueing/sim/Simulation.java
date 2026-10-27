package queueing.sim;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.random.RandomGenerator;
import queueing.sim.probes.Probe;
import queueing.sim.strategies.StrategyBag;

/**
 * Ejecuta una simulación de eventos discretos de un modelo de colas.
 *
 * <p>La simulación procesa los eventos de la lista de eventos futuros en orden cronológico hasta
 * que la simulación alcanza su tiempo de finalización configurado o ya no quedan eventos por
 * procesar.
 */
public final class Simulation {

  /** Tiempo de reloj inicial */
  private final double initClock;

  /** Duracion de la simulacion. */
  private final double simLength;

  /** El modelo de colas que se esta simulando. */
  private final QueueingModel model;

  /** Generador de numeros aleatorios. */
  private final RandomGenerator randomGenerator;

  /** Las estrategias usadas en la simulacion. */
  private final StrategyBag strategyBag;

  /** Generador de tiempos entre llegadas. */
  private final TimeGenerator interarrivalTimeGenerator;

  /** La lista de eventos futuros. */
  private final FutureEventList futureEventList;

  /** Si la simulacion espera por ENTER entre eventos. */
  private final boolean stepByStep;

  /** Lista de observadores. */
  private final List<Probe> probes;

  /**
   * Crea una simulacion.
   *
   * @param initClock el tiempo de reloj inicial
   * @param simLength la duracion de la simulacion
   * @param model el modelo de colas
   * @param randomGenerator el generador de numeros aleatorios
   * @param interarrivalTimeGenerator el generador de tiempos entre llegadas
   * @param strategyBag las estrategias usadas en la simulacion
   * @param probes lista de observadores
   * @param stepByStep if {@code simLength} is negative or not finite
   * @throws NullPointerException if {@code model}, {@code randomGenerator}, {@code
   *     interarrivalTimeGenerator}, or {@code strategyBag} is {@code null}
   * @throws IllegalArgumentException if {@code simLength} is negative or not finite
   */
  public Simulation(
      double initClock,
      double simLength,
      QueueingModel model,
      RandomGenerator randomGenerator,
      TimeGenerator interarrivalTimeGenerator,
      StrategyBag strategyBag,
      List<Probe> probes,
      boolean stepByStep) {

    if (!Double.isFinite(simLength) || simLength < 0.0) {
      throw new IllegalArgumentException("Simulation length must be finite and non-negative");
    }

    this.initClock = initClock;
    this.simLength = simLength;
    this.model = model;
    this.randomGenerator = randomGenerator;
    this.interarrivalTimeGenerator = interarrivalTimeGenerator;
    this.strategyBag = strategyBag;
    this.probes = probes;
    this.stepByStep = stepByStep;
    this.futureEventList = new FutureEventList();

    long[] nextEntityId = {1};
    EntityGenerator entityGenerator = () -> new Entity(nextEntityId[0]++);

    this.schedule(
        new Arrival(
            this.initClock,
            entityGenerator.generate(),
            this.interarrivalTimeGenerator,
            entityGenerator));
  }

  /**
   * Programa un evento para que ocurra durante la simulación.
   *
   * @param event el evento a programar
   * @throws NullPointerException if {@code event} is {@code null}
   */
  public void schedule(Event event) {
    this.futureEventList.insert(Objects.requireNonNull(event));
  }

  /** Runs the simulation. */
  public void run() {

    double endClock = this.initClock + this.simLength;

    InternalSimulationContext context =
        new InternalSimulationContext(
            this.futureEventList, this.randomGenerator, this.model, this.strategyBag, this.probes);

    Scanner scanner = this.stepByStep ? new Scanner(System.in) : null;

    for (Probe probe : this.probes) {
      probe.simulationStarted(this.initClock);
    }

    for (ServerView server : this.model.servers()) {
      for (Probe probe : this.probes) {
        probe.serverBecameIdle(this.initClock, server);
      }
    }

    while (!this.futureEventList.isEmpty() && this.futureEventList.peek().clock() <= endClock) {

      if (this.stepByStep) {
        System.out.println("Step-by-step mode: Press ENTER to inspect the imminent event...");
        scanner.nextLine();
      }

      Event event = this.futureEventList.peek();

      if (this.stepByStep) {
        System.out.println("========================================");
        System.out.println("CLOCK: " + event.clock());
        System.out.println();
        System.out.println("EVENT:");
        System.out.println("  " + event);
        System.out.println();
        System.out.println("FEL:");
        System.out.println("  " + this.futureEventList);
        System.out.println();
        System.out.println("MODEL:");
        System.out.println(this.model);
        System.out.println("========================================\n");

        System.out.println("Press ENTER to process the imminent event...");
        scanner.nextLine();
      }

      Event imminentEvent = this.futureEventList.imminent();
      imminentEvent.execute(context);

      if (this.stepByStep) {
        System.out.println("========================================");
        System.out.println();
        System.out.println("FEL:");
        System.out.println("  " + this.futureEventList);
        System.out.println();
        System.out.println("MODEL:");
        System.out.println(this.model);
        System.out.println("========================================\n");
      }
    }

    for (Probe probe : this.probes) {
      probe.simulationFinished(endClock);
    }
  }
}
