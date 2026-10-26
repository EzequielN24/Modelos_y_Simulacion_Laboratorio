package queueing.sim.strategies;

import java.util.List;
import queueing.sim.ServerView;

/** Una política de selección de servidores que selecciona el primer servidor disponible. */
public final class FirstAvailable implements ServerSelectionStrategy {

  /**
   * Selecciona el primer servidor disponible de la colección.
   *
   * @param servers la colección de servidores disponibles
   * @return el primer servidor disponible
   * @throws IllegalArgumentException si {@code servers} está vacío
   */
  @Override
  public int select(List<ServerView> servers) {
    if (servers.isEmpty()) {
      throw new IllegalArgumentException("No servers available");
    }

    return servers.getFirst().id();
  }
}
