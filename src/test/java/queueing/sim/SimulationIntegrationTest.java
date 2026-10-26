package queueing.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import queueing.sim.probes.ArrivalCounter;
import queueing.sim.probes.DepartureCounter;
import queueing.sim.probes.IdleTime;
import queueing.sim.probes.MaxIdleTime;
import queueing.sim.probes.MaxSystemTime;
import queueing.sim.probes.MaxWaitingTime;
import queueing.sim.probes.Probe;
import queueing.sim.probes.ServedEntitiesCounter;
import queueing.sim.probes.SystemTime;
import queueing.sim.probes.WaitingTime;
import queueing.sim.testing.OneQueueOneServer;
import queueing.sim.testing.TestSimulation;
import queueing.sim.testing.TestStrategies;

/**
 * Integration tests for {@link Simulation}.
 *
 * <p>These tests verify the behavior of a complete simulation using a D/D/1 queueing model with one
 * FIFO queue and one server.
 */
class SimulationIntegrationTest {

  private ArrivalCounter arrivalCounter;
  private ServedEntitiesCounter servedEntitiesCounter;
  private DepartureCounter departureCounter;
  private WaitingTime waitingTime;
  private IdleTime idleTime;
  private MaxWaitingTime maxWaitingTime;
  private MaxIdleTime maxIdleTime;
  private SystemTime systemTime;
  private MaxSystemTime maxSystemTime;

  private List<Probe> probes;

  private final double initClock = 0;
  private final double simLenght = 100;
  private final TimeGenerator interArrivalTimeGenerator = rg -> 10d;

  @BeforeEach
  void setUp() {
    arrivalCounter = new ArrivalCounter();
    servedEntitiesCounter = new ServedEntitiesCounter();
    departureCounter = new DepartureCounter();
    waitingTime = new WaitingTime();
    idleTime = new IdleTime();
    maxWaitingTime = new MaxWaitingTime();
    maxIdleTime = new MaxIdleTime();
    systemTime = new SystemTime();
    maxSystemTime = new MaxSystemTime();

    probes =
        List.of(
            arrivalCounter,
            servedEntitiesCounter,
            departureCounter,
            waitingTime,
            idleTime,
            maxWaitingTime,
            maxIdleTime,
            systemTime,
            maxSystemTime);
  }

  @Test
  @DisplayName("D/D/1 — arrival = 10, service = 9 — FIFO")
  void serviceFasterThanArrivals() {
    Simulation simulation =
        TestSimulation.create(
            initClock,
            simLenght,
            OneQueueOneServer.create(rg -> 9d),
            RandomGenerator.getDefault(),
            interArrivalTimeGenerator,
            TestStrategies.defaultStrategyBag(),
            probes);

    simulation.run();

    assertEquals(11, arrivalCounter.count());
    assertEquals(11, servedEntitiesCounter.count());
    assertEquals(10, departureCounter.count());

    assertEquals(0.0, waitingTime.totalWaitingTime());
    assertEquals(0.0, maxWaitingTime.maxWaitingTime());

    assertEquals(10.0, idleTime.totalIdleTime(1));
    assertEquals(1.0, maxIdleTime.maxIdleTime(1));

    assertEquals(90.0, systemTime.totalSystemTime());
    assertEquals(9.0, maxSystemTime.maxSystemTime());
  }

  @Test
  @DisplayName("D/D/1 — arrival = 10, service = 10 — FIFO")
  void serviceMatchesArrivals() {
    Simulation simulation =
        TestSimulation.create(
            initClock,
            simLenght,
            OneQueueOneServer.create(rg -> 10d),
            RandomGenerator.getDefault(),
            interArrivalTimeGenerator,
            TestStrategies.defaultStrategyBag(),
            probes);

    simulation.run();

    assertEquals(11, arrivalCounter.count());
    assertEquals(11, servedEntitiesCounter.count());
    assertEquals(10, departureCounter.count());

    assertEquals(0.0, waitingTime.totalWaitingTime());
    assertEquals(0.0, maxWaitingTime.maxWaitingTime());

    assertEquals(0.0, idleTime.totalIdleTime(1));
    assertEquals(0.0, maxIdleTime.maxIdleTime(1));

    assertEquals(100.0, systemTime.totalSystemTime());
    assertEquals(10.0, maxSystemTime.maxSystemTime());
  }

  @Test
  @DisplayName("D/D/1 — arrival = 10, service = 11 — FIFO")
  void serviceSlowerThanArrivals() {
    Simulation simulation =
        TestSimulation.create(
            initClock,
            simLenght,
            OneQueueOneServer.create(rg -> 11d),
            RandomGenerator.getDefault(),
            interArrivalTimeGenerator,
            TestStrategies.defaultStrategyBag(),
            probes);

    simulation.run();

    assertEquals(11, arrivalCounter.count());
    assertEquals(10, servedEntitiesCounter.count());
    assertEquals(9, departureCounter.count());

    assertEquals(45.0, waitingTime.totalWaitingTime());
    assertEquals(9.0, maxWaitingTime.maxWaitingTime());

    assertEquals(0.0, idleTime.totalIdleTime(1));
    assertEquals(0.0, maxIdleTime.maxIdleTime(1));

    assertEquals(135.0, systemTime.totalSystemTime());
    assertEquals(19.0, maxSystemTime.maxSystemTime());
  }
}
