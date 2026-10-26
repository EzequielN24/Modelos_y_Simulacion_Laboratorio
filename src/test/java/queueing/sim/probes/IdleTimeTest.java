package queueing.sim.probes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import queueing.sim.ServerView;
import queueing.sim.testing.TestServer;

class IdleTimeTest {

  @Test
  void shouldAccumulateIdleTimePerServer() {
    IdleTime probe = new IdleTime();

    ServerView server1 = new TestServer(1);
    ServerView server2 = new TestServer(2);

    probe.serverBecameIdle(5.0, server1);
    probe.serverBecameBusy(10.0, server1);

    probe.serverBecameIdle(3.0, server2);
    probe.serverBecameBusy(8.0, server2);

    probe.serverBecameIdle(15.0, server1);
    probe.serverBecameBusy(18.0, server1);

    assertEquals(8.0, probe.totalIdleTime(1));
    assertEquals(5.0, probe.totalIdleTime(2));
  }

  @Test
  void shouldThrowWhenServerBecomesBusyWithoutBeingIdle() {
    IdleTime probe = new IdleTime();

    ServerView server = new TestServer(1);

    assertThrows(IllegalStateException.class, () -> probe.serverBecameBusy(10.0, server));
  }

  @Test
  void shouldThrowWhenServerHasNoRecordedIdleTime() {
    IdleTime probe = new IdleTime();

    assertThrows(IllegalArgumentException.class, () -> probe.totalIdleTime(99));
  }
}
