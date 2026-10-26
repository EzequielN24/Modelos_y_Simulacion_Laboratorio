package queueing.sim.probes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import queueing.sim.testing.TestQueue;

class MaxQueueSizeTest {

  @Test
  void shouldRecordMaximumQueueSizePerQueue() {
    MaxQueueSize probe = new MaxQueueSize();

    TestQueue queue1 = new TestQueue(1);
    TestQueue queue2 = new TestQueue(2);

    queue1.size(2);
    probe.queueSizeChanged(1.0, queue1);

    queue1.size(5);
    probe.queueSizeChanged(2.0, queue1);

    queue1.size(3);
    probe.queueSizeChanged(3.0, queue1);

    queue2.size(4);
    probe.queueSizeChanged(4.0, queue2);

    assertEquals(5, probe.maxQueueSize(1));
    assertEquals(4, probe.maxQueueSize(2));
  }

  @Test
  void shouldThrowWhenQueueHasNoRecordedSize() {
    MaxQueueSize probe = new MaxQueueSize();

    assertThrows(IllegalArgumentException.class, () -> probe.maxQueueSize(1));
  }

  @Test
  void shouldNotDecreaseMaximumWhenQueueGetsSmaller() {
    MaxQueueSize probe = new MaxQueueSize();

    TestQueue queue = new TestQueue(1);

    queue.size(7);
    probe.queueSizeChanged(1.0, queue);

    queue.size(3);
    probe.queueSizeChanged(2.0, queue);

    queue.size(5);
    probe.queueSizeChanged(3.0, queue);

    assertEquals(7, probe.maxQueueSize(1));
  }
}
