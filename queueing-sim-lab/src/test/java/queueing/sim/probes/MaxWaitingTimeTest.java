package queueing.sim.probes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import queueing.sim.EntityView;
import queueing.sim.QueueView;
import queueing.sim.ServerView;
import queueing.sim.testing.TestQueue;
import queueing.sim.testing.TestServer;

class MaxWaitingTimeTest {

  @Test
  void shouldRecordMaximumWaitingTime() {
    MaxWaitingTime probe = new MaxWaitingTime();

    EntityView entity1 = () -> 1L;
    EntityView entity2 = () -> 2L;
    EntityView entity3 = () -> 3L;

    QueueView queue = new TestQueue(1);
    ServerView server = new TestServer(1);

    probe.entityEnteredQueue(2.0, entity1, queue);
    probe.entityEnteredQueue(5.0, entity2, queue);
    probe.entityEnteredQueue(10.0, entity3, queue);

    probe.serviceStarted(7.0, entity1, server);
    probe.serviceStarted(20.0, entity2, server);
    probe.serviceStarted(15.0, entity3, server);

    assertEquals(15.0, probe.maxWaitingTime());
  }

  @Test
  void shouldStartAtZero() {
    MaxWaitingTime probe = new MaxWaitingTime();

    assertEquals(0.0, probe.maxWaitingTime());
  }

  @Test
  void shouldIgnoreEntitiesThatDidNotWait() {
    MaxWaitingTime probe = new MaxWaitingTime();

    EntityView entity = () -> 1L;
    ServerView server = new TestServer(1);

    probe.serviceStarted(10.0, entity, server);

    assertEquals(0.0, probe.maxWaitingTime());
  }
}
