package queueing.sim.testing;

import java.util.List;
import queueing.sim.QueueConfig;
import queueing.sim.QueueingModel;
import queueing.sim.ServerConfig;
import queueing.sim.TimeGenerator;

public final class TwoQueuesTwoServers {

  private TwoQueuesTwoServers() {}

  public static QueueingModel create(
      TimeGenerator serviceTimeGenerator1, TimeGenerator serviceTimeGenerator2) {
    return new QueueingModel(
        List.of(
            new QueueConfig(1, List.of(new ServerConfig(1, serviceTimeGenerator1))),
            new QueueConfig(2, List.of(new ServerConfig(2, serviceTimeGenerator2)))));
  }
}
