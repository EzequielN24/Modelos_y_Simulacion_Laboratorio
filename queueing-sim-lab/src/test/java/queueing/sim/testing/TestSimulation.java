package queueing.sim.testing;

import java.util.List;
import java.util.random.RandomGenerator;
import queueing.sim.QueueingModel;
import queueing.sim.Simulation;
import queueing.sim.TimeGenerator;
import queueing.sim.probes.Probe;
import queueing.sim.strategies.StrategyBag;

/**
 * Factory for creating {@link Simulation} instances for testing.
 *
 * <p>This class provides factory methods for creating simulations with either a default test
 * configuration or a fully specified configuration.
 */
public final class TestSimulation {

  private TestSimulation() {}

  /**
   * Creates a simulation using the default test configuration.
   *
   * <p>The default configuration uses an initial clock of {@code 0.0}, a simulation length of
   * {@code 100.0}, the default random generator, and the default test strategy bag. The queueing
   * model, inter-arrival time generator, and probes are supplied by the caller.
   *
   * @param model the queueing model to simulate
   * @param interArrivalTimeGenerator the generator used to determine the time between consecutive
   *     arrivals
   * @param probes the probes used to collect simulation measurements
   * @return a simulation configured with the default test settings
   */
  public static Simulation create(
      QueueingModel model, TimeGenerator interArrivalTimeGenerator, List<Probe> probes) {

    return new Simulation(
        0.0,
        100.0,
        model,
        RandomGenerator.getDefault(),
        interArrivalTimeGenerator,
        TestStrategies.defaultStrategyBag(),
        probes,
        false);
  }

  /**
   * Creates a simulation using the specified configuration.
   *
   * @param initClock the initial value of the simulation clock
   * @param simLength the length of the simulation
   * @param model the queueing model to simulate
   * @param randomGenerator the random generator used by the simulation
   * @param interArrivalTimeGenerator the generator used to determine the time between consecutive
   *     arrivals
   * @param strategyBag the strategies used by the simulation
   * @param probes the probes used to collect simulation measurements
   * @return a simulation configured with the specified parameters
   */
  public static Simulation create(
      double initClock,
      double simLength,
      QueueingModel model,
      RandomGenerator randomGenerator,
      TimeGenerator interArrivalTimeGenerator,
      StrategyBag strategyBag,
      List<Probe> probes) {

    return new Simulation(
        initClock,
        simLength,
        model,
        randomGenerator,
        interArrivalTimeGenerator,
        strategyBag,
        probes,
        false);
  }
}
