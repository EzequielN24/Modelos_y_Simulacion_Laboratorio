package queueing.sim.probes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import queueing.sim.EntityView;

class DepartureCounterTest {

  @Test
  void shouldCountDepartedEntities() {
    DepartureCounter counter = new DepartureCounter();

    EntityView entity1 = () -> 1L;
    EntityView entity2 = () -> 2L;
    EntityView entity3 = () -> 3L;

    counter.entityDeparted(10.0, entity1);
    counter.entityDeparted(15.0, entity2);
    counter.entityDeparted(20.0, entity3);

    assertEquals(3, counter.count());
  }

  @Test
  void shouldStartAtZero() {
    DepartureCounter counter = new DepartureCounter();

    assertEquals(0, counter.count());
  }
}
