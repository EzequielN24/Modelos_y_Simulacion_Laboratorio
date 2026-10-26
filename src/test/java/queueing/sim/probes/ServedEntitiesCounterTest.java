package queueing.sim.probes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import queueing.sim.EntityView;
import queueing.sim.ServerView;
import queueing.sim.testing.TestServer;

class ServedEntitiesCounterTest {

  @Test
  void shouldCountServedEntities() {
    ServedEntitiesCounter counter = new ServedEntitiesCounter();

    EntityView entity1 = () -> 1L;
    EntityView entity2 = () -> 2L;

    ServerView server = new TestServer(1);

    counter.serviceStarted(1.0, entity1, server);
    counter.serviceStarted(5.0, entity2, server);

    assertEquals(2, counter.count());
  }

  @Test
  void shouldStartAtZero() {
    ServedEntitiesCounter counter = new ServedEntitiesCounter();

    assertEquals(0, counter.count());
  }
}
