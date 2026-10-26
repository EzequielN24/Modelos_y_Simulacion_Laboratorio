package queueing.sim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Una lista de eventos ordenados por su reloj de simulación y prioridad.
 *
 * <p>El evento inminente es siempre el primero de la lista. Los eventos con tiempos de simulación
 * más bajos se consideran inminentes. Si dos eventos tienen el mismo tiempo de simulación, el que
 * tenga menor prioridad se considera inminente. Si además tienen la misma prioridad, se conserva su
 * orden de inserción.
 */
final class FutureEventList {

  /**
   * Los eventos ordenados cronológicamente por reloj ascendente, prioridad menor, orden de
   * inserción preservado
   */
  private final List<Event> events = new ArrayList<>();

  /**
   * El comparador usado para ordenar los eventos en la Future Event List.
   *
   * <p>Los eventos con tiempos de simulación más bajos se ordenan primero. Para los eventos con el
   * mismo tiempo de simulación, los eventos con valores de prioridad más bajos se ordenan primero.
   */
  private final Comparator<Event> comparator =
      Comparator.comparingDouble(Event::clock).thenComparingInt(Event::priority);

  /** Constructor de la Future Event List con los eventos dados. */
  FutureEventList(Event... events) {
    for (Event event : events) {
      insert(event);
    }
  }

  /** Inserta un evento en la Future Event List. */
  void insert(Event event) {
    Objects.requireNonNull(event, "event cannot be null");
    this.events.add(event);
    this.events.sort(this.comparator); // List.sort es estable (preserva orden de inserción)
  }

  /** Retorna el evento inminente sin eliminarlo de la Future Event List. */
  Event peek() {
    ensureNotEmpty();
    return this.events.get(0);
  }

  /**
   * Remueve y retorna el evento inminete de la FEL Si dos eventos tienen el mismo tiempo, se tiene
   * en cuenta la prioridad, si siguen siendo iguales se tiene en cuenta el orden de insercion.
   */
  Event imminent() {
    ensureNotEmpty();
    return this.events.remove(0);
  }

  /** Devuelve si la lista de eventos futuros no contiene eventos programados. */
  boolean isEmpty() {
    return this.events.isEmpty();
  }

  @Override
  public String toString() {
    return this.events.toString();
  }

  /** Garantiza que la lista de eventos futuros contenga al menos un evento. */
  private void ensureNotEmpty() {
    if (this.events.isEmpty()) {
      throw new IllegalStateException("Future Event List es esta vacia.");
    }
  }
}
