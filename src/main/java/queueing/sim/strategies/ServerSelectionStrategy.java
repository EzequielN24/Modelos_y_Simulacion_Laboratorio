package queueing.sim.strategies;

import java.util.List;
import queueing.sim.ServerView;

/**
 * Defines a policy for selecting a server from a collection of available servers.
 *
 * <p>The given collection must contain at least one server, and every server in the collection must
 * be available.
 */
public interface ServerSelectionStrategy {

  /**
   * Selects one server from the given available servers.
   *
   * @param servers the available servers
   * @return the selected server
   * @throws IllegalArgumentException if {@code servers} is empty
   */
  int select(List<ServerView> servers);
}
