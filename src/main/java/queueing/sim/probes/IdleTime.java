package queueing.sim.probes;

import queueing.sim.ServerView;
import java.util.*;

/**
 * Una sonda que registra los períodos durante los cuales los servidores
 * permanecen inactivos.
 *
 * <p>
 * Un período de inactividad comienza cuando un servidor pasa a estar inactivo y
 * finaliza cuando
 * el servidor vuelve a estar ocupado.
 *
 * <p>
 * Se espera que todos los servidores se reporten como inactivos al inicio de la
 * simulación.
 * Cualquier período de inactividad que permanezca abierto al finalizar la
 * simulación se cierra
 * utilizando la hora de finalización de la misma.
 */
public final class IdleTime implements Probe {

  private final Map<Integer, Double> tiempo_comienzo_ocio = new HashMap<>();
  private final Map<Integer, Double> tiempo_total_ocio = new HashMap<>();

  /**
   * Registra el momento en que un servidor queda inactivo.
   *
   * @param clock  el tiempo de simulación en el que el servidor quedó inactivo
   * @param server el servidor que quedó inactivo
   */
  @Override
  public void serverBecameIdle(double clock, ServerView server) {
    this.tiempo_comienzo_ocio.put(server.id(), clock);
    this.tiempo_total_ocio.putIfAbsent(server.id(), 0.0);
  }

  /**
   * Registra el tiempo de inactividad de un servidor cuando este pasa a estar
   * ocupado.
   *
   * <p>
   * Un servidor pasa a estar ocupado cuando transita de un estado de inactividad
   * a un estado de
   * ocupación. Iniciar el servicio para una entidad no significa necesariamente
   * que el servidor
   * haya pasado a estar ocupado, ya que un servidor puede estar ocupado
   * previamente al comenzar a
   * atender a otra entidad de una cola.
   *
   * @param clock  el tiempo de simulación en el que el servidor pasó a estar
   *               ocupado
   * @param server el servidor que pasó a estar ocupado
   */
  @Override
  public void serverBecameBusy(double clock, ServerView server) {
    Double tiempo_iniciado = this.tiempo_comienzo_ocio.remove(server.id());
    if (tiempo_iniciado == null) {
      throw new IllegalStateException(
          "El servidor " + server.id() + " no estaba registrado como inactivo.");
    }
    double tiempo_ocio = clock - tiempo_iniciado;
    this.tiempo_total_ocio.put(server.id(), this.tiempo_total_ocio.get(server.id()) + tiempo_ocio);
  }

  /**
   * Cierra todos los períodos de inactividad que permanecen abiertos al finalizar
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
      this.tiempo_total_ocio.put(serverId, this.tiempo_total_ocio.get(serverId) + tiempo_ocio);
    }
    this.tiempo_comienzo_ocio.clear();
  }

  /**
   * Devuelve el tiempo total de inactividad del servidor especificado.
   *
   * @param serverId el identificador del servidor
   * @return el tiempo total de inactividad del servidor
   * @throws IllegalArgumentException si no se registró tiempo de inactividad para
   *                                  el servidor
   */
  public double totalIdleTime(int serverId) {
    Double total = this.tiempo_total_ocio.get(serverId);
    if (total == null) {
      throw new IllegalArgumentException(
          "No se registró tiempo de inactividad para el servidor: " + serverId);
    }
    return total;
  }
}
