package queueing.sim.probes;

import java.util.HashMap;
import java.util.Map;
import queueing.sim.EntityView;
import queueing.sim.QueueView;
import queueing.sim.ServerView;

/**
 * Una sonda que registra el tiempo que las entidades pasan esperando en colas.
 *
 * <p>El tiempo de espera se mide desde el momento en que una entidad entra en una cola hasta el
 * momento en que comienza a recibir servicio.
 *
 * <p>La sonda almacena la hora de entrada a la cola de cada entidad en espera. Una vez que la
 * entidad comienza a recibir servicio, se calcula y registra su tiempo de espera, y su hora de
 * entrada a la cola se elimina del mapa interno.
 */
public final class WaitingTime implements Probe {

  private Map<EntityView, Double> tiempo_entrada_cola = new HashMap<>();
  private double tiempo_espera_total = 0;

  /**
   * Registra el momento en que una entidad entra en una cola.
   *
   * @param clock el tiempo de simulación en el que la entidad entró en la cola
   * @param entity la entidad que entró en la cola
   * @param queue la cola en la que entró la entidad
   */
  @Override
  public void entityEnteredQueue(double clock, EntityView entity, QueueView queue) {
    this.tiempo_entrada_cola.put(entity, clock);
  }

  /**
   * Registre el tiempo de espera de una entidad cuando comienza el servicio.
   *
   * @param reloj la hora de simulación en la que la entidad inició el servicio
   * @param entidad la entidad que inició el servicio
   * @param server el servidor que comenzó a servir a la entidad
   */
  @Override
  public void serviceStarted(double clock, EntityView entity, ServerView server) {
    Double tiempo_llegada = this.tiempo_entrada_cola.remove(entity);
    if (tiempo_llegada != null) {
      this.tiempo_espera_total += (clock - tiempo_llegada);
    }
  }

  /**
   * Devuelve el tiempo total de espera.
   *
   * @return el tiempo total de espera
   */
  public double totalWaitingTime() {
    return this.tiempo_espera_total;
  }
}
