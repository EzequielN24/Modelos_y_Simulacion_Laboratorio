package queueing.sim.probes;

import java.util.*;
import queueing.sim.EntityView;
import queueing.sim.QueueView;
import queueing.sim.ServerView;

/**
 * Un sensor que registra el tiempo mínimo (distinto de cero) que una entidad permanece esperando
 * en una cola.
 *
 * <p>El tiempo de espera se mide desde el momento en que una entidad entra en una cola hasta el
 * momento en que comienza a recibir servicio. Se ignoran los tiempos de espera iguales a cero.
 */
public final class MinWaitingTime implements Probe {

  private final Map<EntityView, Double> tiempo_arribo_cola = new HashMap<>();
  private double tiempo_minimo_espera = Double.POSITIVE_INFINITY;

  /**
   * Registra el momento en el que una entidad entra en una cola.
   *
   * @param clock el tiempo de simulación en el que la entidad entró en la cola
   * @param entity la entidad que entró en la cola
   * @param queue la cola en la que entró la entidad
   */
  @Override
  public void entityEnteredQueue(double clock, EntityView entity, QueueView queue) {
    this.tiempo_arribo_cola.put(entity, clock);
  }

  /**
   * Registra el tiempo de espera de una entidad al comenzar el servicio.
   *
   * <p>Si el tiempo de espera calculado es mayor a cero y menor que el mínimo actual, se actualiza
   * dicho mínimo.
   *
   * @param clock el tiempo de simulación en el que comenzó el servicio
   * @param entity la entidad que comenzó a recibir el servicio
   * @param server el servidor que comenzó a atender a la entidad
   */
  @Override
  public void serviceStarted(double clock, EntityView entity, ServerView server) {
    Double tiempo_arribo = this.tiempo_arribo_cola.remove(entity);
    if (tiempo_arribo != null) {
      double espera = clock - tiempo_arribo;
      if (espera > 0.0 && espera < this.tiempo_minimo_espera) {
        this.tiempo_minimo_espera = espera;
      }
    }
  }

  /**
   * Devuelve el tiempo de espera mínimo (distinto de cero) registrado.
   *
   * @return el tiempo de espera mínimo, o 0.0 si ninguna entidad esperó más de cero
   */
  public double minWaitingTime() {
    return Double.isInfinite(this.tiempo_minimo_espera) ? 0.0 : this.tiempo_minimo_espera;
  }
}
