package queueing.sim.probes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import queueing.sim.ServerView;
import queueing.sim.testing.TestServer;

class MaxIdleTimeTest {

  @Test
  void shouldRecordMaximumIdleTimePerServer() {
    MaxIdleTime probe = new MaxIdleTime();

    ServerView server1 = new TestServer(1);
    ServerView server2 = new TestServer(2);

    probe.serverBecameIdle(5.0, server1);
    probe.serverBecameBusy(10.0, server1);

    probe.serverBecameIdle(20.0, server1);
    probe.serverBecameBusy(23.0, server1);

    probe.serverBecameIdle(2.0, server2);
    probe.serverBecameBusy(12.0, server2);

    assertEquals(5.0, probe.maxIdleTime(1));
    assertEquals(10.0, probe.maxIdleTime(2));
  }

  @Test
  void shouldThrowWhenServerBecomesBusyWithoutBeingIdle() {
    MaxIdleTime probe = new MaxIdleTime();

    ServerView server = new TestServer(1);

    assertThrows(IllegalStateException.class, () -> probe.serverBecameBusy(10.0, server));
  }

  @Test
  void shouldThrowWhenServerHasNoRecordedIdleTime() {
    MaxIdleTime probe = new MaxIdleTime();

    assertThrows(IllegalArgumentException.class, () -> probe.maxIdleTime(99));
  }
}
