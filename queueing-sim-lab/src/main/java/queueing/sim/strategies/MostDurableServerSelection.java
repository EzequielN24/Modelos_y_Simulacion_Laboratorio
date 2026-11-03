package queueing.sim.strategies;

import java.util.List;
import java.util.function.IntToDoubleFunction;
import queueing.sim.ServerView;

/**
 * Política de selección de pista/servidor que prioriza la pista disponible con mayor durabilidad
 * restante (wear-leveling / balanceo de desgaste).
 *
 * <p>En caso de empate en durabilidad, selecciona la de menor identificador.
 */
public final class MostDurableServerSelection implements ServerSelectionStrategy {

  private final IntToDoubleFunction durabilitySupplier;

  /**
   * Crea una estrategia de selección por mayor durabilidad restante.
   *
   * @param durabilitySupplier función que dado el id de un servidor devuelve su durabilidad actual
   */
  public MostDurableServerSelection(IntToDoubleFunction durabilitySupplier) {
    this.durabilitySupplier = durabilitySupplier;
  }

  @Override
  public int select(List<ServerView> servers) {
    if (servers.isEmpty()) {
      throw new IllegalArgumentException("No servers available");
    }

    ServerView bestServer = servers.getFirst();
    double bestDurability = this.durabilitySupplier.applyAsDouble(bestServer.id());

    for (int i = 1; i < servers.size(); i++) {
      ServerView candidate = servers.get(i);
      double candidateDurability = this.durabilitySupplier.applyAsDouble(candidate.id());
      if (candidateDurability > bestDurability) {
        bestServer = candidate;
        bestDurability = candidateDurability;
      }
    }

    return bestServer.id();
  }
}

