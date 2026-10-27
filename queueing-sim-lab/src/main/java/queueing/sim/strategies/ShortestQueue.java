package queueing.sim.strategies;

import java.util.Comparator;
import java.util.List;
import queueing.sim.QueueView;

/**
 * Estrategia de selección de cola que elige la cola con menor número de entidades en espera.
 *
 * <p>En caso de empate en la longitud más corta, selecciona la primera cola en la lista (menor índice/id).
 */
public final class ShortestQueue implements QueueSelectionStrategy {

  @Override
  public int select(List<QueueView> queues) {
    if (queues == null || queues.isEmpty()) {
      throw new IllegalArgumentException("La lista de colas no puede ser nula ni vacía.");
    }
    return queues.stream()
        .min(Comparator.comparingInt(QueueView::size))
        .orElseThrow()
        .id();
  }
}
