package queueing.sim.probes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import queueing.sim.EntityView;
import queueing.sim.QueueView;
import queueing.sim.ServerView;
import queueing.sim.testing.TestQueue;
import queueing.sim.testing.TestServer;

class WaitingTimeTest {

  @Test
  void shouldAccumulateWaitingTime() {

    WaitingTime probe = new WaitingTime();

    EntityView entity1 = () -> 1L;
    EntityView entity2 = () -> 2L;

    QueueView queue = new TestQueue(1);

    ServerView server = new TestServer(1);

    probe.entityEnteredQueue(2.0, entity1, queue);
    probe.entityEnteredQueue(5.0, entity2, queue);

    probe.serviceStarted(10.0, entity1, server);
    probe.serviceStarted(16.0, entity2, server);

    assertEquals(19.0, probe.totalWaitingTime());
  }

  @Test
  void shouldStartAtZero() {
    WaitingTime probe = new WaitingTime();

    assertEquals(0.0, probe.totalWaitingTime());
  }

  @Test
  void shouldIgnoreEntitiesThatDidNotWait() {
    WaitingTime probe = new WaitingTime();

    EntityView entity = () -> 1L;
    ServerView server = new TestServer(1);

    probe.serviceStarted(10.0, entity, server);

    assertEquals(0.0, probe.totalWaitingTime());
  }
}
