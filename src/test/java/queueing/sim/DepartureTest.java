package queueing.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.Test;
import queueing.sim.strategies.QueueSelectionStrategy;
import queueing.sim.strategies.StrategyBag;
import queueing.sim.testing.OneQueueOneServer;
import queueing.sim.testing.TestStrategies;
import queueing.sim.testing.TwoQueuesTwoServers;

/** Unit tests for {@link Departure}. */
class DepartureTest {

  private final TimeGenerator serviceTimeGenerator = rg -> 5d;

  @Test
  void departureShouldReleaseServerWhenNoEntityIsWaiting() {
    QueueingModel model = OneQueueOneServer.create(serviceTimeGenerator);
    Queue queue = model.queue(1);
    Server server = queue.servers().getFirst();
    Entity entity = new Entity(1);

    server.seize(entity);

    SimulationContext context =
        createContext(new FutureEventList(), model, TestStrategies.defaultStrategyBag());

    Departure departure = new Departure(10.0, entity, server);

    departure.execute(context);

    assertFalse(server.isBusy());
    assertEquals(0, queue.size());
  }

  @Test
  void departureShouldAssignFirstWaitingEntityToReleasedServer() {
    QueueingModel model = OneQueueOneServer.create(serviceTimeGenerator);
    Queue queue = model.queue(1);
    Server server = queue.servers().getFirst();
    Entity currentEntity = new Entity(1);
    Entity waitingEntity1 = new Entity(2);
    Entity waitingEntity2 = new Entity(3);

    server.seize(currentEntity);
    queue.add(waitingEntity1);
    queue.add(waitingEntity2);

    SimulationContext context =
        createContext(new FutureEventList(), model, TestStrategies.defaultStrategyBag());

    Departure departure = new Departure(10.0, currentEntity, server);

    departure.execute(context);

    assertTrue(server.isBusy());
    assertSame(waitingEntity1, server.entity());
    assertEquals(1, queue.size());
    assertSame(waitingEntity2, queue.peek());
  }

  @Test
  void departureShouldUseDepartureQueueSelectionStrategy() {
    QueueingModel model = TwoQueuesTwoServers.create(serviceTimeGenerator, serviceTimeGenerator);
    Queue queue1 = model.queue(1);
    Queue queue2 = model.queue(2);
    Server server1 = queue1.servers().getFirst();
    Entity currentEntity = new Entity(1);
    Entity waitingEntity1 = new Entity(2);
    Entity waitingEntity2 = new Entity(3);

    server1.seize(currentEntity);
    queue1.add(waitingEntity1);
    queue2.add(waitingEntity2);

    QueueSelectionStrategy departureQueueSelectionStrategy =
        queues -> {
          assertEquals(
              List.of(queue1, queue2),
              queues.stream().map(queueView -> model.queue(queueView.id())).toList());
          return 2;
        };

    StrategyBag strategyBag =
        new StrategyBag(
            servers -> servers.getFirst().id(),
            queues -> queues.getFirst().id(),
            departureQueueSelectionStrategy);

    SimulationContext context = createContext(new FutureEventList(), model, strategyBag);

    Departure departure = new Departure(10.0, currentEntity, server1);

    departure.execute(context);

    assertTrue(server1.isBusy());
    assertSame(waitingEntity2, server1.entity());
    assertEquals(1, queue1.size());
    assertSame(waitingEntity1, queue1.peek());
    assertEquals(0, queue2.size());
  }

  @Test
  void departureShouldScheduleNextDepartureWhenEntityStartsService() {
    QueueingModel model = OneQueueOneServer.create(serviceTimeGenerator);
    Queue queue = model.queue(1);
    Server server = queue.servers().getFirst();
    Entity currentEntity = new Entity(1);
    Entity waitingEntity = new Entity(2);

    server.seize(currentEntity);
    queue.add(waitingEntity);

    FutureEventList futureEventList = new FutureEventList();

    SimulationContext context =
        createContext(futureEventList, model, TestStrategies.defaultStrategyBag());

    Departure departure = new Departure(10.0, currentEntity, server);

    departure.execute(context);

    Event event = futureEventList.imminent();

    assertTrue(event instanceof Departure);
    assertEquals(15.0, event.clock());
  }

  @Test
  void departureShouldNotScheduleEventWhenNoEntityIsWaiting() {
    QueueingModel model = OneQueueOneServer.create(serviceTimeGenerator);
    Queue queue = model.queue(1);
    Server server = queue.servers().getFirst();
    Entity currentEntity = new Entity(1);

    server.seize(currentEntity);

    FutureEventList futureEventList = new FutureEventList();

    SimulationContext context =
        createContext(futureEventList, model, TestStrategies.defaultStrategyBag());

    Departure departure = new Departure(10.0, currentEntity, server);

    departure.execute(context);

    assertTrue(futureEventList.isEmpty());
  }

  private SimulationContext createContext(
      FutureEventList futureEventList, QueueingModel model, StrategyBag strategyBag) {

    return new InternalSimulationContext(
        futureEventList, RandomGenerator.getDefault(), model, strategyBag, List.of());
  }
}
