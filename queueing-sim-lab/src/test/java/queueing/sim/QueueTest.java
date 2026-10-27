package queueing.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link Queue}. */
class QueueTest {

  @Test
  void queueShouldRejectCreationWithoutServers() {
    assertThrows(IllegalArgumentException.class, () -> new Queue(1, List.of()));
  }

  @Test
  void queueShouldBeEmptyWhenCreated() {
    Queue queue = createQueue(1);

    assertTrue(queue.isEmpty());
    assertEquals(0, queue.size());
  }

  @Test
  void queueShouldAddEntities() {
    Queue queue = createQueue(1);
    Entity e1 = new Entity(1);
    Entity e2 = new Entity(2);
    Entity e3 = new Entity(3);

    queue.add(e1);
    queue.add(e2);
    queue.add(e3);

    assertFalse(queue.isEmpty());
    assertEquals(3, queue.size());
    assertEquals(e1, queue.peek());
  }

  @Test
  void queueShouldRemoveEntitiesInFifoOrder() {
    Queue queue = createQueue(1);
    Entity e1 = new Entity(1);
    Entity e2 = new Entity(2);
    Entity e3 = new Entity(3);

    queue.add(e1);
    queue.add(e2);
    queue.add(e3);

    assertEquals(e1, queue.remove());
    assertEquals(e2, queue.remove());
    assertEquals(e3, queue.remove());
    assertTrue(queue.isEmpty());
    assertEquals(0, queue.size());
  }

  @Test
  void peekShouldNotRemoveEntity() {
    Queue queue = createQueue(1);
    Entity entity = new Entity(1);

    queue.add(entity);

    assertEquals(entity, queue.peek());
    assertEquals(1, queue.size());
    assertFalse(queue.isEmpty());
  }

  @Test
  void availableServersShouldReturnAllIdleServers() {
    Server server1 = new Server(1, rg -> 5.0);
    Server server2 = new Server(2, rg -> 5.0);
    Server server3 = new Server(3, rg -> 5.0);
    Queue queue = new Queue(1, List.of(server1, server2, server3));

    assertEquals(List.of(server1, server2, server3), queue.availableServers());
  }

  @Test
  void availableServersShouldExcludeBusyServers() {
    Server server1 = new Server(1, rg -> 5.0);
    Server server2 = new Server(2, rg -> 5.0);
    Server server3 = new Server(3, rg -> 5.0);

    server2.seize(new Entity(1));

    Queue queue = new Queue(1, List.of(server1, server2, server3));

    assertEquals(List.of(server1, server3), queue.availableServers());
  }

  @Test
  void availableServersShouldReturnEmptyListWhenAllServersAreBusy() {
    Server server1 = new Server(1, rg -> 5.0);
    Server server2 = new Server(2, rg -> 5.0);

    server1.seize(new Entity(1));
    server2.seize(new Entity(2));

    Queue queue = new Queue(1, List.of(server1, server2));

    assertTrue(queue.availableServers().isEmpty());
  }

  @Test
  void queueShouldExposeItsServers() {
    Server server1 = new Server(1, rg -> 5.0);
    Server server2 = new Server(2, rg -> 5.0);
    Queue queue = new Queue(1, List.of(server1, server2));

    assertEquals(List.of(server1, server2), queue.servers());
  }

  @Test
  void queueShouldExposeItsIdThroughQueueView() {
    Queue queue = createQueue(1);
    QueueView queueView = queue;

    assertEquals(1, queueView.id());
  }

  @Test
  void queueViewShouldExposeQueueSize() {
    Queue queue = createQueue(1);

    queue.add(new Entity(1));
    queue.add(new Entity(2));

    QueueView queueView = queue;

    assertEquals(2, queueView.size());
  }

  private Queue createQueue(int... serverIds) {
    List<Server> servers =
        Arrays.stream(serverIds).mapToObj(id -> new Server(id, rg -> 5.0)).toList();

    return new Queue(1, servers);
  }
}
