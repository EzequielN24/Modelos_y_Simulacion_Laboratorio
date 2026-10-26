package queueing.sim.probes;

import queueing.sim.EntityView;

/**
 * Una sonda que cuenta el número de entidades que llegan durante una simulación.
 *
 * <p>El contador se incrementa cada vez que se notifica a la sonda la llegada de una entidad
 * mediante {@link #entityArrived(double, EntityView)}.
 */
public final class ArrivalCounter implements Probe {

  private int count;

  /**
   * Registra la llegada de una entidad.
   *
   * <p>Se reciben el reloj de simulación y la entidad para cumplir con el contrato de {@link
   * Probe}, pero solo la ocurrencia en sí es relevante para esta sonda.
   *
   * @param clock el tiempo de simulación en el que llegó la entidad
   * @param entity la entidad que llegó
   */
  @Override
  public void entityArrived(double clock, EntityView entity) {
    this.count++;
  }

  /**
   * Devuelve el número de entidades que han llegado.
   *
   * @return el número de llegadas registradas por esta sonda
   */
  public int count() {
    return this.count;
  }
}
