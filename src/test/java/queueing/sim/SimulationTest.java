package queueing.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.Test;
import queueing.sim.strategies.ServerSelectionStrategy;
import queueing.sim.strategies.StrategyBag;

/** Unit tests for {@link Simulation}. */
class SimulationTest {

  @Test
  void simulationShouldExecuteEventsInChronologicalOrder() {
    List<String> executionOrder = new ArrayList<>();

    Event eventA = new TestEvent(10.0, 0, context -> executionOrder.add("A"));
    Event eventB = new TestEvent(5.0, 0, context -> executionOrder.add("B"));
    Event eventC = new TestEvent(20.0, 0, context -> executionOrder.add("C"));

    Simulation simulation = createSimulation(0.0, 100.0, 30.0);

    simulation.schedule(eventA);
    simulation.schedule(eventB);
    simulation.schedule(eventC);

    simulation.run();

    assertEquals(List.of("B", "A", "C"), executionOrder);
  }

  @Test
  void simulationShouldExecuteEventsWithSameClockAccordingToPriority() {
    List<String> executionOrder = new ArrayList<>();

    Event lowPriority = new TestEvent(10.0, 2, context -> executionOrder.add("low"));
    Event highPriority = new TestEvent(10.0, 0, context -> executionOrder.add("high"));
    Event mediumPriority = new TestEvent(10.0, 1, context -> executionOrder.add("medium"));

    Simulation simulation = createSimulation(0.0, 100.0, 30.0);

    simulation.schedule(lowPriority);
    simulation.schedule(highPriority);
    simulation.schedule(mediumPriority);

    simulation.run();

    assertEquals(List.of("high", "medium", "low"), executionOrder);
  }

  @Test
  void simulationShouldNotExecuteEventsAfterSimulationEnd() {
    List<String> executedEvents = new ArrayList<>();

    Event beforeEnd = new TestEvent(29.0, 0, context -> executedEvents.add("before"));
    Event atEnd = new TestEvent(30.0, 0, context -> executedEvents.add("at"));
    Event afterEnd = new TestEvent(31.0, 0, context -> executedEvents.add("after"));

    Simulation simulation = createSimulation(10.0, 20.0, 40.0);

    simulation.schedule(beforeEnd);
    simulation.schedule(atEnd);
    simulation.schedule(afterEnd);

    simulation.run();

    assertEquals(List.of("before", "at"), executedEvents);
  }

  @Test
  void simulationShouldFinishWhenFutureEventListBecomesEmpty() {
    List<String> executedEvents = new ArrayList<>();

    Event event = new TestEvent(10.0, 0, context -> executedEvents.add("event"));

    Simulation simulation = createSimulation(0.0, 100.0, 101.0);

    simulation.schedule(event);

    simulation.run();

    assertEquals(List.of("event"), executedEvents);
  }

  @Test
  void simulationShouldExecuteEventsScheduledByOtherEvents() {
    List<String> executionOrder = new ArrayList<>();

    Event secondEvent = new TestEvent(20.0, 0, context -> executionOrder.add("second"));

    Event firstEvent =
        new TestEvent(
            10.0,
            0,
            context -> {
              executionOrder.add("first");
              context.schedule(secondEvent);
            });

    Simulation simulation = createSimulation(0.0, 100.0, 101.0);

    simulation.schedule(firstEvent);

    simulation.run();

    assertEquals(List.of("first", "second"), executionOrder);
  }

  private Simulation createSimulation(double initClock, double simLength, double arrivalInterval) {

    return new Simulation(
        initClock,
        simLength,
        model(),
        RandomGenerator.getDefault(),
        rg -> arrivalInterval,
        createStrategyBag(),
        List.of(),
        false);
  }

  private QueueingModel model() {
    return new QueueingModel(List.of(new QueueConfig(1, List.of(new ServerConfig(1, rg -> 5.0)))));
  }

  private StrategyBag createStrategyBag() {
    return new StrategyBag(
        firstAvailableServer(),
        availableQueues -> availableQueues.getFirst().id(),
        availableQueues -> availableQueues.getFirst().id());
  }

  private ServerSelectionStrategy firstAvailableServer() {
    return availableServers -> availableServers.getFirst().id();
  }

  /** Functional interface representing an action performed by a test event. */
  @FunctionalInterface
  private interface EventAction {

    void execute(SimulationContext context);
  }

  /** Simple event implementation used exclusively for testing {@link Simulation}. */
  private static final class TestEvent implements Event {

    private final double clock;
    private final int priority;
    private final EventAction action;

    private TestEvent(double clock, int priority, EventAction action) {
      this.clock = clock;
      this.priority = priority;
      this.action = action;
    }

    @Override
    public double clock() {
      return clock;
    }

    @Override
    public int priority() {
      return priority;
    }

    @Override
    public void execute(SimulationContext context) {
      action.execute(context);
    }
  }
}
