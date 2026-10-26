package queueing.sim.probes;

import queueing.sim.EntityView;
import queueing.sim.QueueView;
import queueing.sim.ServerView;
import java.util.*;

/**
 * Un sensor que registra el tiempo máximo que una entidad permanece esperando
 * en una cola.
 *
 * <p>
 * El tiempo de espera se mide desde el momento en que una entidad entra en una
 * cola hasta el momento en que
 * comienza a recibir servicio.
 *
 * <p>
 * El sensor almacena el instante de entrada en la cola de cada entidad en
 * espera. Una vez que la entidad comienza a recibir servicio,
 * se calcula su tiempo de espera y se compara con el máximo actual.
 */
public final class MaxWaitingTime implements Probe {

  private final Map<EntityView, Double> tiempo_arribo_cola = new HashMap<>();
  private double timepo_maximo_espera = 0.0;

  /**
   * Registra el momento en el que una entidad entra en una cola.
   *
   * @param clock  el tiempo de simulación en el que la entidad entró en la cola
   * @param entity la entidad que entró en la cola
   * @param queue  la cola en la que entró la entidad
   */
  @Override
  public void entityEnteredQueue(double clock, EntityView entity, QueueView queue) {
    this.tiempo_arribo_cola.put(entity, clock);
  }

  /**
   * Registra el tiempo de espera de una entidad al comenzar el servicio.
   *
   * <p>
   * Si el tiempo de espera calculado es mayor que el máximo actual,
   * se actualiza dicho máximo.
   *
   * <p>
   * Si la entidad no entró en una cola, no se registra ningún tiempo de espera.
   *
   * @param clock  el tiempo de simulación en el que comenzó el servicio
   * @param entity la entidad que comenzó a recibir el servicio
   * @param server el servidor que comenzó a atender a la entidad
   */
  @Override
  public void serviceStarted(double clock, EntityView entity, ServerView server) {
    Double tiempo_arribo = this.tiempo_arribo_cola.remove(entity);
    if (tiempo_arribo != null) {
      double espera = clock - tiempo_arribo;
      if (espera > this.timepo_maximo_espera) {
        this.timepo_maximo_espera = espera;
      }
    }
  }

  /**
   * Devuelve el tiempo de espera máximo registrado.
   *
   * @return el tiempo de espera máximo
   */
  public double maxWaitingTime() {
    return this.timepo_maximo_espera;
  }
}
