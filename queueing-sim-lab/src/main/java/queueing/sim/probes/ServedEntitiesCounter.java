package queueing.sim.probes;

import queueing.sim.EntityView;
import queueing.sim.ServerView;

/**
 * Una sonda que cuenta el número de entidades que han comenzado a recibir
 * servicio.
 *
 * <p>
 * Se considera que una entidad ha recibido servicio cuando comienza a ser
 * atendida por un servidor,
 * independientemente de si completa su servicio antes de que finalice la
 * simulación.
 */
public final class ServedEntitiesCounter implements Probe {

  private int count;

  /**
   * Registra que una entidad ha comenzado a recibir servicio.
   *
   * @param clock  el tiempo de simulación en el que comenzó el servicio
   * @param entity la entidad que comenzó a recibir servicio
   * @param server el servidor que comenzó a prestar servicio a la entidad
   */
  @Override
  public void serviceStarted(double clock, EntityView entity, ServerView server) {
    this.count++;
  }

  /**
   * Devuelve el número de entidades que han comenzado a recibir servicio.
   *
   * @return el número de entidades atendidas
   */
  public int count() {
    return this.count;
  }
}
