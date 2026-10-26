package queueing.sim.testing;

import queueing.sim.strategies.StrategyBag;

public final class TestStrategies {

  private TestStrategies() {}

  public static StrategyBag defaultStrategyBag() {
    return new StrategyBag(
        servers -> servers.getFirst().id(),
        queues -> queues.getFirst().id(),
        queues -> queues.getFirst().id());
  }
}
