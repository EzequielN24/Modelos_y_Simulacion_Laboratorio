package queueing.sim.probes;

import queueing.sim.QueueView;
import java.util.*;

/**
 * Una sonda que registra el tamaño máximo alcanzado por cada cola.
 *
 * <p>
 * El tamaño máximo se actualiza cada vez que una cola notifica un cambio en su
 * tamaño. La sonda almacena
 * el tamaño máximo de forma independiente para cada cola.
 */
public final class MaxQueueSize implements Probe {

  private final Map<Integer, Integer> tamaño_maximo_cola = new HashMap<>();

  /**
   * Registra el tamaño actual de una cola.
   *
   * <p>
   * Si el tamaño actual es mayor que el máximo registrado anteriormente,
   * el máximo se actualiza.
   *
   * @param clock el tiempo de simulación en el que cambió el tamaño de la cola
   * @param queue la cola cuyo tamaño cambió
   */
  @Override
  public void queueSizeChanged(double clock, QueueView queue) {
    int tamaño = queue.size();
    Integer maximo_actual = this.tamaño_maximo_cola.get(queue.id());
    if (maximo_actual == null || tamaño > maximo_actual) {
      this.tamaño_maximo_cola.put(queue.id(), tamaño);
    }
  }

  /**
   * Devuelve el tamaño máximo registrado para la cola especificada.
   *
   * @param queueId el identificador de la cola
   * @return el tamaño máximo alcanzado por la cola
   * @throws IllegalArgumentException si no se ha registrado ningún cambio de
   *                                  tamaño
   *                                  para la cola especificada
   */
  public int maxQueueSize(int queueId) {
    Integer max = this.tamaño_maximo_cola.get(queueId);
    if (max == null) {
      throw new IllegalArgumentException("No size change recorded for queue: " + queueId);
    }
    return max;
  }
}
