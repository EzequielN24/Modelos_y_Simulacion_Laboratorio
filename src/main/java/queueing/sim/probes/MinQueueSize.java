package queueing.sim.probes;

import java.util.*;
import queueing.sim.QueueView;

/**
 * Una sonda que registra el tamaño mínimo (distinto de cero) alcanzado por cada cola.
 *
 * <p>El tamaño mínimo se actualiza cada vez que una cola notifica un tamaño mayor a cero que sea
 * inferior al mínimo actual registrado.
 */
public final class MinQueueSize implements Probe {

  private final Map<Integer, Integer> tamaño_minimo_cola = new HashMap<>();

  /**
   * Registra el tamaño actual de una cola.
   *
   * <p>Si el tamaño actual es mayor a cero y menor que el mínimo registrado anteriormente, el
   * mínimo se actualiza.
   *
   * @param clock el tiempo de simulación en el que cambió el tamaño de la cola
   * @param queue la cola cuyo tamaño cambió
   */
  @Override
  public void queueSizeChanged(double clock, QueueView queue) {
    int tamaño = queue.size();
    if (tamaño > 0) {
      Integer minActual = this.tamaño_minimo_cola.get(queue.id());
      if (minActual == null || tamaño < minActual) {
        this.tamaño_minimo_cola.put(queue.id(), tamaño);
      }
    }
  }

  /**
   * Devuelve el tamaño mínimo (distinto de cero) alcanzado por la cola especificada.
   *
   * @param queueId el identificador de la cola
   * @return el tamaño mínimo alcanzado mayor a cero, o 0 si la cola nunca tuvo elementos
   */
  public int minQueueSize(int queueId) {
    Integer min = this.tamaño_minimo_cola.get(queueId);
    return min == null ? 0 : min;
  }
}
