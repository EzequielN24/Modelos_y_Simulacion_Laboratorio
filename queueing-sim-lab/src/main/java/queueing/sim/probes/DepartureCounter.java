package queueing.sim.probes;

import queueing.sim.EntityView;

/**
 * Una sonda que cuenta el número de entidades que han partido del sistema.
 *
 * <p>Se considera que una entidad ha partido cuando completa su servicio y abandona el sistema.
 */
public final class DepartureCounter implements Probe {

  private int count;

  /**
   * Registra que una entidad ha partido del sistema.
   *
   * @param clock el tiempo de simulación en el que la entidad partió
   * @param entity la entidad que partió
   */
  @Override
  public void entityDeparted(double clock, EntityView entity) {
    this.count++;
  }

  /**
   * Devuelve el número de entidades que han partido del sistema.
   *
   * @return el número de entidades que han partido
   */
  public int count() {
    return this.count;
  }
}
