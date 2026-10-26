package queueing.sim.probes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import queueing.sim.EntityView;

class MaxSystemTimeTest {

  @Test
  void shouldRecordMaximumSystemTime() {
    MaxSystemTime probe = new MaxSystemTime();

    EntityView entity1 = () -> 1L;
    EntityView entity2 = () -> 2L;
    EntityView entity3 = () -> 3L;

    probe.entityArrived(0.0, entity1);
    probe.entityArrived(5.0, entity2);
    probe.entityArrived(10.0, entity3);

    probe.entityDeparted(8.0, entity1);
    probe.entityDeparted(20.0, entity2);
    probe.entityDeparted(15.0, entity3);

    assertEquals(15.0, probe.maxSystemTime());
  }

  @Test
  void shouldStartAtZero() {
    MaxSystemTime probe = new MaxSystemTime();

    assertEquals(0.0, probe.maxSystemTime());
  }

  @Test
  void shouldThrowWhenEntityArrivalWasNotRecorded() {
    MaxSystemTime probe = new MaxSystemTime();

    EntityView entity = () -> 1L;

    assertThrows(IllegalStateException.class, () -> probe.entityDeparted(10.0, entity));
  }

  @Test
  void shouldNotIncludeEntitiesThatHaveNotDeparted() {
    MaxSystemTime probe = new MaxSystemTime();

    EntityView entity1 = () -> 1L;
    EntityView entity2 = () -> 2L;

    probe.entityArrived(0.0, entity1);
    probe.entityArrived(5.0, entity2);

    probe.entityDeparted(10.0, entity1);

    assertEquals(10.0, probe.maxSystemTime());
  }
}
