package queueing.sim.probes;

import java.util.*;
import queueing.sim.EntityView;

/**
 * Un sensor que registra el tiempo mínimo (distinto de cero) que una entidad permanece en el
 * sistema.
 *
 * <p>El tiempo de permanencia en el sistema se mide desde el momento en que una entidad llega hasta
 * el momento en que sale. Se ignoran las duraciones iguales a cero.
 */
public final class MinSystemTime implements Probe {

  private final Map<EntityView, Double> tiempo_arribo = new HashMap<>();
  private double tiempo_minimo_sistema = Double.POSITIVE_INFINITY;

  /**
   * Registra el momento de llegada de una entidad.
   *
   * @param clock el tiempo de simulación en el que llegó la entidad
   * @param entity la entidad que llegó
   */
  @Override
  public void entityArrived(double clock, EntityView entity) {
    this.tiempo_arribo.put(entity, clock);
  }

  /**
   * Registra el tiempo que una entidad ha permanecido en el sistema al momento de su salida.
   *
   * <p>Si el tiempo calculado es mayor a cero y menor al mínimo actual, dicho mínimo se actualiza.
   *
   * @param clock el tiempo de simulación en el que la entidad salió del sistema
   * @param entity la entidad que salió del sistema
   */
  @Override
  public void entityDeparted(double clock, EntityView entity) {
    Double arribo = this.tiempo_arribo.remove(entity);
    if (arribo == null) {
      throw new IllegalStateException("Arrival not recorded for entity: " + entity);
    }
    double tiempo_total = clock - arribo;
    if (tiempo_total > 0.0 && tiempo_total < this.tiempo_minimo_sistema) {
      this.tiempo_minimo_sistema = tiempo_total;
    }
  }

  /**
   * Devuelve el tiempo mínimo (distinto de cero) que una entidad ha permanecido en el sistema.
   *
   * @return el tiempo mínimo en el sistema, o 0.0 si no hubo registros mayores a cero
   */
  public double minSystemTime() {
    return Double.isInfinite(this.tiempo_minimo_sistema) ? 0.0 : this.tiempo_minimo_sistema;
  }
}
