package queueing.sim.strategies;

import java.util.List;
import queueing.sim.QueueView;

/** Defines a strategy for selecting a queue. */
@FunctionalInterface
public interface QueueSelectionStrategy {

  /**
   * Selects a queue from the provided queue views.
   *
   * @param queues the queues available for selection
   * @return the identifier of the selected queue
   */
  int select(List<QueueView> queues);
}
