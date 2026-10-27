package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;
import queueing.sim.EntityView;

/**
 * Una sonda que registra el tiempo que las entidades pasan en el sistema.
 *
 * <p>
 * El tiempo en el sistema se mide desde el momento en que llega una entidad
 * hasta el momento en
 * que sale.
 *
 * <p>
 * La sonda almacena la hora de llegada de cada entidad hasta que esta sale. Una
 * vez que la
 * entidad sale, se calcula y registra su tiempo en el sistema, y ​​su hora de
 * llegada se elimina
 * del mapa interno.
 */
public final class SystemTime implements Probe {

  private Map<EntityView, Double> tiempo_llegada = new HashMap<>();
  private double tiempo_total_sistema = 0;

  /**
   * Registra la hora de llegada de una entidad.
   *
   * @param clock  el tiempo de simulación en el que llegó la entidad
   * @param entity la entidad que llegó
   */
  @Override
  public void entityArrived(double clock, EntityView entity) {
    this.tiempo_llegada.put(entity, clock);
  }

  /**
   * Registra el tiempo que una entidad pasa en el sistema cuando sale.
   *
   * @param clock  el tiempo de simulación en el que salió la entidad
   * @param entity la entidad que salió
   */
  @Override
  public void entityDeparted(double clock, EntityView entity) {
    Double tiempo_llegada_entidad = this.tiempo_llegada.remove(entity);
    if (tiempo_llegada_entidad == null) {
      throw new IllegalStateException("No se registró la llegada para la entidad: " + entity);
    }
    this.tiempo_total_sistema += (clock - tiempo_llegada_entidad);
  }

  /**
   * Devuelve el tiempo total que las entidades han pasado en el sistema.
   *
   * @return el tiempo total que las entidades han pasado en el sistema
   */
  public double totalSystemTime() {
    return this.tiempo_total_sistema;
  }
}
