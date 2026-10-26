package queueing.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import queueing.sim.strategies.QueueSelectionStrategy;
import queueing.sim.testing.TestQueue;

/** Unit tests for queue selection strategies. */
class QueueSelectionStrategyTest {

  @Test
  void shortestQueueStrategyShouldSelectQueueWithSmallestSize() {
    TestQueue queue1 = new TestQueue(1);
    TestQueue queue2 = new TestQueue(2);
    TestQueue queue3 = new TestQueue(3);

    queue1.size(2);
    queue3.size(1);

    QueueSelectionStrategy shortestQueue =
        queues -> queues.stream().min(Comparator.comparingInt(QueueView::size)).orElseThrow().id();

    int selectedQueueId = shortestQueue.select(List.of(queue1, queue2, queue3));

    assertEquals(2, selectedQueueId);
  }
}
