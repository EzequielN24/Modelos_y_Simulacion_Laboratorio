package queueing.sim;

import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;
import queueing.sim.probes.Probe;
import queueing.sim.strategies.StrategyBag;

/**
 * Implementación interna de {@link SimulationContext}.
 *
 * <p>Esta clase proporciona a los eventos acceso controlado al estado de la simulación manteniendo
 * los detalles de implementación internos ocultos de la API pública.
 */
final class InternalSimulationContext implements SimulationContext {

  /** La lista de eventos futuros utilizada por la simulación. */
  private final FutureEventList futureEventList;

  /** El generador de números aleatorios utilizado por la simulación. */
  private final RandomGenerator randomGenerator;

  /** El modelo de colas que se está simulando. */
  private final QueueingModel model;

  /** La bolsa de estrategias que contiene las estrategias utilizadas por la simulación. */
  private final StrategyBag strategyBag;

  /** La lista de sondas para observar la simulación. */
  private final List<Probe> probes;

  /**
   * Crea un contexto de simulación interno.
   *
   * @param futureEventList la lista de eventos futuros utilizada por la simulación
   * @param randomGenerator el generador de números aleatorios utilizado por la simulación
   * @param model el modelo de colas que se está simulando
   * @param strategyBag la bolsa de estrategias que contiene las estrategias utilizadas por la
   *     simulación
   * @param probes la lista de sondas para observar la simulación
   * @throws NullPointerException si algún argumento es nulo
   */
  InternalSimulationContext(
      FutureEventList futureEventList,
      RandomGenerator randomGenerator,
      QueueingModel model,
      StrategyBag strategyBag,
      List<Probe> probes) {

    this.futureEventList = Objects.requireNonNull(futureEventList);
    this.randomGenerator = Objects.requireNonNull(randomGenerator);
    this.model = Objects.requireNonNull(model);
    this.strategyBag = Objects.requireNonNull(strategyBag);
    this.probes = Objects.requireNonNull(probes);
  }

  StrategyBag strategyBag() {
    return this.strategyBag;
  }

  List<Probe> probes() {
    return this.probes;
  }

  @Override
  public RandomGenerator randomGenerator() {
    return this.randomGenerator;
  }

  @Override
  public QueueingModel model() {
    return this.model;
  }

  @Override
  public void schedule(Event event) {
    this.futureEventList.insert(event);
  }
}
