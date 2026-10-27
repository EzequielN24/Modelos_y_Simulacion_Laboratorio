package queueing.sim.testing;

import java.util.List;
import queueing.sim.QueueConfig;
import queueing.sim.QueueingModel;
import queueing.sim.ServerConfig;
import queueing.sim.TimeGenerator;

public final class OneQueueOneServer {

  private OneQueueOneServer() {}

  public static QueueingModel create(TimeGenerator serviceTimeGenerator) {
    return new QueueingModel(
        List.of(new QueueConfig(1, List.of(new ServerConfig(1, serviceTimeGenerator)))));
  }
}
