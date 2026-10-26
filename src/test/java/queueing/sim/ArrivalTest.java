package queueing.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.Test;
import queueing.sim.strategies.QueueSelectionStrategy;
import queueing.sim.strategies.ServerSelectionStrategy;
import queueing.sim.strategies.StrategyBag;
import queueing.sim.testing.OneQueueOneServer;
import queueing.sim.testing.TestStrategies;
import queueing.sim.testing.TwoQueuesTwoServers;

/**
 * Unit tests for {@link Arrival}.
 *
 * <p>These tests verify the behavior of an arrival in the global queueing system, including server
 * selection, queue selection, service scheduling, and scheduling of the next arrival.
 */
class ArrivalTest {

  private final TimeGenerator serviceTimeGenerator = rg -> 5d;

  @Test
  void arrivalShouldStartServiceOnSelectedAvailableServer() {
    QueueingModel model = OneQueueOneServer.create(serviceTimeGenerator);
    Queue queue = model.queue(1);
    Server server = queue.servers().getFirst();
    Entity entity = new Entity(1);

    ServerSelectionStrategy serverSelectionStrategy =
        servers -> {
          assertEquals(
              List.of(server),
              servers.stream().map(serverView -> model.server(serverView.id())).toList());
          return server.id();
        };

    StrategyBag strategyBag =
        new StrategyBag(
            serverSelectionStrategy,
            queues -> queues.getFirst().id(),
            queues -> queues.getFirst().id());

    SimulationContext context = createContext(new FutureEventList(), model, strategyBag);

    Arrival arrival = new Arrival(10.0, entity, randomGenerator -> 3.0, () -> new Entity(2));

    arrival.execute(context);

    assertTrue(server.isBusy());
    assertSame(entity, server.entity());
    assertTrue(queue.isEmpty());
  }

  @Test
  void arrivalShouldSelectAvailableServerGlobally() {
    QueueingModel model = TwoQueuesTwoServers.create(serviceTimeGenerator, serviceTimeGenerator);
    Queue queue1 = model.queue(1);
    Queue queue2 = model.queue(2);
    Server server1 = queue1.servers().getFirst();
    Server server2 = queue2.servers().getFirst();
    Entity entity = new Entity(1);

    ServerSelectionStrategy serverSelectionStrategy =
        servers -> {
          assertEquals(
              List.of(server1, server2),
              servers.stream().map(serverView -> model.server(serverView.id())).toList());
          return server2.id();
        };

    StrategyBag strategyBag =
        new StrategyBag(
            serverSelectionStrategy,
            queues -> queues.getFirst().id(),
            queues -> queues.getFirst().id());

    SimulationContext context = createContext(new FutureEventList(), model, strategyBag);

    Arrival arrival = new Arrival(10.0, entity, randomGenerator -> 3.0, () -> new Entity(2));

    arrival.execute(context);

    assertTrue(server2.isBusy());
    assertSame(entity, server2.entity());
    assertFalse(server1.isBusy());
    assertTrue(queue1.isEmpty());
    assertTrue(queue2.isEmpty());
  }

  @Test
  void arrivalShouldAddEntityToSelectedQueueWhenNoServerIsAvailable() {
    QueueingModel model = TwoQueuesTwoServers.create(serviceTimeGenerator, serviceTimeGenerator);
    Queue queue1 = model.queue(1);
    Queue queue2 = model.queue(2);

    queue1.servers().getFirst().seize(new Entity(90));
    queue2.servers().getFirst().seize(new Entity(91));

    Entity arrivingEntity = new Entity(1);

    QueueSelectionStrategy arrivalQueueSelectionStrategy =
        queues -> {
          assertEquals(
              List.of(queue1, queue2),
              queues.stream().map(queueView -> model.queue(queueView.id())).toList());
          return 2;
        };

    StrategyBag strategyBag =
        new StrategyBag(
            servers -> servers.getFirst().id(),
            arrivalQueueSelectionStrategy,
            queues -> queues.getFirst().id());

    SimulationContext context = createContext(new FutureEventList(), model, strategyBag);

    Arrival arrival =
        new Arrival(10.0, arrivingEntity, randomGenerator -> 3.0, () -> new Entity(2));

    arrival.execute(context);

    assertEquals(0, queue1.size());
    assertEquals(1, queue2.size());
    assertSame(arrivingEntity, queue2.peek());
  }

  @Test
  void arrivalShouldScheduleDepartureWhenServiceStarts() {
    QueueingModel model = OneQueueOneServer.create(serviceTimeGenerator);
    FutureEventList futureEventList = new FutureEventList();

    SimulationContext context =
        createContext(futureEventList, model, TestStrategies.defaultStrategyBag());

    Arrival arrival = new Arrival(10.0, new Entity(1), randomGenerator -> 3.0, () -> new Entity(2));

    arrival.execute(context);

    Event firstEvent = futureEventList.imminent();
    Event secondEvent = futureEventList.imminent();

    assertTrue(firstEvent instanceof Arrival);
    assertEquals(13.0, firstEvent.clock());

    assertTrue(secondEvent instanceof Departure);
    assertEquals(15.0, secondEvent.clock());
  }

  @Test
  void arrivalShouldAlwaysScheduleNextArrival() {
    QueueingModel model = OneQueueOneServer.create(serviceTimeGenerator);
    FutureEventList futureEventList = new FutureEventList();

    SimulationContext context =
        createContext(futureEventList, model, TestStrategies.defaultStrategyBag());

    Arrival arrival = new Arrival(10.0, new Entity(1), randomGenerator -> 7.0, () -> new Entity(2));

    arrival.execute(context);

    Event departure = futureEventList.imminent();

    assertTrue(departure instanceof Departure);
    assertEquals(15.0, departure.clock());

    Event nextArrival = futureEventList.imminent();

    assertTrue(nextArrival instanceof Arrival);
    assertEquals(17.0, nextArrival.clock());
  }

  @Test
  void arrivalShouldGenerateNextEntityForNextArrival() {
    QueueingModel model = OneQueueOneServer.create(serviceTimeGenerator);
    FutureEventList futureEventList = new FutureEventList();
    Entity nextEntity = new Entity(2);

    SimulationContext context =
        createContext(futureEventList, model, TestStrategies.defaultStrategyBag());

    Arrival arrival = new Arrival(10.0, new Entity(1), randomGenerator -> 3.0, () -> nextEntity);

    arrival.execute(context);

    Event event = futureEventList.imminent();

    assertTrue(event instanceof Arrival);

    Arrival nextArrival = (Arrival) event;

    assertEquals(13.0, nextArrival.clock());
    assertEquals("Arrival[clock=13.0, entity=Entity[id=2]]", nextArrival.toString());
  }

  private SimulationContext createContext(
      FutureEventList futureEventList, QueueingModel model, StrategyBag strategyBag) {

    return new InternalSimulationContext(
        futureEventList, RandomGenerator.getDefault(), model, strategyBag, List.of());
  }
}
