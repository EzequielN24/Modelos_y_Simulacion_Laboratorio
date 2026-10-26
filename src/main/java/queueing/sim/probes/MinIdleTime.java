package queueing.sim.probes;

import java.util.*;
import queueing.sim.ServerView;

/**
 * Una sonda que registra el periodo de inactividad mínimo (distinto de cero) para cada servidor.
 *
 * <p>Un periodo de inactividad comienza cuando un servidor queda inactivo y termina cuando el
 * servidor vuelve a estar ocupado. Se ignoran los lapsos iguales a cero.
 *
 * <p>Cualquier periodo de inactividad que permanezca abierto al finalizar la simulación se cierra
 * utilizando el instante de finalización de la misma.
 */
public final class MinIdleTime implements Probe {

  private final Map<Integer, Double> tiempo_comienzo_ocio = new HashMap<>();
  private final Map<Integer, Double> ocio_minimo = new HashMap<>();

  /**
   * Registra el momento en el que un servidor queda libre.
   *
   * @param clock el tiempo de simulación en el que el servidor quedó libre
   * @param server el servidor que quedó libre
   */
  @Override
  public void serverBecameIdle(double clock, ServerView server) {
    this.tiempo_comienzo_ocio.put(server.id(), clock);
  }

  /**
   * Registra el tiempo de inactividad de un servidor en el momento en que este pasa a estar
   * ocupado.
   *
   * @param clock el tiempo de simulación en el que el servidor pasó a estar ocupado
   * @param server el servidor que pasó a estar ocupado
   */
  @Override
  public void serverBecameBusy(double clock, ServerView server) {
    Double startTime = this.tiempo_comienzo_ocio.remove(server.id());
    if (startTime == null) {
      throw new IllegalStateException("Server " + server.id() + " was not recorded as idle.");
    }
    double tiempo_ocio = clock - startTime;
    if (tiempo_ocio > 0.0) {
      Double minActual = this.ocio_minimo.get(server.id());
      if (minActual == null || tiempo_ocio < minActual) {
        this.ocio_minimo.put(server.id(), tiempo_ocio);
      }
    }
  }

  /**
   * Cierra todos los periodos de inactividad que permanecen abiertos al finalizar la simulación.
   *
   * @param clock el instante de la simulación en el que esta finalizó
   */
  @Override
  public void simulationFinished(double clock) {
    for (Map.Entry<Integer, Double> entry : new HashMap<>(this.tiempo_comienzo_ocio).entrySet()) {
      int serverId = entry.getKey();
      double tiempo_iniciado = entry.getValue();
      double tiempo_ocio = clock - tiempo_iniciado;
      if (tiempo_ocio > 0.0) {
        Double minActual = this.ocio_minimo.get(serverId);
        if (minActual == null || tiempo_ocio < minActual) {
          this.ocio_minimo.put(serverId, tiempo_ocio);
        }
      }
    }
    this.tiempo_comienzo_ocio.clear();
  }

  /**
   * Devuelve el tiempo de inactividad mínimo (distinto de cero) registrado para el servidor
   * especificado.
   *
   * @param serverId el identificador del servidor
   * @return el tiempo de inactividad mínimo para el servidor, o 0.0 si no hubo periodos mayores a
   *     cero
   * @throws IllegalArgumentException si el servidor nunca fue registrado
   */
  public double minIdleTime(int serverId) {
    Double min = this.ocio_minimo.get(serverId);
    return min == null ? 0.0 : min;
  }
}
