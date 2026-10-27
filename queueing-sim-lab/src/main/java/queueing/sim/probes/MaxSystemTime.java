package queueing.sim.probes;

import queueing.sim.EntityView;
import java.util.*;

/**
 * Un sensor que registra el tiempo máximo que una entidad permanece en el
 * sistema.
 *
 * <p>
 * El tiempo de permanencia en el sistema se mide desde el momento en que una
 * entidad llega hasta el momento en que sale.
 *
 * <p>
 * El sensor almacena el instante de llegada de cada entidad hasta que esta
 * abandona el sistema. Una vez que la entidad sale, se calcula su tiempo de
 * permanencia en el sistema y se compara con el máximo actual.
 */
public final class MaxSystemTime implements Probe {

  private final java.util.Map<EntityView, Double> tiempo_arribo = new HashMap<>();
  private double tiempo_maximo_sistema = 0.0;

  /**
   * Registra el momento de llegada de una entidad.
   *
   * @param clock  el tiempo de simulación en el que llegó la entidad
   * @param entity la entidad que llegó
   */
  @Override
  public void entityArrived(double clock, EntityView entity) {
    this.tiempo_arribo.put(entity, clock);
  }

  /**
   * Registra el tiempo que una entidad ha permanecido en el sistema al momento de
   * su salida.
   *
   * <p>
   * Si el tiempo calculado de permanencia en el sistema es superior al máximo
   * actual,
   * dicho máximo se actualiza.
   *
   * @param clock  el tiempo de simulación en el que la entidad salió del sistema
   * @param entity la entidad que salió del sistema
   */
  @Override
  public void entityDeparted(double clock, EntityView entity) {
    Double arrbio = this.tiempo_arribo.remove(entity);
    if (arrbio == null) {
      throw new IllegalStateException("Arrival not recorded for entity: " + entity);
    }
    double tiempo_total = clock - arrbio;
    if (tiempo_total > this.tiempo_maximo_sistema) {
      this.tiempo_maximo_sistema = tiempo_total;
    }
  }

  /**
   * Devuelve el tiempo máximo que una entidad que ha salido ha permanecido en el
   * sistema.
   *
   * @return el tiempo máximo en el sistema
   */
  public double maxSystemTime() {
    return this.tiempo_maximo_sistema;
  }
}
