package queueing.sim.probes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import queueing.sim.EntityView;

class ArrivalCounterTest {

  @Test
  void shouldCountArrivals() {
    ArrivalCounter counter = new ArrivalCounter();

    EntityView entity1 = () -> 1L;
    EntityView entity2 = () -> 2L;
    EntityView entity3 = () -> 3L;

    counter.entityArrived(0.0, entity1);
    counter.entityArrived(5.0, entity2);
    counter.entityArrived(10.0, entity3);

    assertEquals(3, counter.count());
  }

  @Test
  void shouldStartAtZero() {
    ArrivalCounter counter = new ArrivalCounter();

    assertEquals(0, counter.count());
  }
}
