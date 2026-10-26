package queueing.sim.probes;

import java.util.*;
import queueing.sim.ServerView;

/**
 * Una sonda que registra el periodo de inactividad máximo para cada servidor.
 *
 * <p>
 * Un periodo de inactividad comienza cuando un servidor queda inactivo y
 * termina cuando el servidor vuelve a estar ocupado.
 *
 * <p>
 * La sonda almacena el instante en que cada servidor pasó a estar inactivo.
 * Cuando dicho servidor vuelve a estar ocupado,
 * se calcula el tiempo de inactividad transcurrido y se compara con el máximo
 * actual para ese servidor.
 *
 * <p>
 * Cualquier periodo de inactividad que permanezca abierto al finalizar la
 * simulación se cierra utilizando el instante de finalización de la misma.
 */
public final class MaxIdleTime implements Probe {

  private final Map<Integer, Double> tiempo_comienzo_ocio = new HashMap<>();
  private final Map<Integer, Double> ocio_maximo = new HashMap<>();

  /**
   * Registra el momento en el que un servidor queda libre.
   *
   * @param clock  el tiempo de simulación en el que el servidor quedó libre
   * @param server el servidor que quedó libre
   */
  @Override
  public void serverBecameIdle(double clock, ServerView server) {
    this.tiempo_comienzo_ocio.put(server.id(), clock);
    this.ocio_maximo.putIfAbsent(server.id(), 0.0);
  }

  /**
   * Registra el tiempo de inactividad de un servidor en el momento en que este
   * pasa a estar ocupado.
   *
   * <p>
   * Un servidor pasa a estar ocupado cuando realiza la transición de un estado de
   * inactividad a un estado de ocupación.
   * El inicio
   * del servicio para una entidad no implica necesariamente que el servidor haya
   * pasado a estar ocupado,
   * ya que es posible que el servidor
   * ya se encuentre ocupado al comenzar a atender a otra entidad proveniente de
   * una cola.
   *
   * @param clock  el tiempo de simulación en el que el servidor pasó a estar
   *               ocupado
   * @param server el servidor que pasó a estar ocupado
   */
  @Override
  public void serverBecameBusy(double clock, ServerView server) {
    Double startTime = this.tiempo_comienzo_ocio.remove(server.id());
    if (startTime == null) {
      throw new IllegalStateException("Server " + server.id() + " was not recorded as idle.");
    }
    double tiempo_ocio = clock - startTime;
    double ocio_max = this.ocio_maximo.get(server.id());
    if (tiempo_ocio > ocio_max) {
      this.ocio_maximo.put(server.id(), tiempo_ocio);
    }
  }

  /**
   * Cierra todos los periodos de inactividad que permanecen abiertos al finalizar
   * la simulación.
   *
   * @param clock el instante de la simulación en el que esta finalizó
   */
  @Override
  public void simulationFinished(double clock) {
    for (Map.Entry<Integer, Double> entry : new HashMap<>(this.tiempo_comienzo_ocio).entrySet()) {
      int serverId = entry.getKey();
      double tiempo_iniciado = entry.getValue();
      double tiempo_ocio = clock - tiempo_iniciado;
      double ocio_max = this.ocio_maximo.get(serverId);
      if (tiempo_ocio > ocio_max) {
        this.ocio_maximo.put(serverId, tiempo_ocio);
      }
    }
    this.tiempo_comienzo_ocio.clear();
  }

  /**
   * Devuelve el tiempo de inactividad máximo registrado para el servidor
   * especificado.
   *
   * @param serverId el identificador del servidor
   * @return el tiempo de inactividad máximo para el servidor
   * @throws IllegalArgumentException si no se ha registrado ningún periodo de
   *                                  inactividad
   *                                  para el servidor especificado
   */
  public double maxIdleTime(int serverId) {
    Double max = this.ocio_maximo.get(serverId);
    if (max == null) {
      throw new IllegalArgumentException("No idle period recorded for server: " + serverId);
    }
    return max;
  }
}
