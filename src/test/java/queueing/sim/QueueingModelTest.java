package queueing.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link QueueingModel}. */
class QueueingModelTest {

  @Test
  void queueingModelShouldRejectEmptyQueueConfigList() {
    assertThrows(IllegalArgumentException.class, () -> new QueueingModel(List.of()));
  }

  @Test
  void queueingModelShouldRejectNullQueueConfigList() {
    assertThrows(NullPointerException.class, () -> new QueueingModel(null));
  }

  @Test
  void queueingModelShouldRejectNullQueueConfig() {
    assertThrows(NullPointerException.class, () -> new QueueingModel(List.of((QueueConfig) null)));
  }

  @Test
  void queueingModelShouldCreateConfiguredQueues() {
    QueueingModel model =
        new QueueingModel(
            List.of(
                new QueueConfig(1, List.of(new ServerConfig(10, rg -> 5.0))),
                new QueueConfig(2, List.of(new ServerConfig(20, rg -> 5.0)))));

    assertEquals(2, model.queues().size());
    assertEquals(1, model.queues().get(0).id());
    assertEquals(2, model.queues().get(1).id());
  }

  @Test
  void queueingModelShouldCreateConfiguredServers() {
    QueueingModel model =
        new QueueingModel(
            List.of(
                new QueueConfig(
                    1,
                    List.of(
                        new ServerConfig(10, rg -> 5.0),
                        new ServerConfig(20, rg -> 5.0),
                        new ServerConfig(30, rg -> 5.0)))));

    Queue queue = model.queue(1);

    assertEquals(3, queue.servers().size());
    assertEquals(10, queue.servers().get(0).id());
    assertEquals(20, queue.servers().get(1).id());
    assertEquals(30, queue.servers().get(2).id());
  }

  @Test
  void queueingModelShouldReturnAllAvailableServers() {
    QueueingModel model =
        new QueueingModel(
            List.of(
                new QueueConfig(
                    1, List.of(new ServerConfig(10, rg -> 5.0), new ServerConfig(20, rg -> 5.0))),
                new QueueConfig(
                    2, List.of(new ServerConfig(30, rg -> 5.0), new ServerConfig(40, rg -> 5.0)))));

    List<ServerView> availableServers = model.availableServers();

    assertEquals(4, availableServers.size());
    assertEquals(10, availableServers.get(0).id());
    assertEquals(20, availableServers.get(1).id());
    assertEquals(30, availableServers.get(2).id());
    assertEquals(40, availableServers.get(3).id());
  }

  @Test
  void queueingModelShouldRejectDuplicateServerIds() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new QueueingModel(
                List.of(
                    new QueueConfig(1, List.of(new ServerConfig(10, rg -> 5.0))),
                    new QueueConfig(2, List.of(new ServerConfig(10, rg -> 5.0))))));
  }

  @Test
  void queueingModelShouldRejectDuplicateQueueIds() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new QueueingModel(
                List.of(
                    new QueueConfig(1, List.of(new ServerConfig(10, rg -> 5.0))),
                    new QueueConfig(1, List.of(new ServerConfig(20, rg -> 5.0))))));
  }

  @Test
  void queueingModelShouldReturnQueueById() {
    QueueingModel model =
        new QueueingModel(
            List.of(
                new QueueConfig(1, List.of(new ServerConfig(10, rg -> 5.0))),
                new QueueConfig(2, List.of(new ServerConfig(20, rg -> 5.0)))));

    Queue queue = model.queue(2);

    assertEquals(2, queue.id());
  }

  @Test
  void queueingModelShouldReturnServerById() {
    QueueingModel model =
        new QueueingModel(
            List.of(
                new QueueConfig(1, List.of(new ServerConfig(10, rg -> 5.0))),
                new QueueConfig(2, List.of(new ServerConfig(20, rg -> 5.0)))));

    Server server = model.server(20);

    assertEquals(20, server.id());
  }

  @Test
  void queueingModelShouldReturnAvailableServers() {
    QueueingModel model =
        new QueueingModel(
            List.of(
                new QueueConfig(
                    1, List.of(new ServerConfig(10, rg -> 5.0), new ServerConfig(20, rg -> 5.0)))));

    Server server1 = model.server(10);
    Server server2 = model.server(20);

    server1.seize(new Entity(1));

    List<ServerView> availableServers = model.availableServers();

    assertEquals(1, availableServers.size());
    assertEquals(20, availableServers.getFirst().id());
    assertFalse(server2.isBusy());
  }
}
