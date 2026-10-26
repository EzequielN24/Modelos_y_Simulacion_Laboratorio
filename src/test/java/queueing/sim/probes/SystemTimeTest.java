package queueing.sim.probes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import queueing.sim.EntityView;

class SystemTimeTest {

  @Test
  void shouldAccumulateSystemTime() {
    SystemTime probe = new SystemTime();

    EntityView entity1 = () -> 1L;
    EntityView entity2 = () -> 2L;

    probe.entityArrived(2.0, entity1);
    probe.entityArrived(5.0, entity2);

    probe.entityDeparted(10.0, entity1);
    probe.entityDeparted(13.0, entity2);

    assertEquals(16.0, probe.totalSystemTime());
  }

  @Test
  void shouldStartAtZero() {
    SystemTime probe = new SystemTime();

    assertEquals(0.0, probe.totalSystemTime());
  }

  @Test
  void shouldThrowWhenEntityArrivalWasNotRecorded() {
    SystemTime probe = new SystemTime();

    EntityView entity = () -> 1L;

    assertThrows(IllegalStateException.class, () -> probe.entityDeparted(10.0, entity));
  }

  @Test
  void shouldNotIncludeEntitiesThatHaveNotDeparted() {
    SystemTime probe = new SystemTime();

    EntityView entity1 = () -> 1L;
    EntityView entity2 = () -> 2L;

    probe.entityArrived(2.0, entity1);
    probe.entityArrived(5.0, entity2);

    probe.entityDeparted(10.0, entity1);

    assertEquals(8.0, probe.totalSystemTime());
  }
}
